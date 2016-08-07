package co.kukurin.server;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

public final class ServerLogger {

    public static final String ERROR_PREFIX = "ERROR";
    public static final String INFO_PREFIX = "INFO";
    private static final ServerLogger instance = new ServerLogger();

    private PrintStream outputLogStream;

    private ServerLogger() {
        this.outputLogStream = System.out;
    }

    public static ServerLogger getInstance() {
        return instance;
    }

    public void info(String data) {
        write(INFO_PREFIX, data);
    }

    public void info(Object object) {
        info(object.toString());
    }

    public void error(Exception exception) {
        write(ERROR_PREFIX, exception.getMessage());
    }

    public void error(String description) {
        write(ERROR_PREFIX, description);
    }

    public void error(String description, Exception exception) {
        write("ERROR", description + ": " + exception.getMessage());
    }

    private void write(String prefix, String content) {
        try {
            byte[] message = defaultMessageAsBytes(prefix, content);
            outputLogStream.write(message);
        } catch (IOException ignorable) {
        }
    }

    private byte[] defaultMessageAsBytes(String prefix, String content) {
        return new StringBuilder()
                .append(prefix)
                .append(" | ")
                .append(content)
                .append(System.lineSeparator())
                .toString()
                .getBytes();
    }
}
