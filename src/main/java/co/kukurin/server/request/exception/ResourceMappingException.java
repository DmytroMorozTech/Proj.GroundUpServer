package co.kukurin.server.request.exception;

import co.kukurin.server.annotations.ResourceMapping;
import co.kukurin.server.exception.ServerException;

public class ResourceMappingException extends ServerException {

    public ResourceMappingException(ResourceMapping mapping) {
        super("Failed to map resource: " + mapping);
    }

}
