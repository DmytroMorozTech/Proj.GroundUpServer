package co.kukurin.server.request;

import co.kukurin.server.ServerLogger;
import co.kukurin.server.resource.Resource;

import java.io.IOException;
import java.util.Map;

public class ResourceResolverImpl implements ResourceResolver {

    // not really used anywhere
    private final RegularFileResolver regularFileResolver;
    private final Map<String, Resource> resourceToActualFile;
    private final ServerLogger logger;

    public ResourceResolverImpl(RegularFileResolver regularFileResolver,
                                Map<String, Resource> resourceToActualFile,
                                ServerLogger logger) {
        this.regularFileResolver = regularFileResolver;
        this.resourceToActualFile = resourceToActualFile;
        this.logger = logger;
    }

    public byte[] getResponseBytes(String resource, HttpConstants.Method method) throws IOException {
        Resource requestedResource = resourceToActualFile.get(withoutStartingForwardSlash(resource));
        Object response = tryRequestedMethodOnResource(requestedResource, method);

        return deserialize(response);
    }

    private String withoutStartingForwardSlash(String resource) {
        return resource.charAt(0) == '/' ? resource.substring(1) : resource;
    }

    // TODO this should actually be done during context init.
    private Object tryRequestedMethodOnResource(Resource requestedResource, HttpConstants.Method httpMethod) {
        Class<?> requestedClass = requestedResource.getClazz();

        logger.info("Initializing class", requestedClass);

        try {
            Object instance = requestedClass.getConstructor().newInstance();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestedClass.toString();
    }

    private byte[] deserialize(Object response) throws IOException {
        if(isString(response)) {
            // return regularFileResolver.getContents((String) response);
            return ((String)response).getBytes();
        } else {
            throw new IllegalArgumentException("test");
        }
    }

    private boolean isString(Object response) {
        return response instanceof String;
    }

}
