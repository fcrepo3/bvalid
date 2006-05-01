package net.sf.bvalid;

/**
 * Superclass for all BValid-generated exceptions.
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
