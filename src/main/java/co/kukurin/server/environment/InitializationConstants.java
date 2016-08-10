package co.kukurin.server.environment;

import static co.kukurin.helpers.ExceptionUtils.throwNonInstantiable;

public final class InitializationConstants {

    static final String PROPERTIES_PATH = "./src/main/resources/config.properties";
    public static final byte[] DEFAULT_ERROR_MESSAGE_BYTES = "Error accessing file".getBytes();

    public static final Integer DEFAULT_PORT = 8080;

    public static final String PORT_KEY = "server.port";
    public static final Integer DEFAULT_NUM_SERVER_THREADS = 8;

    public static final String NUM_SERVER_THREADS_KEY = "server.nthreads";
    public static final String WEB_BASE_DIR_KEY = "server.basedir";

    public static final String DEFAULT_WEB_BASE_DIR = "web";
    public static final String DEFAULT_ERROR_FILE_KEY = "error";

    private InitializationConstants() {
        throwNonInstantiable();
    }

}
