package media.searcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import media.fetcher.LocalMediaFetcher;
import media.fetcher.MediaFetchException;

/**
 * Buscador de ficheros de audio (.wav) dentro de un directorio.
 */
public class AudioSearcher implements MediaSearcher {
    private static final String EXTENSION = ".wav";

    private final File directory;

    private final Logger logger = Logger.getLogger("AudioSearcherLog");

    public AudioSearcher(File directory) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");

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

    public AudioSearcher(String directoryPath) {
        this(new File(Objects.requireNonNull(directoryPath, "directoryPath no puede ser null")));
    }

    /** Devuelve los títulos (nombre sin extensión) cuyo nombre contiene la consulta. */
    @Override
    public List<String> search(String query) {
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

    /** Devuelve el fichero cuyo título coincide exactamente (sin distinguir mayúsculas). */
    @Override
    public File select(String name) {
        Objects.requireNonNull(name, "name no puede ser null");

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
    private static String stripExtension(String fileName) {
        return fileName.substring(0, fileName.length() - EXTENSION.length());
    }
}