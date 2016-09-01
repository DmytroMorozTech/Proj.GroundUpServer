package co.kukurin.server.response;

import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Method;

@Getter
@ToString
public class ResourceResponse {

    // TODO private Map Http.method -> class method?
    // TODO get rid of clazz and getClazz

    private final Object methodOwner;
    private final Method method;

    public ResourceResponse(Object methodOwner, Method method) {
        this.methodOwner = methodOwner;
        this.method = method;
    }

}
