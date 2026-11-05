package media.searcher;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizer {
    private TextNormalizer() {}

    /** sin caracteres especiales */
    public static String words(String s) {
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")          // strip accents
                .toLowerCase(Locale.ROOT);
        return n.replaceAll("[^a-z0-9]+", " ").trim();
    }

    /** sin separadores */
    public static String compact(String s) {
        return words(s).replace(" ", "");
    }

    /** regex */
    public static boolean matchesAll(String text, String query) {
        String haystack = words(text);
        String q = words(query);
        if (q.isEmpty()) return false;
        for (String token : q.split(" ")) {
            if (!haystack.contains(token)) return false;
        }
        return true;
    }
}