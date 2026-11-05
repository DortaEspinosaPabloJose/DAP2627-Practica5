package media.searcher.video;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

import media.fetcher.LocalMediaFetcher;
import media.fetcher.MediaFetchException;
import media.searcher.MediaSearcher;

/**
 * Buscador de vídeos locales (MP4) dentro del directorio de descargas.
 */
public class LocalVideoSearcher implements MediaSearcher {
    private static final String EXTENSION = ".mp4";
    private final File directory;

    public LocalVideoSearcher(File directory) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
    }

    @Override
    public List<String> search(String query) {
        Objects.requireNonNull(query, "query no puede ser null");
        String needle = query.toLowerCase(Locale.ROOT);

        try (Stream<Path> paths = Files.walk(directory.toPath())) {
            return paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.toLowerCase(Locale.ROOT).endsWith(EXTENSION))
                    .map(LocalVideoSearcher::stripExtension)
                    .filter(title -> title.toLowerCase(Locale.ROOT).contains(needle))
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new MediaFetchException("Error al buscar vídeos en local: " + directory.getAbsolutePath(), e);
        }
    }

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
                    .orElseThrow(() -> new MediaFetchException("No se encontró el vídeo local: " + name));

            return new LocalMediaFetcher(found).getMedia();
        } catch (IOException e) {
            throw new MediaFetchException("Error al seleccionar vídeo local en: " + directory.getAbsolutePath(), e);
        }
    }

    private static String stripExtension(String fileName) {
        return fileName.substring(0, fileName.length() - EXTENSION.length());
    }
}