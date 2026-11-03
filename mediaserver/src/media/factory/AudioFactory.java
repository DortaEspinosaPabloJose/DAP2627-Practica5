package media.factory;

import java.io.File;
import java.util.Objects;

import media.player.AudioPlayer;
import media.player.MediaPlayer;
import media.searcher.AudioSearcher;
import media.searcher.MediaSearcher;

/**
 * Fábrica concreta de la familia "audio" (WAV).
 */
public class AudioFactory implements MediaFactory {
    private final File directory;

    public AudioFactory(File directory) {
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
    }

    public AudioFactory(String directoryPath) {
        this(new File(Objects.requireNonNull(directoryPath, "directoryPath no puede ser null")));
    }

    @Override
    public MediaSearcher createSearcher() {
        return new AudioSearcher(directory);
    }

    @Override
    public MediaPlayer createPlayer() {
        return new AudioPlayer();
    }
}