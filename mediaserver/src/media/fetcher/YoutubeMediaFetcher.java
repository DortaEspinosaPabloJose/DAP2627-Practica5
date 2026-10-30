package media.fetcher;

import java.io.File;
import java.net.URL;
import com.github.axet.vget.VGet;
import java.util.Objects;

public class YoutubeMediaFetcher implements MediaFetcher {
    private final String url;
    private final String destinationPath;

    public YoutubeMediaFetcher(String url, String destinationPath) {
        this.url = url;
        this.destinationPath = destinationPath;
    }

    private void downloadMedia() {
        try {
            VGet v = new VGet(new URL(url), new File(destinationPath));
            v.download();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File getMedia() {
        downloadMedia();
        File file = new File(Objects.requireNonNull(destinationPath, "path no puede ser null"));

        if (!file.exists()) {
            throw new MediaFetchException("El fichero no existe: " + file.getAbsolutePath());
        }
        // ... (resto de comprobaciones idénticas al original)
        return file;
    }
}