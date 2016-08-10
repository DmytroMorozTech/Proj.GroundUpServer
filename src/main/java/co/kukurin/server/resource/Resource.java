package co.kukurin.server.resource;

import co.kukurin.server.exception.UnsupportedMethodException;

public class Resource {

    public Object get() {
        throw new UnsupportedMethodException();
    }

    public Object post() {
        throw new UnsupportedMethodException();
    }
    public Object put() {
        throw new UnsupportedMethodException();
    }
    
    public Object delete() {
        throw new UnsupportedMethodException();
    }

}
