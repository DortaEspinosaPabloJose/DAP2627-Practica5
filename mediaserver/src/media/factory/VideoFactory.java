package media.factory;

import java.io.File;
import java.util.Objects;
import javax.swing.JPanel;

import media.player.MediaPlayer;
import media.player.VideoPlayer;
import media.searcher.LocalVideoSearcher;
import media.searcher.MediaSearcher;
import media.searcher.YoutubeVideoSearcher;

public class VideoFactory implements MediaFactory {
    private final File directory;
    private final JPanel targetPanel;
    private final boolean isLocalMode;

    public VideoFactory(File directory, JPanel targetPanel, boolean isLocalMode) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
        this.targetPanel = Objects.requireNonNull(targetPanel, "targetPanel no puede ser null");
        this.isLocalMode = isLocalMode;
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