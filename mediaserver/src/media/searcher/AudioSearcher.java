package media.searcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import media.fetcher.LocalMediaFetcher;
import media.fetcher.MediaFetchException;
import media.fetcher.MediaFormat;
import media.fetcher.yt.YoutubeMediaFetcher;
import media.fetcher.yt.YoutubeSearch;
import media.fetcher.yt.YoutubeSearchResult;

/**
 * Buscador de ficheros de audio (.wav) dentro de un directorio.
 */
public class AudioSearcher implements MediaSearcher {
    private static final String EXTENSION = ".wav";

    private final File directory;
    private final String source;

    private Map<String, YoutubeSearchResult> lastResults = new LinkedHashMap<>();

    private final Logger logger = Logger.getLogger("AudioSearcherLog");

    public AudioSearcher(String source, File directory) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
        this.source = source;

        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            List<String> found = paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .toList();
            logger.log(Level.INFO, "AudioSearcher files in directory (" + directory.getAbsolutePath() + ") found: " + found);
        } catch (IOException e) {
            throw new MediaFetchException("Error al buscar en: " + directory.getAbsolutePath(), e);
        }
    }

    /** Devuelve los títulos (nombre sin extensión) cuyo nombre contiene la consulta. */
    @Override
    public List<String> search(String query) {
        if(Objects.equals(source, "Local")) return localAudioSearch(query);

        List<String> found = youtubeAudioSearch(query)
                .stream()
                .map(YoutubeSearchResult::getTitle)
                .toList();
        logger.log(Level.INFO, "Query: " + query + " found: " + found);
        return found;
    }

    /** Devuelve el fichero cuyo título coincide exactamente (sin distinguir mayúsculas). */
    @Override
    public File select(String name) {
        Objects.requireNonNull(name, "name no puede ser null");
        if(Objects.equals(source, "Local")) return selectLocal(name);
        return selectYoutube(name);

    }


    private List<String> localAudioSearch(String query){
        Objects.requireNonNull(query, "query no puede ser null");
        String needle = query.toLowerCase(Locale.ROOT);

        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            List<String> found = paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.toLowerCase(Locale.ROOT).endsWith(EXTENSION))
                    .map(AudioSearcher::stripExtension)
                    .filter(title -> title.toLowerCase(Locale.ROOT).contains(needle))
                    .sorted()
                    .toList();
            logger.log(Level.INFO, "Query: " + query + " needle: " + needle + " found: " + found);
            return found;
        } catch (IOException e) {
            throw new MediaFetchException("Error al buscar en: " + directory.getAbsolutePath(), e);
        }
    }

    private List<YoutubeSearchResult> youtubeAudioSearch(String query){
        Objects.requireNonNull(query, "query no puede ser null");
        List<YoutubeSearchResult> res = YoutubeSearch.query(query);
        lastResults = new LinkedHashMap<>();
        res.forEach(r -> lastResults.putIfAbsent(r.getTitle(), r));
        return res;
    }

    private File selectLocal(String name){
        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            File found = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(EXTENSION))
                    .filter(p -> stripExtension(p.getFileName().toString()).equalsIgnoreCase(name))
                    .map(Path::toFile)
                    .findFirst()
                    .orElseThrow(() -> new MediaFetchException("No se encontró el audio: " + name));

            // Reutiliza las validaciones (existe, es fichero, legible)
            return new LocalMediaFetcher(found).getMedia();
        } catch (IOException e) {
            throw new MediaFetchException("Error al seleccionar en: " + directory.getAbsolutePath(), e);
        }
    }

    private File selectYoutube(String name){
        YoutubeSearchResult r = lastResults.get(name);
        if (r == null) throw new MediaFetchException("Busca primero: " + name);
        return new YoutubeMediaFetcher(r.getUrl(),directory.getAbsolutePath(), MediaFormat.WAV).getMedia();
    }

    private static String stripExtension(String fileName) {
        return fileName.substring(0, fileName.length() - EXTENSION.length());
    }
}