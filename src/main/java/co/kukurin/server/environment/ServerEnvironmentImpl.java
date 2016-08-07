package co.kukurin.server.environment;

import co.kukurin.custom.Optional;
import co.kukurin.server.Server;
import co.kukurin.server.ServerLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static co.kukurin.server.environment.InitializationConstants.*;

public class ServerEnvironmentImpl implements ServerEnvironment {

    private final ServerProperties properties;
    private final ServerLogger logger;
    private final ExecutorService executorService;
    private final Server server;

    public ServerEnvironmentImpl(Class<?> clazz) {
        this.logger = ServerLogger.getInstance();
        this.properties = loadProperties();
        this.executorService = getExecutorService();
        this.server = new Server(logger, properties, executorService);
    }

    private ServerProperties loadProperties() {
        logger.info("Looking for server properties");

        Properties properties = loadIfConfigPresent()
                .orElseGet(() -> {
                    logger.info("No properties file found.");
                    return new Properties();
                });
        return new ServerProperties(properties);
    }

    private ExecutorService getExecutorService() {
        int nThreads = properties.getOrDefaultInt(NUM_SERVER_THREADS_KEY, DEFAULT_NUM_SERVER_THREADS);
        return Executors.newFixedThreadPool(nThreads);
    }

    private Optional<Properties> loadIfConfigPresent() {
        try(InputStream propertyInputStream = Files.newInputStream(Paths.get(PROPERTIES_PATH))) {
            Properties properties = new Properties();
            properties.load(propertyInputStream);

            logger.info("Loaded user-defined properties.");
            return Optional.of(properties);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void run(String ... args) {
        server.start(args);
    }
}
