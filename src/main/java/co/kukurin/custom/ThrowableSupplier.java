package co.kukurin.custom;


public interface ThrowableSupplier<T> {

    T get() throws Exception;

}
