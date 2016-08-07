package co.kukurin.server;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.server.environment.ServerProperties;
import co.kukurin.server.request.PathResolver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import static co.kukurin.server.environment.InitializationConstants.*;

public class Server {

    private final ServerLogger logger;
    private final ServerProperties properties;
    private final ExecutorService executorService;
    private final PathResolver pathResolver;

    public Server(ServerLogger logger,
                  ServerProperties properties,
                  ExecutorService executorService, PathResolver pathResolver) {
        this.logger = logger;
        this.properties = properties;
        this.executorService = executorService;
        this.pathResolver = pathResolver;
    }

    @SuppressWarnings("unused") // for now the args are unused, maybe will add some usage later.
    public void start(String... args) {
        Integer port = properties.getOrDefaultInt(PORT_KEY, DEFAULT_PORT);

        getServerSocket(port)
                .ifPresent(this::serverLoop)
                .orElseDo(() -> logger.error("Could not open port " + port));
    }

    private Optional<ServerSocket> getServerSocket(Integer port) {
        try { return Optional.of(new ServerSocket(port)); }
        catch (IOException e) { return Optional.empty(); }
    }

    private void serverLoop(ServerSocket serverSocket) {
        while (true) {
            ErrorHandler
                    .catchIfThrows(() -> acceptAndSubmitConnection(serverSocket))
                    .handleExceptionAs(e -> logger.error("Encountered exception processing socket.", e));
        }
    }

    private void acceptAndSubmitConnection(ServerSocket serverSocket) throws IOException {
        Socket socketClient = serverSocket.accept();
        ServerExecutor serverExecutor = new ServerExecutor(socketClient, logger, pathResolver);
        executorService.submit(serverExecutor);
    }

}
