package net.sf.bvalid;

import net.sf.bvalid.locator.SchemaLocator;
import net.sf.bvalid.locator.WebSchemaLocator;
import net.sf.bvalid.xsd.xerces.XercesXSDValidator;

public abstract class ValidatorFactory {

    public static Validator getValidator(SchemaLanguage lang)
            throws ValidatorException {
        return getValidator(lang, new WebSchemaLocator());
    }

    public static Validator getValidator(SchemaLanguage lang,
                                            SchemaLocator locator)
            throws ValidatorException {
        return getValidator(lang, locator, true);
    }

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
