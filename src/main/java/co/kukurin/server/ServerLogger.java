package co.kukurin.server;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public final class ServerLogger {
    // TODO synchronized

    private static final String ERROR_PREFIX = "[ERROR] ";
    private static final String INFO_PREFIX = "[INFO] ";
    private static final String NEWLINE = System.lineSeparator();
    private static final ServerLogger instance = new ServerLogger();

    private PrintStream outputLogStream;

    private ServerLogger() {
        this.outputLogStream = System.out;
    }

    public static ServerLogger getInstance() {
        return instance;
    }

//    public void info(String data) {
//        write(INFO_PREFIX, data);
//    }

    public void info(Object object) {
        info(object.toString(), null);
    }

    public void info(String message, Object ... objectsToLog) {
        StringBuilder sb = new StringBuilder(message);
        Optional.ofNullable(objectsToLog)
                .map(Arrays::stream)
                .ifPresent(streamed ->
                        streamed
                                .filter(item -> item != null)
                                .forEach(item -> sb.append(" { ").append(item.toString()).append(" }, "))
                );

        final int commaAndSpaceLength = 2;
        write(INFO_PREFIX,
                sb.length() == message.length()
                        ? sb.toString()
                        : sb.substring(0, sb.length() - commaAndSpaceLength)
        );
    }

    public void error(Exception exception) {
        write(ERROR_PREFIX, exception.getMessage());
    }

    public void error(String description) {
        write(ERROR_PREFIX, description);
    }

    public void error(String description, Exception exception) {
        write(ERROR_PREFIX, description + ": " + exception.getMessage());
    }

    private void write(String prefix, String content) {
        ErrorHandler.ignoreIfThrows(() -> {
            byte[] message = defaultMessageAsBytes(prefix, content);
            outputLogStream.write(message);
        });
    }

    private byte[] defaultMessageAsBytes(String prefix, String content) {
        return (prefix + content + NEWLINE).getBytes();
    }
}
