package net.sf.bvalid.xsd;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class XSDPackageTestSuite {
    
    public static Test suite() {

        TestSuite suite = new TestSuite(XSDPackageTestSuite.class.getName());

        suite.addTestSuite(XercesXSDValidatorTest.class);

        return suite;
    }

    public static void main(String[] args) {
        TestRunner.run(XSDPackageTestSuite.class);
    }
}
