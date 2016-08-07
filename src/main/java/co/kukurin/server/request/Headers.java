package co.kukurin.server.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static co.kukurin.server.request.HeaderUtils.*;

@Getter
@AllArgsConstructor
public class Headers {

    private String requestType;
    private String resource;
    private String requestProtocol;
    private Map<String, String> properties;
    private String body;

    public static Headers fromInputStream(InputStream inputStream) throws IOException {
        return HeaderUtils.fromInputStream(inputStream);
    }

}
