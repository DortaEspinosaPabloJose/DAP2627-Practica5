package mediafactory;

import media.player.MediaPlayer;
import media.searcher.MediaSearcher;

public interface MediaFactory {
    public MediaSearcher makeVideoSearcher();
    public MediaPlayer makeVideoPlayer();
}
