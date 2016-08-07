package co.kukurin.server.request;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.BDDAssertions.*;
import static org.junit.Assert.assertTrue;

public class HeadersTest {

    static final String AUTHORIZATION_KEY = "Authorization";
    static final String AUTHORIZATION_VALUE = "Basic 123";
    static final String AUTHORIZATION_HEADER_LINE = AUTHORIZATION_KEY + ":" + AUTHORIZATION_VALUE;
    public static final String HEADER_BODY = "GivenBody";

    @Test
    public void shouldParseSimpleHeaders() throws Exception {
        // given
        String givenHeaders = getGivenHeaders();

        // when
        Headers whenHeaders = Headers.fromInputStream(createInputStream(givenHeaders));

        // then
        then(whenHeaders.getProperties().get("Authorization")).isEqualTo(AUTHORIZATION_VALUE);
        then(whenHeaders.getRequestType()).isEqualTo("GET");
    }

    @Test
    public void shouldParseHeadersWithBody() throws Exception {
        // given
        String givenHeaders = postGivenHeaders();

        // when
        Headers whenHeaders = Headers.fromInputStream(createInputStream(givenHeaders));

        // then
        then(whenHeaders.getRequestType()).isEqualTo("POST");
        then(whenHeaders.getResource()).isEqualTo("/");
        then(whenHeaders.getRequestProtocol()).isEqualTo("HTTP/1.1");
        then(whenHeaders.getBody()).isEqualTo(HEADER_BODY);
    }

    private InputStream createInputStream(String givenInvalidHeaders) {
        return new ByteArrayInputStream(givenInvalidHeaders.getBytes());
    }

    private String postGivenHeaders() {
        return "POST / HTTP/1.1\n"
                + AUTHORIZATION_HEADER_LINE
                + "\n\n"
                + HEADER_BODY;
    }

    private String getGivenHeaders() {
        return "GET / HTTP/1.1\n"
                + AUTHORIZATION_HEADER_LINE
                + "\n\n";
    }

    public String getGivenInvalidHeaders() {
        return "GET / HTTP/1.1"
                + AUTHORIZATION_HEADER_LINE;
    }
}
