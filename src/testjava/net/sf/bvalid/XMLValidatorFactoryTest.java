package net.sf.bvalid;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import net.sf.bvalid.locator.WebSchemaLocator;

public class XMLValidatorFactoryTest extends TestCase {

    public XMLValidatorFactoryTest(String name) { super (name); }

    public void setUp() {
    }
            
    public void tearDown() {
    }

    //---------------------------------------------------------[ Test methods ]

    public void testGetValidatorDefaultLocator() 
            throws ValidatorException {
        XMLValidatorFactory.getValidator(SchemaLanguage.XSD);
    }

    public void testGetValidatorSpecificLocator() 
            throws ValidatorException {
        XMLValidatorFactory.getValidator(SchemaLanguage.XSD,
                                         new WebSchemaLocator());
    }

    public void testGetValidatorNoFailOnMissingSchema() 
            throws ValidatorException {
        XMLValidatorFactory.getValidator(SchemaLanguage.XSD,
                                         new WebSchemaLocator(),
                                         false);
    }

    public static void main(String[] args) {
        TestRunner.run(XMLValidatorFactoryTest.class);
    }   

}
