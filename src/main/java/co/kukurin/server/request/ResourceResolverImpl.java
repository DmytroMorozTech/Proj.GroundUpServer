package co.kukurin.server.request;

import co.kukurin.custom.ErrorHandler;
import co.kukurin.custom.Optional;
import co.kukurin.server.logging.ServerLoggerImpl;
import co.kukurin.server.response.ResourceResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class ResourceResolverImpl implements ResourceResolver {

    private final Map<ResourceRequest, ResourceResponse> requestHandler;
    private final ServerLoggerImpl logger;

    public ResourceResolverImpl(Map<ResourceRequest, ResourceResponse> requestHandler,
                                ServerLoggerImpl logger) {
        this.requestHandler = requestHandler;
        this.logger = logger;
    }

    public byte[] getResponseBytes(ResourceRequest request) throws IOException {
        Object response = Optional.ofNullable(requestHandler.get(request))
                .map(resourceResponse -> this.tryRequestedMethodOnResource(request, resourceResponse))
                .orElseGet(() -> "Error.");

        return deserialize(response);
    }

    // TODO actual method parameters, etc.
    private Object tryRequestedMethodOnResource(ResourceRequest request, ResourceResponse resourceResponse) {
        Method method = resourceResponse.getMethod();

        return ErrorHandler
                .optionalResult(() -> method.invoke(resourceResponse.getMethodOwner()))
                .orElseGet(() -> {
                    logger.info("Error processing request", request);
                    return null;
                });
    }

    private byte[] deserialize(Object response) throws IOException {
        if(isString(response)) {
            return ((String)response).getBytes();
        } else {
            throw new IllegalArgumentException("test");
        }
    }

    private boolean isString(Object response) {
        return response instanceof String;
    }

}
