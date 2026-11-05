package media.factory;

import java.io.File;
import java.util.Objects;

import media.player.AudioPlayer;
import media.player.MediaPlayer;
import media.searcher.MediaSearcher;
import media.searcher.audio.YoutubeAudioSearcher;
import media.searcher.audio.LocalAudioSearcher;
import media.searcher.audio.LocalAudioSearcher;


/**
 * Fábrica concreta de la familia "audio".
 */
public class AudioFactory implements MediaFactory {
    private final boolean isLocalMode;
    private final File directory;

    public AudioFactory(boolean isLocalMode) {
        this(isLocalMode, new File(System.getProperty("user.home"), "Audios"));
    }

    public AudioFactory(boolean isLocalMode, File directory) {
        this.isLocalMode = isLocalMode;
        this.directory = Objects.requireNonNull(directory, "directory no puede ser null");
    }

    public AudioFactory(File directory){
        this.isLocalMode = false;
        this.directory = directory;
    }

    @Override
    public MediaSearcher createSearcher() {
        if(this.isLocalMode) return new LocalAudioSearcher(directory);
        return new YoutubeAudioSearcher(directory);
    }

    @Override
    public MediaPlayer createPlayer() {
        return new AudioPlayer();
    }
}