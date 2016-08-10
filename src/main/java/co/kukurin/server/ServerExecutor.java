package co.kukurin.server;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.server.request.headers.Headers;
import co.kukurin.server.request.PathResolver;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

import static co.kukurin.server.environment.InitializationConstants.DEFAULT_ERROR_FILE_KEY;
import static co.kukurin.server.environment.InitializationConstants.DEFAULT_ERROR_MESSAGE_BYTES;

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
                .catchIfThrows(this::handleRequest)
                .finalizingWith(this::closeStreams)
                .handleExceptionAs(e -> logger.error("Exception processing socket request", e));
    }

    private void handleRequest() throws IOException {
        initializeStreamsAndHeaders();
        logRequest();
        outputRequestedResource();
    }

    private void initializeStreamsAndHeaders() throws IOException {
        inputStream = new PushbackInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
        headers = Headers.fromInputStream(inputStream);
    }

    private void logRequest() {
        logger.info("Received " + headers.getRequestMethod()
                + " request for " + headers.getResource());
    }

    private void outputRequestedResource() throws IOException {
        ErrorHandler
                .catchIfThrows(() -> {
                    // Path resourcePath = pathResolver.getResourceResponse(headers.getResource(), headers.getRequestMethod());
                    byte[] response = pathResolver.getResourceResponse(headers.getResource(), headers.getRequestMethod());
                    outputStream.write(response);
                }).handleExceptionAs(e -> {
                    logger.error("Error getting resources", e);
                    ErrorHandler.ignoreIfThrows(() -> outputStream.write(DEFAULT_ERROR_MESSAGE_BYTES));
                });
        outputStream.flush();
    }

//    private void redirectToError() {
//        byte[] errorMessageClientOutput = getErrorMessage();
//        ErrorHandler.ignoreIfThrows(() -> outputStream.write(errorMessageClientOutput));
//    }
//
//    private byte[] getErrorMessage() {
//        Path errorLocationPath = pathResolver.getResourceResponse(DEFAULT_ERROR_FILE_KEY, headers.getRequestMethod());
//        return Optional
//                .ofNullable(getBytes(errorLocationPath))
//                .orElse(DEFAULT_ERROR_MESSAGE_BYTES);
//    }
//
//    private byte[] getBytes(Path errorLocationPath) {
//        try { return Files.readAllBytes(errorLocationPath); }
//        catch (IOException ignorable) { return null; }
//    }

    private void closeStreams() {
        ErrorHandler.ignoreIfThrows(() -> {
            inputStream.close();
            outputStream.close();
            socket.close();
        });
    }

}
