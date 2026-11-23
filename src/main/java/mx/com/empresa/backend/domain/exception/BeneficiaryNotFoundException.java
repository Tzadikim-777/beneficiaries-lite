package mx.com.empresa.backend.domain.exception;

public class BeneficiaryNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BeneficiaryNotFoundException(Long id) {
        super("No existe un beneficiario con ID: " + id);
    }
}
