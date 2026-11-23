package mx.com.empresa.backend.application.util;

public final class ClabeMasker {

    private static final int VISIBLE_DIGITS = 4;

    private ClabeMasker() {
    }

    public static String mask(String clabe) {

        if (clabe == null || clabe.length() <= VISIBLE_DIGITS) {
            return "****";
        }

        int hiddenLength = clabe.length() - VISIBLE_DIGITS;

        return "*".repeat(hiddenLength)
                + clabe.substring(hiddenLength);
    }
}
