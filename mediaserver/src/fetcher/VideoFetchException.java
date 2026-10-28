package fetcher;

/**
 * Error al recuperar un fichero de vídeo desde cualquier fuente.
 */
public class VideoFetchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public VideoFetchException(String message) {
        super(message);
    }

    public VideoFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}