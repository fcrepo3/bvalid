package net.sf.bvalid;

/**
 * Signals that validation failed.
 *
 * If there were multiple error messages, they'll be separated
 * by newlines in the message.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ValidationException extends ValidatorException {

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
