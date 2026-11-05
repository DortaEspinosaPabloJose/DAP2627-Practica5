package media.player;

import java.io.File;
import java.util.function.Consumer;

public interface MediaPlayer {
    void play(File file);
    void stop();
    void pause();
    void resume();
    void seek(double percentage);
    void setProgressListener(Consumer<Double> listener);
}