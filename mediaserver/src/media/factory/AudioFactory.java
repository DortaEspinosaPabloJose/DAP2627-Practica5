package media.factory;

import java.io.File;
import java.util.Objects;

import media.player.AudioPlayer;
import media.player.MediaPlayer;
import media.searcher.AudioSearcher;
import media.searcher.MediaSearcher;

/**
 * Fábrica concreta de la familia "audio".
 */
public class AudioFactory implements MediaFactory {
    private final File directory;
    private final String source;

    public AudioFactory(String source) {
        String audioDirectory = System.getProperty("user.home") + "/Audios";
        this.source = source;
        this.directory = new File(Objects.requireNonNull(audioDirectory, "directory no puede ser null"));
    }

    @Override
    public MediaSearcher createSearcher() {
        return new AudioSearcher(source,directory);
    }

    @Override
    public MediaPlayer createPlayer() {
        return new AudioPlayer();
    }
}