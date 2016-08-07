package co.kukurin.custom;

import java.util.function.Consumer;

public class ErrorHandler {

    private Exception exception;

    private ErrorHandler(Exception exception) {
        this.exception = exception;
    }

    public void handleException(Consumer<Exception> handler) {
        if(this.exception != null)
            handler.accept(exception);
    }

    public void rethrowAsUncheckedFirstInvoking(Runnable doBeforeThrowing) {
        doBeforeThrowing.run();
        rethrowAsUnchecked();
    }

    public void rethrowAsUnchecked() {
        if(this.exception != null)
            throw new RuntimeException(exception);
    }

    public static ErrorHandler catchException(ThrowableRunnable r) {
        try {
            r.run();
        } catch(Exception e) {
            return new ErrorHandler(e);
        }

        return new ErrorHandler(null);
    }

    public static void ignoreIfThrows(ThrowableRunnable r) {
        try {
            r.run();
        } catch(Exception ignorable) {
        }
    }

}
