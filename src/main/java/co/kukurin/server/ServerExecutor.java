package co.kukurin.server;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.server.request.Headers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.Socket;

class ServerExecutor implements Runnable {

    private final Socket socket;
    private final ServerLogger logger;

    private PushbackInputStream inputStream;
    private BufferedOutputStream outputStream;
    private Headers headers;

    ServerExecutor(Socket socket,
                   ServerLogger logger) {
        this.socket = socket;
        this.logger = logger;
    }

    @Override
    public void run() {
        ErrorHandler
                .catchException(this::initializeAndReadFromInput)
                .rethrowAsUncheckedFirstInvoking(this::closeStreams);
    }

    private void initializeAndReadFromInput() throws IOException {
        initializeStreams();
        headers = Headers.fromInputStream(inputStream);
        logRequest();

        outputStream.write("Hello.".getBytes());
        outputStream.flush();
        closeStreams();
    }

    private void logRequest() {
        logger.info("Received " + headers.getRequestType()
                + " request for " + headers.getResource());
    }

    private void initializeStreams() throws IOException {
        inputStream = new PushbackInputStream(socket.getInputStream());
        outputStream = new BufferedOutputStream(socket.getOutputStream());
    }

    private void closeStreams() {
        ErrorHandler.ignoreIfThrows(() -> {
            inputStream.close();
            outputStream.close();
            socket.close();
        });
    }

}
