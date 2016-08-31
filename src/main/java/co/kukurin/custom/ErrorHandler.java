package co.kukurin.custom;

import java.util.function.Consumer;

public class ErrorHandler {

    private Exception exception;

    private ErrorHandler(Exception exception) {
        this.exception = exception;
    }

    public void handleExceptionAs(Consumer<Exception> handler) {
        if(this.exception != null)
            handler.accept(exception);
    }

    public ErrorHandler finalizingWith(Runnable doAfter) {
        doAfter.run();
        return this;
    }

    public void rethrowAsUnchecked() {
        if(this.exception != null)
            throw new RuntimeException(exception);
    }

    public ErrorHandler andThen(ThrowableRunnable r) {
        if(this.exception == null) {
            try {
                r.run();
            } catch(Exception e) {
                this.exception = e;
            }
        }

        return this;
    }

    public static ErrorHandler catchIfThrows(ThrowableRunnable r) {
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

    public static <T> Optional<T> optionalResult(ThrowableSupplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch(Exception any) {
            return Optional.empty();
        }
    }

}
