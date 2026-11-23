package mx.com.empresa.backend.domain.exception;

public class BeneficiaryLimitExceededException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BeneficiaryLimitExceededException() {
        super("El cliente ha alcanzado el límite de 10 beneficiarios activos");
    }
}
