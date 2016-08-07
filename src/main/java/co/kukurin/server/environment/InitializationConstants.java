package co.kukurin.server.environment;

public final class InitializationConstants {

    static final String PROPERTIES_PATH = "src/main/java/resources/config.properties";

    public static final Integer DEFAULT_PORT = 8080;
    public static final String PORT_KEY = "server.port";
    public static final Integer DEFAULT_NUM_SERVER_THREADS = 8;
    public static final String NUM_SERVER_THREADS_KEY = "server.nthreads";

    private InitializationConstants() { throw new UnsupportedOperationException("Class should not be instantiated"); }

}
