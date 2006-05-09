package net.sf.bvalid.locator;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class LocatorPackageTestSuite {
    
    public static Test suite() {

        TestSuite suite = new TestSuite(LocatorPackageTestSuite.class.getName());

        suite.addTestSuite(CachingSchemaLocatorTest.class);
        suite.addTestSuite(CatalogSchemaLocatorTest.class);
        suite.addTestSuite(ChainingSchemaLocatorTest.class);
        suite.addTestSuite(URLSchemaLocatorTest.class);

        return suite;
    }

    public static void main(String[] args) {
        TestRunner.run(LocatorPackageTestSuite.class);
    }
}
