package co.kukurin.server.exception;

public class ContextInitializationException extends ServerException {

    public ContextInitializationException() {
        super("Context failed to initialize properly");
    }

}
