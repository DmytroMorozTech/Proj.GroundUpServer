package co.kukurin;

import co.kukurin.server.resource.Resource;

public class Service extends Resource {

    @Override
    public Object get() {
        return "Sample response.";
    }
}
