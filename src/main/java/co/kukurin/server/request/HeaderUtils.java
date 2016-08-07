package co.kukurin.server.request;

import co.kukurin.helpers.ExceptionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HeaderUtils {

    private static final int READ_DOUBLE_NEWLINE_STATE = 5;
    public static final int ASCII_CR = 13;
    public static final int ASCII_LF = 10;

    private HeaderUtils() {
        ExceptionUtils.throwNonInstantiable();
    }

    static Headers fromInputStream(InputStream inputStream) throws IOException {
        List<String> headerLines = inputStreamToLinesAlwaysIncludingRequestBodyAsLastItem(inputStream);
        requireHeaderLinesToSpecifyRequest(headerLines);

        String[] requestTypeAndResourceAndProtocol = headerLines.get(0).trim().split("\\s+");
        Map<String, String> properties = extractProperties(headerLines);
        String body = extractBody(headerLines);

        return new Headers(requestTypeAndResourceAndProtocol[0],
                           requestTypeAndResourceAndProtocol[1],
                           requestTypeAndResourceAndProtocol[2],
                           properties,
                           body);
    }

    static List<String> inputStreamToLinesAlwaysIncludingRequestBodyAsLastItem(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int currentState = 0;

        while(true) {
            int currentCharacter = inputStream.read();
            if(currentCharacter == -1)
                throw new RuntimeException("Client closed the request early."); // TODO MalformedRequestException

            if(currentCharacter != ASCII_CR)
                outputStream.write(currentCharacter);

            currentState = determineNextState(currentState, currentCharacter);
            if(currentState == READ_DOUBLE_NEWLINE_STATE) {
                if(inputStream.available() > 0) currentState = 0;
                else break;
            }
        }

        return toLines(outputStream);
    }

    private static int determineNextState(int state, int character) {
        switch(state) {
            case 0:
                if(character == ASCII_CR) state = 1;
                else if(character == ASCII_LF) state = 4;
                break;
            case 1:
                if(character == ASCII_LF) state=2;
                else state=0;
                break;
            case 2:
                if(character == ASCII_CR) state = 3;
                else state=0;
                break;
            case 3:
                if(character == ASCII_LF) state = READ_DOUBLE_NEWLINE_STATE;
                else state=0;
                break;
            case 4:
                if(character == ASCII_LF) state = READ_DOUBLE_NEWLINE_STATE;
                else state=0;
                break;
        }

        return state;
    }

    private static void requireHeaderLinesToSpecifyRequest(List<String> headerLines) {
        if(headerLines.size() < 2)
            throw new RuntimeException("Client sent an empty request");
    }

    static Map<String, String> extractProperties(List<String> headerLines) {
        return headerLines.stream()
                .skip(1)
                .limit(headerLines.size() - 2)
                .map(line -> line.split(":"))
                .collect(Collectors.toMap((str) -> str[0], (str) -> str[1].trim()));
    }

    static String extractBody(List<String> headerLines) {
        return headerLines.get(headerLines.size() - 1);
    }

    static List<String> toLines(ByteArrayOutputStream outputStream) {
        String fromStream = new String(outputStream.toByteArray());
        String[] headersAndBody = fromStream.split("\\n\\n");

        ArrayList<String> asList = new ArrayList<>(Arrays.asList(headersAndBody[0].split("\\n")));
        asList.add(bodyOrEmpty(headersAndBody));
        return asList;
    }

    static String bodyOrEmpty(String[] headersAndBody) {
        return headersAndBody.length > 1 ? headersAndBody[1] : null;
    }
}
