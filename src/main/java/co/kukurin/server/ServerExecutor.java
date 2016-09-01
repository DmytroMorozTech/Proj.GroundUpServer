package co.kukurin.server;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.server.request.ResourceRequest;
import co.kukurin.server.request.ResourceResolver;
import co.kukurin.server.request.headers.Headers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;

import static co.kukurin.server.environment.InitializationConstants.DEFAULT_ERROR_MESSAGE_BYTES;

class ServerExecutor implements Runnable {

    private final Socket socket;
    private final ServerLogger logger;
    private final ResourceResolver resourceResolver;

    private PushbackInputStream inputStream;
    private BufferedOutputStream outputStream;
    private Headers headers;

    ServerExecutor(Socket socket,
                   ServerLogger logger,
                   ResourceResolver resourceResolver) {
        this.socket = socket;
        this.logger = logger;
        this.resourceResolver = resourceResolver;
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
                    ResourceRequest resourceRequest = new ResourceRequest(headers.getRequestMethod(), headers.getResource());
                    byte[] response = resourceResolver.getResponseBytes(resourceRequest);

                    outputStream.write(response);
                }).handleExceptionAs(e -> {
                    logger.error("Error getting resources", e);
                    ErrorHandler.ignoreIfThrows(() -> outputStream.write(DEFAULT_ERROR_MESSAGE_BYTES));
                });
        outputStream.flush();
    }

    private void closeStreams() {
        ErrorHandler.ignoreIfThrows(() -> {
            inputStream.close();
            outputStream.close();
            socket.close();
        });
    }

}
