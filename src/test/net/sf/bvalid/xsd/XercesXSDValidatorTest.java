package net.sf.bvalid.xsd;

import java.io.*;

import junit.swingui.TestRunner;

import net.sf.bvalid.TestConfig;
import net.sf.bvalid.Validator;
import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.ValidatorTest;
import net.sf.bvalid.locator.SchemaLocator;

public class XercesXSDValidatorTest extends ValidatorTest {

    public XercesXSDValidatorTest(String name) { super (name); }

    public void setUp() throws Exception {
    }
            
    public void tearDown() {
    }

    protected Validator getValidator(SchemaLocator locator,
                                     boolean failOnMissingReferenced,
                                     boolean cacheParsedGrammars)
                throws ValidatorException {

        return new XercesXSDValidator(locator, failOnMissingReferenced, cacheParsedGrammars);
    }

    protected String getBadInstance() {
        return TestConfig.BAD_INSTANCE_XSD;
    }

    protected String getBadSchema() {
        return TestConfig.BAD_SCHEMA_XSD;
    }

    protected String getGoodInstance() {
        return TestConfig.GOOD_INSTANCE_XSD;
    }

    protected String getGoodSchema() {
        return TestConfig.GOOD_SCHEMA_XSD;
    }


    public static void main(String[] args) {
        TestRunner.run(XercesXSDValidatorTest.class);
    }   

}
