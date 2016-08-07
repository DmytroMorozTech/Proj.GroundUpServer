package co.kukurin.server.request;

import co.kukurin.server.environment.InitializationConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static co.kukurin.server.environment.InitializationConstants.*;

public class PathResolver {

    private final String serverBaseDirectory;
    private final Map<String, String> resourceToActualFile;

    public PathResolver(String serverBaseDirectory,
                        Map<String, String> resourceToActualFile) {
        this.serverBaseDirectory = serverBaseDirectory;
        this.resourceToActualFile = resourceToActualFile;
    }

    public Path getResourcePath(String resource) {
        String actual = Optional
                .ofNullable(resourceToActualFile.get(resource))
                .orElse(null);
        return Paths.get(serverBaseDirectory + actual);
    }

    public byte[] getErrorMessage() {
        Path errorLocationPath = getResourcePath(DEFAULT_ERROR_FILE_KEY);
        return Optional
                .ofNullable(getBytes(errorLocationPath))
                .orElse("Error accessing file".getBytes());
    }

    private byte[] getBytes(Path errorLocationPath) {
        try { return Files.readAllBytes(errorLocationPath); }
        catch (IOException ignorable) { return null; }
    }
}
