package co.kukurin.server.environment;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.server.Server;
import co.kukurin.server.ServerLogger;
import co.kukurin.server.request.PathResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static co.kukurin.server.environment.InitializationConstants.*;

public class ServerEnvironmentImpl implements ServerEnvironment {

    private final ServerProperties properties;
    private final ServerLogger logger;
    private final ExecutorService executorService;
    private final PathResolver pathResolver;
    private final Server server;

    @SuppressWarnings("unused") // no usage of class for now, maybe will add support for property finding via annotations.
    public ServerEnvironmentImpl(Class<?> applicationMainClass) {
        this.logger = ServerLogger.getInstance();
        this.properties = loadProperties();
        this.executorService = getExecutorService();
        this.pathResolver = getPathResolver();
        this.server = new Server(logger, properties, executorService, pathResolver);
    }

    private ServerProperties loadProperties() {
        logger.info("Looking for server properties...");

        Properties properties = loadIfConfigPresent()
                .orElseGet(() -> {
                    logger.info("No property file found.");
                    return new Properties();
                });
        return new ServerProperties(properties);
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

    private ExecutorService getExecutorService() {
        int nThreads = properties.getOrDefaultInt(NUM_SERVER_THREADS_KEY, DEFAULT_NUM_SERVER_THREADS);
        return Executors.newFixedThreadPool(nThreads);
    }

    private PathResolver getPathResolver() {
        String serverBaseDirectory = properties.getOrDefaultString(WEB_BASE_DIR_KEY, DEFAULT_WEB_BASE_DIR);
        Map<String, String> resourceToActualFile = getServerResources(serverBaseDirectory);
        return new PathResolver(serverBaseDirectory, resourceToActualFile);
    }

    private Map<String, String> getServerResources(String serverBaseDirectory) {
        try {
            Map<String, String> map = Files.walk(Paths.get(serverBaseDirectory))
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toMap(
                            file -> "/" + ((Path) file).getFileName().toString(), // TODO String -> class
                            file -> "/" + ((Path) file).getFileName().toString()));
            map.put("/", "/index.html");
            return map;
        } catch(IOException e) {
            logger.error("Resource mappings not found", e);
        }

        return null;
    }

    @Override
    public void run(String ... args) {
        server.start(args);
    }
}
