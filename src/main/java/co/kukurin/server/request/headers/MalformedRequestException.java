package co.kukurin.server.request.headers;

import co.kukurin.server.exception.ServerException;

public class MalformedRequestException extends ServerException {

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
