package co.kukurin.server.request;

import static co.kukurin.helpers.ExceptionUtils.throwNonInstantiable;

public class HttpConstants {

    private HttpConstants() {
        throwNonInstantiable();
    }

    public enum HttpMethod {
        GET, POST, PUT, DELETE, UPDATE

    }
    public class Ascii {
        public static final int LINE_FEED = 10;
        public static final int CARRIAGE_RETURN = 13;
        public static final int SPACE = 32;

    }
    public class HeaderPropertyKeys {
        public static final String CONTENT_LENGTH = "content-length";
        public static final String KEY_VALUE_SPLITTER = ":";

    }

}
