package co.kukurin.helpers;

import static co.kukurin.helpers.ExceptionUtils.*;

public class ResourceSanitizer {

    private ResourceSanitizer() {
        throwNonInstantiable();
    }

    public static String sanitizeResourceName(String resourceName) {
        return resourceName.toLowerCase();
    }

}
