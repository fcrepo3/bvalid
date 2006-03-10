package net.sf.bvalid;

import java.io.*;
import java.util.*;

import net.sf.bvalid.locator.SchemaLocator;

public interface Validator {

    /**
     * Set the <code>SchemaLocator</code> for this validator.
     */
    public void setSchemaLocator(SchemaLocator locator);

    /**
     * Set whether validation should fail if a referenced schema could
     * not be found with the locator.  This defaults to true.
     */
    public void setFailOnMissingReferencedSchema(boolean value);

    /**
     * Validate the document according to the given schema.
     *
     * Validation will fail if:
     *   - The indicated schema cannot be located
     *   - The document is not well-formed XML
     *   - failOnMissingReferencedSchema is true and at least one referenced 
     *     schema cannot be located.
     */
    public void validate(InputStream xmlStream,
                         String schemaURI) throws ValidationException;

    /**
     * Validate the document according to the schema(s) referenced within, 
     * if any.
     *
     * Validation will fail if:
     *   - The document is not well-formed XML
     *   - failOnMissingReferencedSchema is true and at least one referenced 
     *     schema cannot be located.
     */
    public void validate(InputStream xmlStream) 
            throws ValidationException;

}
