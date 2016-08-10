package co.kukurin.server.request;

import co.kukurin.server.resource.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

public class PathResolver {

    // private final String serverBaseDirectory;
    private final Map<String, Resource> resourceToActualFile;

    public PathResolver(String serverBaseDirectory,
                        Map<String, Resource> resourceToActualFile) {
        // this.serverBaseDirectory = serverBaseDirectory;
        this.resourceToActualFile = resourceToActualFile;
    }

    public byte[] getResourceResponse(String resource, HttpConstants.Method method) {
        Resource requestedResource = resourceToActualFile.get(resource);
        Object response = requestedResource.get();

        return deserialize(response);
    }

    private byte[] deserialize(Object response) {
        String fqcn = response.getClass().toString();
        return fqcn
                .substring(fqcn.lastIndexOf(".") + 1)
                .getBytes();
    }

}
