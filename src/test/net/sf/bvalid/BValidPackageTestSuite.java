package net.sf.bvalid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import net.sf.bvalid.catalog.CatalogPackageTestSuite;
import net.sf.bvalid.locator.LocatorPackageTestSuite;
import net.sf.bvalid.util.JettyTestSetup;
import net.sf.bvalid.xsd.XSDPackageTestSuite;

public class BValidPackageTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(BValidPackageTestSuite.class.getName());
   
        // classes in this package
        suite.addTestSuite(SchemaLanguageTest.class);
        suite.addTestSuite(ValidatorFactoryTest.class);
        suite.addTestSuite(ValidatorOptionTest.class);

        // sub-package suites
        suite.addTest(CatalogPackageTestSuite.suite());
        suite.addTest(LocatorPackageTestSuite.suite());
        suite.addTest(XSDPackageTestSuite.suite());

        return new JettyTestSetup(suite, 
                                  TestConfig.TEST_PORT,
                                  "/", 
                                  TestConfig.TEST_DATADIR,
                                  TestConfig.JETTY_FORK);
    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(BValidPackageTestSuite.suite());
        } else {
            TestRunner.run(BValidPackageTestSuite.class);
        }
    }
}
