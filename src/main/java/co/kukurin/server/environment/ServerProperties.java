package co.kukurin.server.environment;

import java.util.Optional;
import java.util.Properties;

public class ServerProperties {

    private Properties properties;

    public ServerProperties(Properties properties) {
        this.properties = properties;
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    public Integer getOrDefaultInt(String key, Integer defaultValue) {
        return Optional.ofNullable(properties.get(key))
                .map(obj -> (String) obj)
                .map(Integer::parseInt)
                .orElseGet(() -> defaultValue);
    }

    public String getOrDefaultString(String key, String defaultValue) {
        return (String) properties.getOrDefault(key, defaultValue);
    }

}
