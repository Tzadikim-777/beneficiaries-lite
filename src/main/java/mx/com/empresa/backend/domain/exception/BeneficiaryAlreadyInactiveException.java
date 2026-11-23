package mx.com.empresa.backend.domain.exception;

public class BeneficiaryAlreadyInactiveException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BeneficiaryAlreadyInactiveException() {
        super("El beneficiario ya se encuentra inactivo");
    }
}
