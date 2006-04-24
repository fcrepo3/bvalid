package net.sf.bvalid;

import net.sf.bvalid.locator.SchemaLocator;
import net.sf.bvalid.locator.WebSchemaLocator;
import net.sf.bvalid.xsd.XercesXSDValidator;

public abstract class ValidatorFactory {

    /**
     * Get a validator for the given schema language, using the default 
     * <code>SchemaLocator</code>.
     *
     * The default schema locator does no caching; it loads schemas from
     * the web each time they're needed.
     */
    public static Validator getValidator(SchemaLanguage lang)
            throws ValidatorException {
        return getValidator(lang, new WebSchemaLocator());
    }

    /**
     * Get a validator for the given schema language using the given 
     * <code>SchemaLocator</code>.
     */
    public static Validator getValidator(SchemaLanguage lang,
                                         SchemaLocator locator)
            throws ValidatorException {
        return getValidator(lang, locator, true);
    }

    /**
     * Get a validator for the given schema language using the given 
     * <code>SchemaLocator</code>.
     */
    public static Validator getValidator(SchemaLanguage lang,
                                         SchemaLocator locator,
                                         boolean failOnMissingReferencedSchema) 
            throws ValidatorException {

        Validator validator = null;
        if (lang == SchemaLanguage.XSD) {
            validator = new XercesXSDValidator();
        } 
        
        if (validator == null) {
            throw new ValidatorException("No validator found for schema "
                    + "language: " + lang.getName());
        } else {
            validator.setSchemaLocator(locator);
            validator.setFailOnMissingReferencedSchema(failOnMissingReferencedSchema);
            return validator;
        }
    }

}
