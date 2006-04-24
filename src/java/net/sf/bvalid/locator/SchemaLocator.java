package net.sf.bvalid.locator;

import java.io.*;

import net.sf.bvalid.ValidatorException;

public interface SchemaLocator {

    /**
     * Get the content of the indicated schema.
     *
     * If the schema is not found and required is false, return null.
     * If the schema is not found and required is true, throw an exception.
     */
    public InputStream get(String uri, boolean required) throws ValidatorException;

    /**
     * Notify the locator that the indicated schema was just used for a
     * sucessful validation.
     *
     * This information is particularly useful for SchemaLocators
     * that do caching.  Other implementations may simply ignore the
     * the message.
     */
    public void successfullyUsed(String uri) throws ValidatorException;

}
