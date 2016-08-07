package co.kukurin.server.request.headers;

import co.kukurin.helpers.ExceptionUtils;
import co.kukurin.server.exception.MalformedRequestException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static co.kukurin.server.request.HttpConstants.Ascii.CR;

class HeaderParser {

    private HeaderParser() {
        ExceptionUtils.throwNonInstantiable();
    }

    static Headers fromInputStream(InputStream inputStream) throws IOException {
        List<String> headerLines = inputStreamToLinesAlwaysIncludingRequestBodyAsLastItem(inputStream);
        requireHeaderLinesToSpecifyRequest(headerLines);

        String[] requestTypeAndResourceAndProtocol = headerLines.get(0).trim().split("\\s+");
        requireFirstLineToSpecifyRequestTypeAndResourceAndProtocol(requestTypeAndResourceAndProtocol);

        Map<String, String> properties = extractProperties(headerLines);
        String body = extractBody(headerLines);

        return new Headers(requestTypeAndResourceAndProtocol[0],
                           requestTypeAndResourceAndProtocol[1],
                           requestTypeAndResourceAndProtocol[2],
                           properties,
                           body);
    }

    private static List<String> inputStreamToLinesAlwaysIncludingRequestBodyAsLastItem(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HeaderRequestReadState currentState = HeaderRequestReadState.INITIAL;

        while(true) {
            int currentCharacter = inputStream.read();

            if(currentCharacter == -1)
                throw new MalformedRequestException("Client closed the request prematurely.");
            else if(currentCharacter != CR.getIntValue())
                outputStream.write(currentCharacter);

            currentState = HeaderRequestReadState.determineNextState(currentState, currentCharacter);
            if(currentState == HeaderRequestReadState.READ_DOUBLE_NEWLINE) {
                if(inputStream.available() > 0)
                    currentState = HeaderRequestReadState.INITIAL;
                else break;
            }
        }

        return toLines(outputStream);
    }

    private static void requireHeaderLinesToSpecifyRequest(List<String> headerLines) {
        if(headerLines.size() < 2)
            throw new RuntimeException("Client sent an empty request");
    }

    private static void requireFirstLineToSpecifyRequestTypeAndResourceAndProtocol(String[] firstLine) {
        if(firstLine.length != 3)
            throw new MalformedRequestException("Received malformed request: " + String.join(" ", Arrays.asList(firstLine)));
    }

    private static Map<String, String> extractProperties(List<String> headerLines) {
        int numberOfElementsWithoutFirstLineAndRequestBody = headerLines.size() - 2;
        int firstLine = 1;
        String headerKeyToValueSplitString = ":";
        return headerLines.stream()
                .skip(firstLine)
                .limit(numberOfElementsWithoutFirstLineAndRequestBody)
                .map(line -> line.split(headerKeyToValueSplitString, 2))
                .collect(Collectors.toMap((str) -> str[0], (str) -> str[1].trim()));
    }

    private static String extractBody(List<String> headerLines) {
        return headerLines.get(headerLines.size() - 1);
    }

    private static List<String> toLines(ByteArrayOutputStream outputStream) {
        String fromStream = new String(outputStream.toByteArray());
        String[] headersAndBody = fromStream.split("\\n\\n");

        ArrayList<String> asList = new ArrayList<>(Arrays.asList(headersAndBody[0].split("\\n")));
        asList.add(bodyOrEmpty(headersAndBody));
        return asList;
    }

    private static String bodyOrEmpty(String[] headersAndBody) {
        return headersAndBody.length > 1 ? headersAndBody[1] : null;
    }
}
