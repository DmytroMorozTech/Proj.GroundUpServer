package co.kukurin.server.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class HttpConstants {

    public enum Method { GET, POST, PUT, DELETE }

    @Getter
    @AllArgsConstructor
    public enum Ascii {
        CR(13), LF(10);
        private int intValue;
    }

}
