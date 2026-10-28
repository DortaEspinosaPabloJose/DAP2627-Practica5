package fetcher;

import java.io.File;
import java.net.URL;
import com.github.axet.vget.VGet;
import java.util.Objects;

public class YoutubeVideoFetcher implements VideoFetcher{
    String url;
    String destinationPath;

    public YoutubeVideoFetcher(String url, String destinationPath){
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
    public File getVideo() {
        downloadVideo();

        File file = new File(Objects.requireNonNull(destinationPath, "path no puede ser null"));

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
