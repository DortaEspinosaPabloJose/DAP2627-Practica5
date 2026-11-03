package media.fetcher.yt;

import media.fetcher.MediaFetchException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoutubeSearch {
    private static final Logger logger = Logger.getLogger("YoutubeSearch");
    private static final int DEFAULT_LIMIT = 10;
    private static final String PREFIX = "RESULT\t";

    private YoutubeSearch() {}

    public static List<YoutubeSearchResult> query(String query) {
        return query(query, DEFAULT_LIMIT);
    }

    public static List<YoutubeSearchResult> query(String query, int limit) {
        Objects.requireNonNull(query, "query no puede ser null");
        if (query.isBlank()) {
            throw new IllegalArgumentException("query no puede estar vacía");
        }
        if (limit < 1) {
            throw new IllegalArgumentException("limit debe ser >= 1");
        }

        List<String> cmd = List.of(
                "yt-dlp",
                "--flat-playlist",       // don't resolve each video, much faster
                "--no-warnings",
                "--print", PREFIX + "%(id)s\t%(title)s\t%(channel)s\t%(duration_string)s",
                "ytsearch" + limit + ":" + query);

        logger.log(Level.INFO, "Running: " + String.join(" ", cmd));

        try {
            // Merged streams avoid the stdout/stderr deadlock; non-RESULT lines are kept as error text.
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();

            List<YoutubeSearchResult> results = new ArrayList<>();
            StringBuilder other = new StringBuilder();

            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) {
                    if (line.startsWith(PREFIX)) {
                        YoutubeSearchResult res = parse(line.substring(PREFIX.length()));
                        if (res != null) {
                            results.add(res);
                        }
                    } else {
                        other.append(line).append('\n');
                    }
                }
            }

            int code = p.waitFor();
            if (code != 0) {
                throw new MediaFetchException("yt-dlp falló (código " + code + "): " + other);
            }
            return results;

        } catch (IOException e) {
            throw new MediaFetchException("No se pudo ejecutar yt-dlp (¿está instalado y en el PATH?)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MediaFetchException("Búsqueda interrumpida", e);
        }
    }

    private static YoutubeSearchResult parse(String s) {
        String[] f = s.split("\t", -1);
        if (f.length < 4 || f[0].isEmpty()) {
            return null;
        }
        return new YoutubeSearchResult(f[0], f[1], naToNull(f[2]), naToNull(f[3]));
    }

    private static String naToNull(String v) {
        return (v.isEmpty() || v.equals("NA")) ? null : v;
    }
}