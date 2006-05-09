package net.sf.bvalid.locator;

import java.io.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import net.sf.bvalid.TestConfig;
import net.sf.bvalid.catalog.MemorySchemaCatalog;
import net.sf.bvalid.catalog.SchemaCatalog;

public class CatalogSchemaLocatorTest extends SchemaLocatorTest {

    private SchemaLocator _locator;
    private SchemaCatalog _catalog;

    public CatalogSchemaLocatorTest(String name) { super (name); }

    public void setUp() throws Exception {
        _catalog = new MemorySchemaCatalog();
        _catalog.put(TestConfig.EXISTING_SCHEMA_URL,
                     new FileInputStream(new File(TestConfig.EXISTING_SCHEMA_PATH)));
        _locator = new CatalogSchemaLocator(_catalog);
    }
            
    public void tearDown() {
    }

    protected SchemaLocator getTestLocator() {
        return _locator;
    }

    public static void main(String[] args) {
        TestRunner.run(CatalogSchemaLocatorTest.class);
    }   

}
