package mx.com.empresa.backend.domain.exception;

public class InvalidClabeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidClabeException() {
        super("CLABE inválida");
    }
}
