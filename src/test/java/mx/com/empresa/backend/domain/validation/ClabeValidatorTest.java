package mx.com.empresa.backend.domain.validation;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClabeValidatorTest {

    private ClabeValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ClabeValidator();
    }

    @Test
    void shouldAcceptValidClabe() {
        String clabe = "032180000118359719";

        assertTrue(validator.isValid(clabe));
    }

    @Test
    void shouldRejectIncorrectCheckDigit() {
        String clabe = "032180000118359718";

        assertFalse(validator.isValid(clabe));
    }

    @Test
    void shouldRejectIncorrectLength() {
        String clabe = "03218000011835971";

        assertFalse(validator.isValid(clabe));
    }

    @Test
    void shouldRejectNonNumericCharacters() {
        String clabe = "03218000011835971A";

        assertFalse(validator.isValid(clabe));
    }

    @Test
    void shouldRejectNullClabe() {
        assertFalse(validator.isValid(null));
    }
}
