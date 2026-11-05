package media.searcher;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

import media.fetcher.MediaFetchException;
import media.fetcher.MediaFormat;
import media.fetcher.yt.YoutubeMediaFetcher;
import media.fetcher.yt.YoutubeSearch;
import media.fetcher.yt.YoutubeSearchResult;

/**
 * Buscador de vídeos en YouTube y descargador a un directorio local.
 */
public class YoutubeVideoSearcher implements MediaSearcher {

    private final File directory;
    private final Map<String, YoutubeSearchResult> searchCache;

    public YoutubeVideoSearcher(File directory) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
        this.searchCache = new HashMap<>();
    }

    public YoutubeVideoSearcher(String directoryPath) {
        this(new File(Objects.requireNonNull(directoryPath, "directoryPath no puede ser null")));
    }

    /** Devuelve los títulos de los vídeos encontrados en YouTube. */
    @Override
    public List<String> search(String query) {
        Objects.requireNonNull(query, "query no puede ser null");
        searchCache.clear();

        try {
            List<YoutubeSearchResult> results = YoutubeSearch.query(query);

            return results.stream()
                    .map(result -> {
                        // Guardamos el resultado en caché usando el título como clave
                        searchCache.put(result.getTitle(), result);
                        return result.getTitle();
                    })
                    .toList();
        } catch (Exception e) {
            throw new MediaFetchException("Error al buscar en YouTube: " + query, e);
        }
    }

    /** Descarga el fichero cuyo título coincide y lo devuelve. */
    @Override
    public File select(String name) {
        Objects.requireNonNull(name, "name no puede ser null");

        YoutubeSearchResult result = searchCache.get(name);
        if (result == null) {
            throw new MediaFetchException("No se encontró el vídeo en los resultados de búsqueda: " + name);
        }

        try {
            YoutubeMediaFetcher fetcher = new YoutubeMediaFetcher(
                    result.getUrl(),
                    directory.getAbsolutePath(),
                    MediaFormat.MP4
            );
            return fetcher.getMedia();
        } catch (Exception e) {
            throw new MediaFetchException("Error al descargar el vídeo: " + name, e);
        }
    }
}