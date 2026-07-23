package br.edu.com.matricula.util;

public final class SearchUtils {

    private SearchUtils() {
    }

    /** Null ou blank → null (sem filtro). Caso contrário, trim. */
    public static String normalize(String q) {
        if (q == null) {
            return null;
        }
        String trimmed = q.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
