package media.factory;

import java.io.File;
import java.util.Objects;
import javax.swing.JPanel;

import media.player.MediaPlayer;
import media.player.VideoPlayer;
import media.searcher.MediaSearcher;
import media.searcher.VideoSearcher;

/**
 * Fábrica concreta de la familia "vídeo" (MP4).
 */
public class VideoFactory implements MediaFactory {
    private final File directory;
    private final JPanel targetPanel;

    public VideoFactory(File directory, JPanel targetPanel) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
        this.targetPanel = Objects.requireNonNull(targetPanel, "targetPanel no puede ser null");
    }

    public VideoFactory(String directoryPath, JPanel targetPanel) {
        this(new File(Objects.requireNonNull(directoryPath, "directoryPath no puede ser null")), targetPanel);
    }

    @Override
    public MediaSearcher createSearcher() {
        return new VideoSearcher(directory);
    }

    @Override
    public MediaPlayer createPlayer() {
        return new VideoPlayer(targetPanel);
    }
}