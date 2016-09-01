package co.kukurin;

import co.kukurin.server.annotations.ResourceMapping;
import co.kukurin.server.environment.ServerEnvironment;
import co.kukurin.server.environment.ServerEnvironmentImpl;
import com.sun.deploy.net.BasicHttpRequest;

import java.io.IOException;
import java.net.URL;

public class Application {

    public static void main(String[] args) throws IOException {
        ServerEnvironment environment = new ServerEnvironmentImpl(Application.class);
        environment.run();
    }

    @ResourceMapping(resourcePath = "/")
    public String testHandleRequest() {
        return "Hey there.";
    }

    private static void testConnection() throws IOException {
        BasicHttpRequest request = new BasicHttpRequest();
        URL connectUrl = new URL("http", "localhost", 80, "/");

        request.doGetRequest(connectUrl);
    }

}
