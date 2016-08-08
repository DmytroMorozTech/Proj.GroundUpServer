package co.kukurin.server.request;

import co.kukurin.helpers.ExceptionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class HttpConstants {

    public enum Method {
        GET, POST, PUT, DELETE
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

    private HttpConstants() {
        ExceptionUtils.throwNonInstantiable();
    }

}
