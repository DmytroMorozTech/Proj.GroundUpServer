package co.kukurin.helpers;

public class ExceptionUtils {

    public static void throwNonInstantiable() {
        throw new UnsupportedOperationException("Object should not be instantiated.");
    }

}
