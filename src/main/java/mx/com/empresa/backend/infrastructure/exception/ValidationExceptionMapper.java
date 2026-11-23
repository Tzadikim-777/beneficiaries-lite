package mx.com.empresa.backend.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(ConstraintViolationException exception) {

		List<String> details = exception.getConstraintViolations().stream().map(this::formatViolation).sorted()
				.toList();

		ErrorResponse error = new ErrorResponse("BEN-400", "Error de validación", LocalDateTime.now(), details);

		return Response.status(Response.Status.BAD_REQUEST).entity(error).type("application/json").build();
	}

	private String formatViolation(ConstraintViolation<?> violation) {

		String path = violation.getPropertyPath().toString();

		String field = path.substring(path.lastIndexOf('.') + 1);

		return field + ": " + violation.getMessage();
	}
}
