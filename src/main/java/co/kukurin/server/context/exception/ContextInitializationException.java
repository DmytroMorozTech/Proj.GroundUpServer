package co.kukurin.server.context.exception;

import co.kukurin.server.exception.ServerException;

public class ContextInitializationException extends ServerException {

    public ContextInitializationException() {
        super("Context failed to initialize properly");
    }

}
