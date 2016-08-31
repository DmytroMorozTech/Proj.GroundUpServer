package co.kukurin.server.resource;

public class Resource {

    // TODO private Map Http.method -> class method
    // TODO get rid of clazz and getClazz

    private Class<?> clazz;

    public Resource(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        return clazz;
    }

}
