package co.kukurin.server.request;

import java.io.IOException;

public interface ResourceResolver {

    byte[] getResponseBytes(ResourceRequest request) throws IOException;

}
