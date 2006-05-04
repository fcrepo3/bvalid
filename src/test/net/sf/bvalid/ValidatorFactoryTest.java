package net.sf.bvalid;

import java.util.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import net.sf.bvalid.locator.URLSchemaLocator;

public class ValidatorFactoryTest extends TestCase {

    public ValidatorFactoryTest(String name) { super (name); }

    public void setUp() {
    }
            
    public void tearDown() {
    }

    //---------------------------------------------------------[ Test methods ]

    public void testGetValidatorDefault() 
            throws ValidatorException {

        ValidatorFactory.getValidator(SchemaLanguage.XSD, null);
    }

    public void testGetValidatorCustom() 
            throws ValidatorException {

        ValidatorFactory.getValidator(SchemaLanguage.XSD, 
                                      new URLSchemaLocator(), 
                                      null);
    }

    public void testGetValidatorCustomWithGoodOptions() 
            throws ValidatorException {

        Map options = new HashMap();
        options.put(ValidatorOption.CACHE_PARSED_GRAMMARS, "false");
        ValidatorFactory.getValidator(SchemaLanguage.XSD, 
                                      new URLSchemaLocator(), 
                                      options);
    }

    public void testGetValidatorCustomWithBadOptions() 
            throws ValidatorException {

        Map options = new HashMap();
        options.put(ValidatorOption.CACHE_PARSED_GRAMMARS, "faults");
        boolean threwException = false;
        try {
            ValidatorFactory.getValidator(SchemaLanguage.XSD, 
                                          new URLSchemaLocator(), 
                                          options);
        } catch (ValidatorException e) {
            threwException = true;
        }
        assertEquals("Should have thrown exception due to bad option", true, threwException);
    }

    public static void main(String[] args) {

        TestRunner.run(ValidatorFactoryTest.class);
    }   

}
