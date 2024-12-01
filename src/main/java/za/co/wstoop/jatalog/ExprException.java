package za.co.wstoop.jatalog;

/**
 *
 * @author pi
 */
public class ExprException extends RuntimeException {

    public ExprException(String msg) {
        super(msg);
    }

    public ExprException(String message, Throwable cause) {
        super(message, cause);
    }

}
