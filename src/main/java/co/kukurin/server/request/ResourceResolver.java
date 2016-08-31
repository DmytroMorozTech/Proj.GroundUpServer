package co.kukurin.server.request;

import java.io.IOException;

public interface ResourceResolver {

    byte[] getResponseBytes(String resource, HttpConstants.Method method) throws IOException;

}
