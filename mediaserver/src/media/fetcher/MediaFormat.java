package media.fetcher;

public enum MediaFormat {
    WAV(true),
    MP3(true),
    MP4(false);

    private final boolean audioOnly;

    MediaFormat(boolean audioOnly) {
        this.audioOnly = audioOnly;
    }

    public boolean isAudioOnly() {
        return audioOnly;
    }

    /** Value yt-dlp expects, e.g. "mp3". */
    public String extension() {
        return name().toLowerCase();
    }
}