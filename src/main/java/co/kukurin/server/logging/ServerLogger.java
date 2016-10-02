package co.kukurin.server.logging;

public interface ServerLogger {

    void info(String message, Object ... objectsToLog);

    void error(String description);
    void error(Throwable exception);
    void error(String description, Throwable exception);

}
