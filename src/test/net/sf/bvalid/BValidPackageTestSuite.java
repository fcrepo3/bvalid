package net.sf.bvalid;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import net.sf.bvalid.catalog.CatalogPackageTestSuite;

public class BValidPackageTestSuite {
    
    public static Test suite() {

        TestSuite suite = new TestSuite(BValidPackageTestSuite.class.getName());
   
        // classes in this package
        suite.addTestSuite(SchemaLanguageTest.class);
        suite.addTestSuite(ValidatorFactoryTest.class);
        suite.addTestSuite(ValidatorOptionTest.class);

        // sub-packages
        suite.addTest(CatalogPackageTestSuite.suite());

        return suite;
    }

    public static void main(String[] args) {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(BValidPackageTestSuite.suite());
        } else {
            TestRunner.run(BValidPackageTestSuite.class);
        }
    }
}
