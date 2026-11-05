package media.fetcher.yt;

import media.fetcher.MediaFetchException;
import media.fetcher.MediaFetcher;
import media.fetcher.MediaFormat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class YoutubeMediaFetcher implements MediaFetcher {
    private final String url;
    private final File destinationDir;
    private final MediaFormat format;
    private final Logger logger = Logger.getLogger("YoutubeFetcher");

    /** Defaults to MP4 to keep the old behaviour. */
    public YoutubeMediaFetcher(String url, String destinationDir) {
        this(url, destinationDir, MediaFormat.MP4);
    }

    public YoutubeMediaFetcher(String url, String destinationDir, MediaFormat format) {
        this.url = Objects.requireNonNull(url, "url no puede ser null");
        this.destinationDir = new File(Objects.requireNonNull(destinationDir, "destinationDir no puede ser null"));
        this.format = Objects.requireNonNull(format, "format no puede ser null");
    }

    private List<String> buildCommand() {
        List<String> cmd = new ArrayList<>();
        cmd.add("yt-dlp");
        cmd.add("--no-playlist");
        cmd.add("--restrict-filenames");

        if (format.isAudioOnly()) {
            cmd.add("-x");
            cmd.add("--audio-format");
            cmd.add(format.extension());          // wav or mp3
        } else {
            cmd.add("-f");
            cmd.add("b[ext=mp4][vcodec^=avc]/b[ext=mp4]/b");
            cmd.add("--merge-output-format");
            cmd.add(format.extension());          // mp4
        }

        cmd.add("-o");
        cmd.add(new File(destinationDir, "%(title)s.%(ext)s").getAbsolutePath());
        cmd.add("--print");
        cmd.add("after_move:filepath");
        cmd.add("--no-simulate");
        cmd.add(url);
        return cmd;
    }

    @Override
    public File getMedia() {
        if (!destinationDir.exists() && !destinationDir.mkdirs()) {
            throw new MediaFetchException("No se pudo crear el directorio: " + destinationDir.getAbsolutePath());
        }

        List<String> cmd = buildCommand();
        logger.log(Level.INFO, "Running: " + String.join(" ", cmd));

        try {
            Process p = new ProcessBuilder(cmd).start();

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