package media.fetcher;

import java.io.File;
import java.util.Objects;

public class LocalMediaFetcher implements MediaFetcher {
    private final File file;

    public LocalMediaFetcher(String path) {
        this(new File(Objects.requireNonNull(path, "path no puede ser null")));
    }

    public LocalMediaFetcher(File file) {
        this.file = Objects.requireNonNull(file, "file no puede ser null");
    }

    @Override
    public File getMedia() {
        if (!file.exists()) {
            throw new MediaFetchException("El fichero no existe: " + file.getAbsolutePath());
        }
        if (!file.isFile()) {
            throw new MediaFetchException("La ruta no es un fichero: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new MediaFetchException("No hay permisos de lectura: " + file.getAbsolutePath());
        }
        return file;
    }
}