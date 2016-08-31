package co.kukurin.server.environment;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.server.Server;
import co.kukurin.server.ServerLogger;
import co.kukurin.server.context.ContextIntializer;
import co.kukurin.server.request.RegularFileResolver;
import co.kukurin.server.request.ResourceResolver;
import co.kukurin.server.request.ResourceResolverImpl;
import co.kukurin.server.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static co.kukurin.server.environment.InitializationConstants.*;

public class ServerEnvironmentImpl implements ServerEnvironment {

    private final ServerProperties properties;
    private final ServerLogger logger;
    private final ExecutorService executorService;
    private final ResourceResolver resourceResolver;
    private final Server server;

    public ServerEnvironmentImpl(Class<?> applicationMainClass) {
        this.logger = ServerLogger.getInstance();
        this.properties = loadProperties();
        this.executorService = getExecutorService();
        this.resourceResolver = getResourceResolver(applicationMainClass);
        this.server = new Server(logger, properties, executorService, resourceResolver);
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

    private ResourceResolver getResourceResolver(Class<?> applicationMainClass) {
        String serverBaseDirectory = properties.getOrDefaultString(WEB_BASE_DIR_KEY, DEFAULT_WEB_BASE_DIR);
        RegularFileResolver regularFileResolver = new RegularFileResolver(serverBaseDirectory);

        return new ResourceResolverImpl(regularFileResolver, getServerResources(applicationMainClass), logger);
    }

    private Map<String, Resource> getServerResources(Class<?> applicationMainClass) {
        // TODO ContextInitializationException
        ContextIntializer contextIntializer = ErrorHandler
                .optionalResult(() -> new ContextIntializer(applicationMainClass, logger))
                .orElseThrow(RuntimeException::new);
        return ErrorHandler.optionalResult(contextIntializer::scan).orElseThrow(RuntimeException::new);
    }

    @Override
    public void run(String ... args) {
        server.start(args);
    }
}
