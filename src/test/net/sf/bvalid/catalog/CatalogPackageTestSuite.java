package net.sf.bvalid.catalog;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.swingui.TestRunner;

public class CatalogPackageTestSuite {
    
    public static Test suite() {

        TestSuite suite = new TestSuite(CatalogPackageTestSuite.class.getName());

        suite.addTestSuite(FileSchemaIndexTest.class);
        suite.addTestSuite(MemorySchemaCatalogTest.class);
        suite.addTestSuite(DiskSchemaCatalogTest.class);

        return suite;
    }

    public static void main(String[] args) {
        TestRunner.run(CatalogPackageTestSuite.class);
    }
}
