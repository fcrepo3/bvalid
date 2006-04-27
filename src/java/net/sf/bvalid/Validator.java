package net.sf.bvalid;

import java.io.*;

import net.sf.bvalid.locator.SchemaLocator;

/**
 * An XML document validator.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface Validator {

    /**
     * Validate the document according to the given schema.
     */
    public void validate(InputStream xmlStream,
                         String schemaURI) 
            throws ValidationException;

    /**
     * Validate the document according to the schema(s) referenced 
     * within, if any.
     */
    public void validate(InputStream xmlStream) 
            throws ValidationException;

}
