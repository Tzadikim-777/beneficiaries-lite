package mx.com.empresa.backend.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.jboss.logging.Logger;

import org.hibernate.exception.ConstraintViolationException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import mx.com.empresa.backend.domain.exception.BeneficiaryAlreadyInactiveException;
import mx.com.empresa.backend.domain.exception.BeneficiaryLimitExceededException;
import mx.com.empresa.backend.domain.exception.BeneficiaryNotFoundException;
import mx.com.empresa.backend.domain.exception.DuplicateClabeException;
import mx.com.empresa.backend.domain.exception.InvalidClabeException;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

	private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

	@Override
	public Response toResponse(Exception exception) {

		int status;
		String code;
		String message = exception.getMessage();

		if (exception instanceof InvalidClabeException) {
			status = 400;
			code = "BEN-400";

		} else if (exception instanceof BeneficiaryNotFoundException) {
			status = 404;
			code = "BEN-404";
		} else if (exception instanceof DuplicateClabeException
				|| exception instanceof BeneficiaryAlreadyInactiveException || isDuplicateClabeConstraint(exception)) {

			status = 409;
			code = "BEN-409";

			if (isDuplicateClabeConstraint(exception)) {
				message = "La CLABE ya está registrada para este cliente";
			}
		} else if (exception instanceof BeneficiaryLimitExceededException) {
			status = 422;
			code = "BEN-422";

		} else {
			status = 500;
			code = "BEN-500";
			message = "Error interno del servidor";

			LOG.error("Error inesperado al procesar la solicitud", exception);
		}

		ErrorResponse error = new ErrorResponse(code, message, LocalDateTime.now(), List.of());

		return Response.status(status).entity(error).build();
	}

	private boolean isDuplicateClabeConstraint(Exception exception) {

		Throwable cause = exception;

		while (cause != null) {

			if (cause instanceof ConstraintViolationException violation) {

				String constraintName = violation.getConstraintName();

				if (constraintName != null && constraintName.toLowerCase(java.util.Locale.ROOT)
						.contains("uk_beneficiary_customer_clabe")) {

					return true;
				}
			}

			cause = cause.getCause();
		}

		return false;
	}

}
