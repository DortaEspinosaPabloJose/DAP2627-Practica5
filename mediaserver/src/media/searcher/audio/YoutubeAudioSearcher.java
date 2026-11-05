package media.searcher.audio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import media.fetcher.LocalMediaFetcher;
import media.fetcher.MediaFetchException;
import media.fetcher.MediaFormat;
import media.fetcher.yt.YoutubeMediaFetcher;
import media.fetcher.yt.YoutubeSearch;
import media.fetcher.yt.YoutubeSearchResult;
import media.searcher.MediaSearcher;
import media.searcher.TextNormalizer;

/**
 * Buscador de audio en YouTube. Si el título seleccionado ya existe como
 * fichero en el directorio destino, se reutiliza en lugar de descargarlo.
 */
public class YoutubeAudioSearcher implements MediaSearcher {
    private static final String EXTENSION = ".wav";

    private final File directory;
    private final Logger logger = Logger.getLogger(YoutubeAudioSearcher.class.getName());

    private Map<String, YoutubeSearchResult> lastResults = new LinkedHashMap<>();

    public YoutubeAudioSearcher(File directory) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
        if (!directory.exists() && !directory.mkdirs()) {
            throw new MediaFetchException("No se pudo crear el directorio: " + directory.getAbsolutePath());
        }
    }

    /** Busca en YouTube y devuelve los títulos encontrados. */
    @Override
    public List<String> search(String query) {
        Objects.requireNonNull(query, "query no puede ser null");

        List<YoutubeSearchResult> res = YoutubeSearch.query(query);
        Map<String, YoutubeSearchResult> cache = new LinkedHashMap<>();
        res.forEach(r -> cache.putIfAbsent(r.getTitle(), r));
        lastResults = cache;

        List<String> found = List.copyOf(cache.keySet());
        logger.info(() -> "Query: " + query + " found: " + found);
        return found;
    }

    /**
     * Devuelve el fichero del título indicado. Si ya existe en el directorio
     * se devuelve ése; si no, se descarga desde YouTube.
     */
    @Override
    public File select(String name) {
        Objects.requireNonNull(name, "name no puede ser null");

        YoutubeSearchResult r = lastResults.get(name);
        if (r == null) {
            throw new MediaFetchException("Busca primero: " + name);
        }

        Optional<File> existing = findLocal(r.getTitle());
        if (existing.isPresent()) {
            logger.info(() -> "Ya descargado, se omite la descarga: " + existing.get());
            return new LocalMediaFetcher(existing.get()).getMedia(); // valida existe/fichero/legible
        }

        return new YoutubeMediaFetcher(r.getUrl(), directory.getAbsolutePath(), MediaFormat.WAV).getMedia();
    }

    private Optional<File> findLocal(String title) {
        String target = TextNormalizer.compact(title);
        if (target.isEmpty()) {
            return Optional.empty(); // título sin letras ni dígitos: evita falsos positivos
        }

        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(EXTENSION))
                    .filter(p -> TextNormalizer.compact(stripExtension(p.getFileName().toString())).equals(target))
                    .map(Path::toFile)
                    .findFirst();
        } catch (IOException e) {
            throw new MediaFetchException("Error al buscar en: " + directory.getAbsolutePath(), e);
        }
    }

    private static String stripExtension(String fileName) {
        return fileName.substring(0, fileName.length() - EXTENSION.length());
    }
}