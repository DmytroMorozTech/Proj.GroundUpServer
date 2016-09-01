package co.kukurin.server.request;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

// TODO implementing file parsing
// (e.g. return "file.html";)

public class RegularFileResolver {

    private final String serverBaseDirectory;

    public RegularFileResolver(String serverBaseDirectory) {
        this.serverBaseDirectory = serverBaseDirectory;
    }

    public byte[] getContents(String location) throws IOException {
        Path requestedPath = resolveRequestedPath(location);
        return Files.readAllBytes(requestedPath);
    }

    private Path resolveRequestedPath(String location) {
        return Paths.get(serverBaseDirectory, location);
    }

}
