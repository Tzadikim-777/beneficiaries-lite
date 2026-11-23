package mx.com.empresa.backend.domain.exception;

public class DuplicateClabeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DuplicateClabeException() {
        super("La CLABE ya está registrada para este cliente");
    }
}
