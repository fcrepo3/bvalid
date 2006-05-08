package net.sf.bvalid;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

import net.sf.bvalid.catalog.CatalogPackageTestSuite;
import net.sf.bvalid.util.JettyTestSetup;

public class BValidPackageTestSuite extends TestCase {

    public static Test suite() throws Exception {

        TestSuite suite = new TestSuite(BValidPackageTestSuite.class.getName());
   
        // classes in this package
        suite.addTestSuite(SchemaLanguageTest.class);
        suite.addTestSuite(ValidatorFactoryTest.class);
        suite.addTestSuite(ValidatorOptionTest.class);

        // sub-packages
        suite.addTest(CatalogPackageTestSuite.suite());

        boolean fork = true;
        String forkValue = System.getProperty("jetty.fork");
        if (forkValue != null) {
            fork = !(forkValue.equalsIgnoreCase("false") || forkValue.equalsIgnoreCase("no"));
        }
        return new JettyTestSetup(suite, 7357, "/", ".", fork);
    }

    public static void main(String[] args) throws Exception {
        if (System.getProperty("text") != null && System.getProperty("text").equals("true")) {
            junit.textui.TestRunner.run(BValidPackageTestSuite.suite());
        } else {
            TestRunner.run(BValidPackageTestSuite.class);
        }
    }
}
