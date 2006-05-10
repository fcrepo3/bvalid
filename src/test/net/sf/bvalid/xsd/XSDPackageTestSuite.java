package net.sf.bvalid.xsd;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class XSDPackageTestSuite {
    
    public static Test suite() {

        TestSuite suite = new TestSuite(XSDPackageTestSuite.class.getName());

        suite.addTestSuite(LocatorEntityResolverTest.class);
        suite.addTestSuite(URLBasedGrammarPoolTest.class);
        suite.addTestSuite(XercesXSDValidatorTest.class);
        suite.addTestSuite(XSDErrorHandlerTest.class);

        return suite;
    }

    public static void main(String[] args) {
        TestRunner.run(XSDPackageTestSuite.class);
    }
}
