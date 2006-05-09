package net.sf.bvalid;

import java.io.*;

import junit.framework.TestCase;

import net.sf.bvalid.catalog.MemorySchemaCatalog;
import net.sf.bvalid.catalog.SchemaCatalog;
import net.sf.bvalid.locator.CatalogSchemaLocator;
import net.sf.bvalid.locator.SchemaLocator;

public abstract class ValidatorTest extends TestCase {

    public ValidatorTest(String name) { super(name); }

    /**
     * Get a Validator with the given configuration.
     */
    protected abstract Validator getValidator(SchemaLocator locator,
                                              boolean failOnMissingReferenced,
                                              boolean cacheParsedGrammars) 
            throws ValidatorException;

    /**
     * Get the filename of the bad instance document.
     */
    protected abstract String getBadInstance();

    /**
     * Get the filename of the bad schema file.
     */
    protected abstract String getBadSchema();

    /**
     * Get the filename of the good instance document.
     */
    protected abstract String getGoodInstance();

    /**
     * Get the filename of the good schema file.
     */
    protected abstract String getGoodSchema();


    /**
     * Get a memory catalog containing the good and bad schema.
     */
    private final SchemaCatalog getSimpleCatalog() throws Exception {

        SchemaCatalog catalog = new MemorySchemaCatalog();

        String fn = getGoodSchema();
        File goodSchemaFile = new File(TestConfig.TEST_DATADIR + fn);
        catalog.put(TestConfig.BASE_URL + fn, new FileInputStream(goodSchemaFile));

        fn = getBadSchema();
        File badSchemaFile = new File(TestConfig.TEST_DATADIR + fn);
        catalog.put(TestConfig.BASE_URL + fn, new FileInputStream(badSchemaFile));

        return catalog;
    }

    /**
     * Get a validator that fails on missing referenced schemas, does not
     * cache parsed grammars, and uses a CatalogSchemaLocator backed by
     * the simple catalog (above).
     */
    private final Validator getSimpleValidator() throws Exception {
        return getValidator(new CatalogSchemaLocator(getSimpleCatalog()),
                            true, 
                            false);
    }

    /**
     * Do validation for a test.
     */
    private static final void validate(Validator validator,
                                       String schemaFilename,
                                       String instanceFilename) throws Exception {

        String schemaURI = TestConfig.BASE_URL + schemaFilename;
        File instanceFile = new File(TestConfig.TEST_DATADIR + instanceFilename);

        validator.validate(new FileInputStream(instanceFile), schemaURI);
    }

    public void testValidateGoodSchemaGoodInstance() throws Exception {

        Validator validator = getSimpleValidator();
        String testMethod = validator.getClass().getName() + ".validate(..)";

        try {
            validate(validator, getGoodSchema(), getGoodInstance());
        } catch (ValidatorException e) {
            fail(testMethod + " threw ValidatorException with good schema, good instance");
        }
    }

    public void testValidateGoodSchemaBadInstance() throws Exception {

        Validator validator = getSimpleValidator();
        String testMethod = validator.getClass().getName() + ".validate(..)";

        try {
            validate(validator, getGoodSchema(), getBadInstance());
            fail(testMethod + " did not throw ValidationException with good schema, bad instance");
        } catch (ValidationException e) {
            // expected
        } catch (ValidatorException e) {
            fail(testMethod + " threw ValidatorException (not ValidationException) with good schema, bad instance");
        }
    }

    public void testValidateBadSchemaGoodInstance() throws Exception {

        Validator validator = getSimpleValidator();
        String testMethod = validator.getClass().getName() + ".validate(..)";

        try {
            validate(validator, getBadSchema(), getGoodInstance());
            fail(testMethod + " did not throw ValidatorException with bad schema, good instance");
        } catch (ValidatorException e) {
            // expected
        }
    }

    public void testValidateBadSchemaBadInstance() throws Exception {

        Validator validator = getSimpleValidator();
        String testMethod = validator.getClass().getName() + ".validate(..)";

        try {
            validate(validator, getBadSchema(), getBadInstance());
            fail(testMethod + " did not throw ValidatorException with bad schema, bad instance");
        } catch (ValidatorException e) {
            // expected
        }
    }

    public void testValidateMissingSchemaGoodInstance() throws Exception {

        Validator validator = getSimpleValidator();
        String testMethod = validator.getClass().getName() + ".validate(..)";

        try {
            validate(validator, TestConfig.MISSING_SCHEMA, getGoodInstance());
            fail(testMethod + " did not throw ValidatorException with missing schema, good instance");
        } catch (ValidatorException e) {
            // expected
        }
    }

    public void testValidateMissingSchemaBadInstance() throws Exception {

        Validator validator = getSimpleValidator();
        String testMethod = validator.getClass().getName() + ".validate(..)";

        try {
            validate(validator, TestConfig.MISSING_SCHEMA, getBadInstance());
            fail(testMethod + " did not throw ValidatorException with missing schema, bad instance");
        } catch (ValidatorException e) {
            // expected
        }
    }
}
