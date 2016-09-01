package co.kukurin.server.context;

import java.lang.annotation.*;

import static co.kukurin.server.request.HttpConstants.HttpMethod;

@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ResourceMapping {

    String resourcePath();
    HttpMethod method() default HttpMethod.GET;

}
