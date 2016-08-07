package co.kukurin.server.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class HttpConstants {

    public enum Method {
        GET, POST, PUT, DELETE
    }

    @Getter
    @AllArgsConstructor
    public enum Ascii {
        LINE_FEED(10), CARRIAGE_RETURN(13);
        private int intValue;
    }

}
