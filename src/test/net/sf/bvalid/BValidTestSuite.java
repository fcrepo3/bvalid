package net.sf.bvalid;

import junit.framework.Test;
import junit.framework.TestSuite;

public class BValidTestSuite {
    
    public static Test suite() {

        TestSuite suite = new TestSuite();
    
        suite.addTestSuite(SchemaLanguageTest.class);
        suite.addTestSuite(ValidatorFactoryTest.class);
        suite.addTestSuite(ValidatorOptionTest.class);

        //
        // Another example test suite of tests.
        // 
//        suite.addTest(CreditCardTestSuite.suite());

        return suite;
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
