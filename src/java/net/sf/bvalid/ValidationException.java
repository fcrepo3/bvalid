package net.sf.bvalid;

public class ValidationException extends ValidatorException {

    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, Throwable cause) {
        super(msg, cause);
    }

}