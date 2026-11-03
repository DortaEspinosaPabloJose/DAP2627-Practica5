package media.fetcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoutubeMediaFetcher implements MediaFetcher {
    private final String url;
    private final File destinationDir;
    private final Logger logger = Logger.getLogger("YoutubeFetcher");

    public YoutubeMediaFetcher(String url, String destinationDir) {
        this.url = Objects.requireNonNull(url, "url no puede ser null");
        this.destinationDir = new File(Objects.requireNonNull(destinationDir, "destinationDir no puede ser null"));
    }

    @Override
    public File getMedia() {
        if (!destinationDir.exists() && !destinationDir.mkdirs()) {
            throw new MediaFetchException("No se pudo crear el directorio: " + destinationDir.getAbsolutePath());
        }

        List<String> cmd = List.of(
                "yt-dlp",
                "-x", "--audio-format", "mp3",          // needs ffmpeg installed
                "--no-playlist",
                "-o", new File(destinationDir, "%(title)s.%(ext)s").getAbsolutePath(),
                "--print", "after_move:filepath",        // prints final path to stdout
                "--no-simulate",
                url);

        logger.log(Level.INFO, "Running: " + String.join(" ", cmd));

        try {
            Process p = new ProcessBuilder(cmd).redirectErrorStream(false).start();

            String lastLine = null;
            try (BufferedReader out = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = out.readLine()) != null) {
                    lastLine = line;
                }
            }
            String err;
            try (BufferedReader er = new BufferedReader(
                    new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {
                err = er.lines().reduce("", (a, b) -> a + b + "\n");
            }

            int code = p.waitFor();
            if (code != 0 || lastLine == null) {
                throw new MediaFetchException("yt-dlp falló (código " + code + "): " + err);
            }

            File file = new File(lastLine.trim());
            if (!file.isFile() || !file.canRead()) {
                throw new MediaFetchException("Fichero descargado no válido: " + file.getAbsolutePath());
            }
            return file;

        } catch (IOException e) {
            throw new MediaFetchException("No se pudo ejecutar yt-dlp (¿está instalado y en el PATH?)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MediaFetchException("Descarga interrumpida", e);
        }
    }
}