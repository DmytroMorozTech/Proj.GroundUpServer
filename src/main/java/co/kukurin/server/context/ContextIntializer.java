package co.kukurin.server.context;

import co.kukurin.server.ServerLogger;
import co.kukurin.server.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ContextIntializer {

    public static final char PACKAGE_SEPARATOR = '.';

    private final ClassLoader classLoader;
    private final Map<String, Class<?>> resourceStringToClassHandler;
    private final ServerLogger logger;

    public ContextIntializer(Class<?> applicationMainClass,
                             ServerLogger logger) throws IOException {
        this.classLoader = applicationMainClass.getClassLoader();
        this.logger = logger;

        String applicationMainPackage = extractPackageFromClassname(applicationMainClass);
        this.resourceStringToClassHandler = searchForClassesInPackage(applicationMainPackage);
    }

    private String extractPackageFromClassname(Class<?> applicationMainClass) {
        String fqcn = applicationMainClass.getName();
        return fqcn.substring(0, fqcn.lastIndexOf(PACKAGE_SEPARATOR));
    }

    private Map<String, Class<?>> searchForClassesInPackage(String packageName) throws IOException {
        logger.info("Loading resources...");

        String path = packageName.replace(PACKAGE_SEPARATOR, File.separatorChar);
        Enumeration<URL> resources = classLoader.getResources(path);
        ResourceMapPopulatingVisitor resourceMapPopulatingVisitor = new ResourceMapPopulatingVisitor(packageName);

        while (resources.hasMoreElements()) {
            try {
                Path resourcePath = Paths.get(resources.nextElement().toURI());
                Files.walkFileTree(resourcePath, resourceMapPopulatingVisitor);
            } catch (URISyntaxException shouldNeverOccur) {}
        }

        logger.info("Loaded: ", resourceMapPopulatingVisitor.getResourceNameToClassMap());
        return resourceMapPopulatingVisitor.getResourceNameToClassMap();
    }

    public Map<String, Resource> scan() throws ClassNotFoundException {
        Map<String, Resource> newMap = new HashMap<>();
        resourceStringToClassHandler.forEach((k,v) -> newMap.put(k, new Resource(v)));
        return newMap;
    }

}
