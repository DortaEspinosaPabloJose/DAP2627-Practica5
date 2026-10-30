package fetcher;

/**
 * Error al recuperar un fichero de vídeo desde cualquier fuente.
 */
public class MediaFetchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MediaFetchException(String message) {
        super(message);
    }

    public MediaFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}