package mx.com.empresa.backend.application.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class ClabeMaskerTest {

    @Test
    void shouldMaskClabeKeepingLastFourDigits() {

        String clabe = "032180000118359719";

        String masked = ClabeMasker.mask(clabe);

        assertEquals("**************9719", masked);
        assertFalse(masked.contains(clabe));
    }

    @Test
    void shouldHandleNullClabe() {

        String masked = ClabeMasker.mask(null);

        assertEquals("****", masked);
    }

    @Test
    void shouldHandleShortClabe() {

        String masked = ClabeMasker.mask("123");

        assertEquals("****", masked);
    }

    @Test
    void shouldHandleFourDigitClabe() {

        String masked = ClabeMasker.mask("1234");

        assertEquals("****", masked);
    }

    @Test
    void shouldMaskAllButLastFourDigits() {

        String masked = ClabeMasker.mask("1234567890");

        assertEquals("******7890", masked);
    }
}
