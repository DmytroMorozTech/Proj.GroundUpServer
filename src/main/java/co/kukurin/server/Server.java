package co.kukurin.server;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.server.environment.ServerProperties;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static co.kukurin.server.environment.InitializationConstants.DEFAULT_PORT;
import static co.kukurin.server.environment.InitializationConstants.PORT_KEY;

public class Server {

    private final ServerLogger logger;
    private final ServerProperties properties;
    private final ExecutorService executorService;

    public Server(ServerLogger logger,
                  ServerProperties properties,
                  ExecutorService executorService) {
        this.logger = logger;
        this.properties = properties;
        this.executorService = executorService;
    }

    public void start(String... args) {
        Integer port = properties.getOrDefaultInt(PORT_KEY, DEFAULT_PORT);

        getServerSocket(port)
                .ifPresent(this::serverLoop)
                .orElseDo(() -> logger.error("Could not open port " + port));
    }

    private Optional<ServerSocket> getServerSocket(Integer port) {
        try {
            return Optional.of(new ServerSocket(port));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private void serverLoop(ServerSocket serverSocket) {
        while (true) {
            ErrorHandler
                    .catchException(() -> acceptAndSubmitConnection(serverSocket))
                    .handleException(e -> logger.error("Encountered exception processing socket.", e));
        }
    }

    private void acceptAndSubmitConnection(ServerSocket serverSocket) throws IOException {
        //try(Socket socketClient = serverSocket.accept()) {
            Socket socketClient = serverSocket.accept();
            ServerExecutor serverExecutor = new ServerExecutor(socketClient, logger);
            executorService.submit(serverExecutor);
        //}
    }

}
