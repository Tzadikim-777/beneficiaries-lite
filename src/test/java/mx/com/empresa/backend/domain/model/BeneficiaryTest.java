package mx.com.empresa.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import mx.com.empresa.backend.domain.exception.InvalidClabeException;

class BeneficiaryTest {

    @Test
    void shouldRejectInvalidClabeWhenCreatingBeneficiary() {

        String invalidClabe = "032180000118359718";

        InvalidClabeException exception = assertThrows(
                InvalidClabeException.class,
                () -> new Beneficiary(
                        "12345678",
                        "Proveedor prueba",
                        "Empresa SA de CV",
                        invalidClabe,
                        new BigDecimal("5000.00"),
                        "proveedor@ejemplo.com"
                )
        );

        assertEquals("CLABE inválida", exception.getMessage());
    }
}
