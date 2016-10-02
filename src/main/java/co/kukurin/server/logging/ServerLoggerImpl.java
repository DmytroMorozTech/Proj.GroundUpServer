package co.kukurin.server.logging;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.custom.ThrowableRunnable;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class ServerLoggerImpl implements ServerLogger {

    private static ServerLoggerImpl instance;

    private static final String ERROR_PREFIX = "[ERROR] ";
    private static final String INFO_PREFIX = "[INFO] ";
    private static final String NEWLINE = System.lineSeparator();

    private PrintStream outputLogStream;

    private ServerLoggerImpl(PrintStream outputLogStream) {
        this.outputLogStream = outputLogStream;
    }

    public static void setInstancePrintStream(PrintStream outputLogStream) {
        ServerLoggerImpl.instance = new ServerLoggerImpl(outputLogStream);
    }

    public static ServerLoggerImpl getInstance() {
        return instance;
    }

    public void info(String message, Object ... objectsToLog) {
        String stringToWrite = Optional.ofNullable(objectsToLog)
                .map(Arrays::stream)
                .map(streamed -> {
                    StringBuilder sb = new StringBuilder(message);
                    streamed
                            .filter(item -> item != null)
                            .map(Object::toString)
                            .forEach(item -> sb.append(" { ").append(item).append(" }, "));
                    final int commaAndSpaceLength = 2;
                    return sb.substring(0, sb.length() - commaAndSpaceLength); })
               .orElse(message);

        write(INFO_PREFIX, stringToWrite);
    }

    public void error(Throwable exception) {
        write(ERROR_PREFIX, exception.getMessage());
    }

    public void error(String description) {
        write(ERROR_PREFIX, description);
    }

    public void error(String description, Throwable exception) {
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
