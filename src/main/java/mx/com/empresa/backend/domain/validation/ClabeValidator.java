package mx.com.empresa.backend.domain.validation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClabeValidator {

    private static final int CLABE_LENGTH = 18;
    private static final int[] WEIGHTS = {3, 7, 1};

    public boolean isValid(String clabe) {

        if (clabe == null || !clabe.matches("\\d{18}")) {
            return false;
        }

        int sum = 0;

        for (int i = 0; i < CLABE_LENGTH - 1; i++) {

            int digit = Character.digit(clabe.charAt(i), 10);

            int weight = WEIGHTS[i % WEIGHTS.length];

            sum += (digit * weight) % 10;
        }

        int checkDigit = (10 - (sum % 10)) % 10;

        int lastDigit = Character.digit(clabe.charAt(17), 10);

        return checkDigit == lastDigit;
    }
}
