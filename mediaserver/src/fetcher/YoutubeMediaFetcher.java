package fetcher;

import java.io.File;
import java.net.URL;
import com.github.axet.vget.VGet;
import java.util.Objects;

public class YoutubeMediaFetcher implements MediaFetcher {
    String url;
    String destinationPath;

    public YoutubeMediaFetcher(String url, String destinationPath){
        this.url = url;
        this.destinationPath = destinationPath;
    }

    public void downloadVideo(){
        try {
            VGet v = new VGet(new URL(url), new File(destinationPath));
            v.download();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getMedia() {
        downloadVideo();

        File file = new File(Objects.requireNonNull(destinationPath, "path no puede ser null"));

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
