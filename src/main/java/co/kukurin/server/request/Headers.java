package co.kukurin.server.request;

import com.sun.deploy.util.ArrayUtil;
import lombok.Getter;
import lombok.Value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class Headers {

    private static final int READ_BUFFER_SIZE = 1024;
    private String requestType;
    private String resource;
    private String requestProtocol;
    private Map<String, String> properties;
    private String body;

    public static Headers fromInputStream(InputStream inputStream) throws IOException {
        Headers headers = new Headers();
        List<String> headerLines = inputStreamToLines(inputStream);

        extractFromFirstLine(headers, headerLines.get(0));
        extractProperties(headers, headerLines);
        extractBody(headers, headerLines);
        return headers;
    }

    private static void extractFromFirstLine(Headers headers, String line) {
        String[] lineContents = line.split("\\s+");
        headers.requestType = lineContents[0];
        headers.resource = lineContents[1];
        headers.requestProtocol = lineContents[2];
    }

    private static void extractProperties(Headers headers, List<String> headerLines) {
        headers.properties = headerLines.stream()
                .skip(1)
                .limit(headerLines.size() - 2)
                .map(line -> line.split(":"))
                .collect(Collectors.toMap((str) -> str[0], (str) -> str[1].trim()));
    }

    private static void extractBody(Headers headers, List<String> headerLines) {
        headers.body = getLast(headerLines);
    }

    private static String getLast(List<String> headerLines) {
        return headerLines.get(headerLines.size() - 1);
    }

    private static List<String> inputStreamToLines(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        int readBytes;

        while((readBytes = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, readBytes);

            if(hasEndedWithDoubleNewline(buffer, readBytes))
                break;
        }

        return toLines(outputStream);
    }

    private static boolean hasEndedWithDoubleNewline(byte[] buffer, int readBytes) {
        boolean isLf = buffer[readBytes - 1] == 13 && buffer[readBytes - 2] == 13;
        boolean isCrLf = buffer[readBytes - 1] == 10 && buffer[readBytes - 2] == 13
                && buffer[readBytes - 3] == 10 && buffer[readBytes - 4] == 13;
        return isLf || isCrLf;
    }

    private static List<String> toLines(ByteArrayOutputStream outputStream) {
        String fromStream = new String(outputStream.toByteArray());
        String[] headersAndBody = fromStream.split("\\r?\\n\\r?\\n");

        List<String> asList = new ArrayList<String>(Arrays.asList(headersAndBody[0].split("\\r?\\n")));
        asList.add(bodyOrEmpty(headersAndBody));
        return asList;
    }

    private static String bodyOrEmpty(String[] headersAndBody) {
        return headersAndBody.length > 1 ? headersAndBody[1] : null;
    }

}
