package co.kukurin.server;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.server.request.headers.Headers;
import co.kukurin.server.request.PathResolver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

class ServerExecutor implements Runnable {

    private final Socket socket;
    private final ServerLogger logger;
    private final PathResolver pathResolver;

    private PushbackInputStream inputStream;
    private BufferedOutputStream outputStream;
    private Headers headers;

    ServerExecutor(Socket socket,
                   ServerLogger logger,
                   PathResolver pathResolver) {
        this.socket = socket;
        this.logger = logger;
        this.pathResolver = pathResolver;
    }

    @Override
    public void run() {
        ErrorHandler
                .catchIfThrows(this::initializeAndReadFromInput)
                .finalizingWith(this::closeStreams)
                .handleExceptionAs(e -> logger.error("Exception processing socket request", e));
    }

    private void initializeAndReadFromInput() throws IOException {
        initializeStreamsAndHeaders();
        logRequest();
        tryToGetResource();
        outputStream.flush();
    }

    private void tryToGetResource() {
        ErrorHandler
                .catchIfThrows(() -> {
                    Path resourcePath = pathResolver.getResourcePath(headers.getResource());

                    byte[] fileContents = Files.readAllBytes(resourcePath);
                    outputStream.write(fileContents);
                }).handleExceptionAs(e -> {
                    String errorMessage = "Error getting resources: " + e.getMessage();
                    logger.error(errorMessage);

                    redirectToError();
                });
    }

    private void redirectToError() {
        // TODO not really the job of pathResolver
        // also should send a 404 header.
        byte[] errorMessageClientOutput = pathResolver.getErrorMessage();
        ErrorHandler.ignoreIfThrows(() -> outputStream.write(errorMessageClientOutput));
    }

    private void logRequest() {
        logger.info("Received " + headers.getRequestType()
                + " request for " + headers.getResource());
    }

    private void initializeStreamsAndHeaders() throws IOException {
        inputStream = new PushbackInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
        headers = Headers.fromInputStream(inputStream);
    }

    private void closeStreams() {
        ErrorHandler.ignoreIfThrows(() -> {
            inputStream.close();
            outputStream.close();
            socket.close();
        });
    }

}
