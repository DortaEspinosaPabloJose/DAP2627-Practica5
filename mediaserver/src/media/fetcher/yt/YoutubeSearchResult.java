package media.fetcher.yt;

public final class YoutubeSearchResult {
    private final String id;
    private final String title;
    private final String channel;   // may be null
    private final String duration;  // e.g. "3:45", may be null (live streams)

    public YoutubeSearchResult(String id, String title, String channel, String duration) {
        this.id = id;
        this.title = title;
        this.channel = channel;
        this.duration = duration;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getChannel() { return channel; }
    public String getDuration() { return duration; }

    public String getUrl() {
        return "https://www.youtube.com/watch?v=" + id;
    }

    @Override
    public String toString() {
        return title + " [" + (duration != null ? duration : "?") + "]"
                + (channel != null ? " - " + channel : "") + " " + getUrl();
    }
}