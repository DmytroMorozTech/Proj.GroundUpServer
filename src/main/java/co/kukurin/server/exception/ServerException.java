package co.kukurin.server.exception;

public class ServerException extends RuntimeException {

    public ServerException(Throwable cause) {
        super(cause);
    }

    public ServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerException(String message) {
        super(message);
    }

    public ServerException() {
        super();
    }
}
