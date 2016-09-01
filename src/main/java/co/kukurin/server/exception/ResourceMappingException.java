package co.kukurin.server.exception;

import co.kukurin.server.annotations.ResourceMapping;

public class ResourceMappingException extends ServerException {

    public ResourceMappingException(ResourceMapping mapping) {
        super("Failed to map resource: " + mapping);
    }

}
