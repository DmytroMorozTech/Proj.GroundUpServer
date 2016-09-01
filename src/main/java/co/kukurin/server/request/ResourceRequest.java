package co.kukurin.server.request;

import co.kukurin.server.request.HttpConstants.HttpMethod;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@Value
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ResourceRequest {

    private HttpMethod httpMethod;
    private String resourcePath;

}
