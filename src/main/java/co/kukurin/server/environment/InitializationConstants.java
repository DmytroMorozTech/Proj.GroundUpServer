package co.kukurin.server.environment;

import co.kukurin.helpers.ExceptionUtils;

public final class InitializationConstants {

    static final String PROPERTIES_PATH = "./src/main/resources/config.properties";

    public static final Integer DEFAULT_PORT = 8080;
    public static final String PORT_KEY = "server.port";
    public static final Integer DEFAULT_NUM_SERVER_THREADS = 8;
    public static final String NUM_SERVER_THREADS_KEY = "server.nthreads";
    public static final String WEB_BASE_DIR_KEY = "server.basedir";
    public static final String DEFAULT_WEB_BASE_DIR = "web";

    private InitializationConstants() {
        ExceptionUtils.throwNonInstantiable();
    }

}
