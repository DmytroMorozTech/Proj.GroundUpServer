package co.kukurin.server.request.headers;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Getter
@AllArgsConstructor
public class Headers {

    private String requestType;
    private String resource;
    private String requestProtocol;
    private Map<String, String> properties;
    private String body;

    public static Headers fromInputStream(InputStream inputStream) throws IOException {
        return HeaderParser.fromInputStream(inputStream);
    }

}
