package media.factory;

import media.player.MediaPlayer;
import media.searcher.MediaSearcher;

public interface MediaFactory {
    MediaSearcher createSearcher();
    MediaPlayer createPlayer();
}