package net.sf.bvalid;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import net.sf.bvalid.locator.WebSchemaLocator;

public class ValidatorFactoryTest extends TestCase {

    public ValidatorFactoryTest(String name) { super (name); }

    public void setUp() {
    }
            
    public void tearDown() {
    }

    //---------------------------------------------------------[ Test methods ]

    public void testGetValidatorDefaultLocator() 
            throws ValidatorException {
        ValidatorFactory.getValidator(SchemaLanguage.XSD);
    }

    public void testGetValidatorSpecificLocator() 
            throws ValidatorException {
        ValidatorFactory.getValidator(SchemaLanguage.XSD,
                                         new WebSchemaLocator());
    }

    public void testGetValidatorNoFailOnMissingSchema() 
            throws ValidatorException {
        ValidatorFactory.getValidator(SchemaLanguage.XSD,
                                         new WebSchemaLocator(),
                                         false);
    }

    public static void main(String[] args) {
        TestRunner.run(ValidatorFactoryTest.class);
    }   

}
