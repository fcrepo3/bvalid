package net.sf.bvalid;

/**
 * Signals an error not directly related to validation.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ValidatorException extends Exception {

    public ValidatorException(String msg) {
        super(msg);
    }

    public ValidatorException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
