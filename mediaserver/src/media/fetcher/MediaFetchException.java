package media.fetcher;

public class MediaFetchException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MediaFetchException(String message) {
        super(message);
    }

    public MediaFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}