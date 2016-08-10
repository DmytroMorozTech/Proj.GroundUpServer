package co.kukurin.server.request.headers;

import co.kukurin.custom.Optional;
import co.kukurin.server.exception.ServerException;
import co.kukurin.server.request.HttpConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static co.kukurin.server.request.HttpConstants.*;
import static co.kukurin.server.request.HttpConstants.Ascii.*;
import static co.kukurin.server.request.HttpConstants.HeaderPropertyKeys.CONTENT_LENGTH;
import static co.kukurin.server.request.HttpConstants.HeaderPropertyKeys.KEY_VALUE_SPLITTER;
import static co.kukurin.server.request.headers.HeaderRequestReadState.*;

public class Headers {

    private Method requestMethod;
    private String resource;
    private String requestProtocol;
    private Map<String, String> properties;
    private String body;

    private Headers() {
        this.properties = new HashMap<>();
    }

    public static Headers fromInputStream(InputStream inputStream) throws IOException {
        try(ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            return new Headers().fromInputStream(inputStream, outputStream);
        }
    }

    private Headers fromInputStream(InputStream inputStream, ByteArrayOutputStream outputStream) throws IOException {
        HeaderRequestReadState currentState = INITIAL;
        currentState = parseFromFirstHeaderLine(currentState, inputStream, outputStream);
        currentState = parseFromProperties(currentState, inputStream, outputStream);

        if(methodCouldContainRequestBody())
            parseFromRequestBody(currentState, inputStream, outputStream);

        return this;
    }

    private HeaderRequestReadState parseFromFirstHeaderLine(HeaderRequestReadState currentState,
                                                            InputStream inputStream,
                                                            ByteArrayOutputStream outputStream) throws IOException {
        final int expectedTokensInFirstLine = 3;
        final String[] methodAndResourceAndProtocol = new String[expectedTokensInFirstLine];
        int parsedIterator = 0;

        while(true) {
            int currentCharacter = getFromStreamOrThrowIfClosed(inputStream);
            writeCharacterIfNotOneOfForbidden(outputStream, currentCharacter, CARRIAGE_RETURN, LINE_FEED, SPACE);
            currentState = determineNextState(currentState, currentCharacter);

            boolean shouldFlushStream = currentCharacter == SPACE || isSingleNewlineState(currentState);
            if(shouldFlushStream) {
                if(parsedIterator >= expectedTokensInFirstLine)
                    throw new MalformedRequestException("First line contained unexpected tokens");

                methodAndResourceAndProtocol[parsedIterator++] = flushAndResetStream(outputStream);
            }

            if(isSingleNewlineState(currentState)) {
                if(parsedIterator != expectedTokensInFirstLine)
                    throw new MalformedRequestException("First line has missing tokens.");

                outputStream.reset();
                break;
            }
        }

        this.requestMethod = Method.valueOf(methodAndResourceAndProtocol[0]);
        this.resource = methodAndResourceAndProtocol[1];
        this.requestProtocol = methodAndResourceAndProtocol[2];
        return currentState;
    }

    private HeaderRequestReadState parseFromProperties(HeaderRequestReadState currentState,
                                                       InputStream inputStream,
                                                       ByteArrayOutputStream outputStream) throws IOException {
        while(!isDoubleNewlineState(currentState)) {
            int currentCharacter = getFromStreamOrThrowIfClosed(inputStream);
            writeCharacterIfNotOneOfForbidden(outputStream, currentCharacter, CARRIAGE_RETURN, LINE_FEED);
            currentState = determineNextState(currentState, currentCharacter);

            boolean shouldTryToMapAsProperty = isSingleNewlineState(currentState);
            if(shouldTryToMapAsProperty) {
                String[] keyAndValue = splitCurrentBufferIntoKeyAndValue(outputStream);
                properties.put(keyAndValue[0], keyAndValue[1]);
            }
        }

        return currentState;
    }

    private boolean isDoubleNewlineState(HeaderRequestReadState currentState) {
        return currentState == READ_DOUBLE_NEWLINE;
    }

    private String[] splitCurrentBufferIntoKeyAndValue(ByteArrayOutputStream outputStream) {
        final int keyAndValueExpectedSize = 2;
        String[] keyAndValue = flushAndResetStream(outputStream).split(KEY_VALUE_SPLITTER, keyAndValueExpectedSize);
        if(keyAndValue.length != keyAndValueExpectedSize)
            throw new MalformedRequestException("Unable to split request data into key and value");
        return keyAndValue;
    }

    private HeaderRequestReadState parseFromRequestBody(HeaderRequestReadState currentState,
                                                        InputStream inputStream,
                                                        ByteArrayOutputStream outputStream) throws IOException {
        int contentLength = requireExistingContentLengthProperty();

        while(contentLength-- > 0) {
            int currentCharacter = getFromStreamOrThrowIfClosed(inputStream);
            outputStream.write(currentCharacter);
        }

        this.body = flushAndResetStream(outputStream);
        return currentState;
    }

    private int requireExistingContentLengthProperty() {
        return Optional.ofNullable(properties.get(CONTENT_LENGTH))
                .map(String::trim)
                .map(Integer::parseInt)
                .orElseThrow(() -> new MalformedRequestException("Missing valid content length"));
    }

    private int getFromStreamOrThrowIfClosed(InputStream inputStream) throws IOException {
        int current = inputStream.read();
        if(current == -1)
            throw new MalformedRequestException("Connection closed prematurely.");
        return current;
    }

    private String flushAndResetStream(ByteArrayOutputStream outputStream) {
        String contents = new String(outputStream.toByteArray());
        outputStream.reset();

        return contents;
    }

    private void writeCharacterIfNotOneOfForbidden(ByteArrayOutputStream outputStream,
                                                   int currentCharacter,
                                                   int ... forbiddenCharacters) {
        for(int forbiddenCharacter : forbiddenCharacters)
            if(currentCharacter == forbiddenCharacter)
                return;

        outputStream.write(currentCharacter);
    }

    private boolean methodCouldContainRequestBody() {
        return !requestMethod.equals(HttpConstants.Method.GET);
    }

    private boolean isSingleNewlineState(HeaderRequestReadState currentState) {
        return currentState == READ_CR_LF || currentState == READ_LF;
    }

    public Method getRequestMethod() {
        return requestMethod;
    }

    public String getResource() {
        return resource;
    }

    public String getRequestProtocol() {
        return requestProtocol;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getBody() {
        return body;
    }
}
