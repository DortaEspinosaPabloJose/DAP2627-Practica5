package fetcher;

import java.io.File;
import java.util.Objects;

/**
 * recupera un vídeo ya existente en el sistema de ficheros local.
 */
public class LocalVideoFetcher implements VideoFetcher {

    private final File file;

    public LocalVideoFetcher(String path) {
        this(new File(Objects.requireNonNull(path, "path no puede ser null")));
    }

    public LocalVideoFetcher(File file) {
        this.file = Objects.requireNonNull(file, "file no puede ser null");
    }

    @Override
    public File getVideo() {
        if (!file.exists()) {
            throw new VideoFetchException("El fichero no existe: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new VideoFetchException("La ruta no es un fichero: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new VideoFetchException("No hay permisos de lectura: " + file.getAbsolutePath());
        }
        return file;
    }
}