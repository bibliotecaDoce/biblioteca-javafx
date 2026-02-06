package util;

public final class Validations {

    private Validations() {}

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isIsbn13(String isbn) {
        if (isBlank(isbn)) return false;
        return isbn.matches("^\\ d{13}$");
    }
}
