package media.factory;

import java.io.File;
import java.util.Objects;
import javax.swing.JPanel;

import media.player.MediaPlayer;
import media.player.VideoPlayer;
import media.searcher.video.LocalVideoSearcher;
import media.searcher.MediaSearcher;
import media.searcher.video.YoutubeVideoSearcher;

/**
 * Fábrica concreta de la familia "video".
 */
public class VideoFactory implements MediaFactory {
    private final boolean isLocalMode;
    private final File directory;
    private final JPanel targetPanel;

    public VideoFactory(boolean isLocalMode, File directory, JPanel targetPanel) {
        this.isLocalMode = isLocalMode;
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
        this.targetPanel = Objects.requireNonNull(targetPanel, "targetPanel no puede ser null");
    }

    @Override
    public MediaSearcher createSearcher() {
        if (isLocalMode) {
            return new LocalVideoSearcher(directory);
        } else {
            return new YoutubeVideoSearcher(directory);
        }
    }

    @Override
    public MediaPlayer createPlayer() {
        return new VideoPlayer(targetPanel);
    }
}