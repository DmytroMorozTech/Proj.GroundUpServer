package co.kukurin.server.context;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.server.ServerLogger;
import co.kukurin.server.request.ResourceRequest;
import co.kukurin.server.response.ResourceResponse;
import lombok.Getter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static co.kukurin.server.context.ContextIntializer.PACKAGE_SEPARATOR;
import static co.kukurin.server.request.HttpConstants.HttpMethod;

public class ResourceMapPopulatingVisitor extends SimpleFileVisitor<Path> {

    private final String packageName;
    private Map<ResourceRequest, ResourceResponse> resourceHandler;
    private final ServerLogger logger;

    ResourceMapPopulatingVisitor(String packageName,
                                 ServerLogger logger) {
        this.packageName = packageName;
        this.logger = logger;
    }

    public Map<ResourceRequest, ResourceResponse> getResourceHandler() {
        return resourceHandler;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        boolean hasAlreadyVisitedBasePackage = this.resourceHandler != null;

        if(!hasAlreadyVisitedBasePackage) {
            this.resourceHandler = new HashMap<>();
            return FileVisitResult.CONTINUE;
        }

        return FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        ErrorHandler
                .optionalResult(() -> classFromPath(file))
                .ifPresent(this::putIfHasValidMethodMappings)
                .orElseDo(() -> logger.info("couldn't load class for file " + file));

        return super.visitFile(file, attrs);
    }

    private Class<?> classFromPath(Path file) throws ClassNotFoundException {
        return Class.forName(this.packageName + PACKAGE_SEPARATOR + classNameFromPath(file));
    }

    private void putIfHasValidMethodMappings(Class<?> clazz) {
        ErrorHandler
                .catchIfThrows(() ->  {
                    Object classInstance = clazz.newInstance();
                    parseClassMethods(classInstance, clazz.getDeclaredMethods()); })
                .handleExceptionAs(e -> logger.error("could not instantiate class", e));
    }

    private void parseClassMethods(Object classInstance, Method[] methods) {
        Arrays.stream(methods)
                .forEach(method -> {
                    Map<Class<? extends Annotation>, Annotation> methodAnnotations =
                            Arrays.stream(method.getDeclaredAnnotations())
                                    .collect(Collectors.toMap(Annotation::annotationType, Function.identity()));

                    Optional.ofNullable(methodAnnotations.get(ResourceMapping.class))
                            .map(obj -> (ResourceMapping)obj)
                            .ifPresent(annotation -> storeResourceMappingOrThrowIfAlreadyPresent(classInstance, method, annotation));
                });
    }

    private void storeResourceMappingOrThrowIfAlreadyPresent(Object methodOwner,
                                                             Method method,
                                                             ResourceMapping resourceMapping) {
        HttpMethod httpMethod = resourceMapping.method();
        String resourcePath = resourceMapping.resourcePath();

        ResourceRequest resourceRequest = new ResourceRequest(httpMethod, resourcePath);
        if(this.resourceHandler.get(resourceRequest) != null)
            throw new RuntimeException("Found duplicate mapping for resource: " + resourceRequest); // TODO DuplicateResourceMappingException

        method.setAccessible(true);
        ResourceResponse resourceResponse = new ResourceResponse(methodOwner, method);
        this.resourceHandler.put(resourceRequest, resourceResponse);
    }

    private String classNameFromPath(Path file) {
        final char fileExtensionSeparator = '.';
        String fileName = file.getFileName().toString();

        return fileName.substring(0, fileName.lastIndexOf(fileExtensionSeparator));
    }

}
