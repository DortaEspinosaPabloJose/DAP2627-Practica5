package videofactory;

import video.videoplayer.VideoPlayer;
import video.videosearcher.VideoSearcher;

public interface VideoFactory {
    public VideoSearcher makeVideoSearcher();
    public VideoPlayer makeVideoPlayer();
}
