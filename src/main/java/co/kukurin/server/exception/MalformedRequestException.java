package co.kukurin.server.exception;

public class MalformedRequestException extends RuntimeException {

    public MalformedRequestException(Throwable cause) {
        super(cause);
    }

    public MalformedRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public MalformedRequestException(String message) {
        super(message);
    }

    public MalformedRequestException() {
    }
}
