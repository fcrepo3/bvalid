package net.sf.bvalid.locator;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import net.sf.bvalid.TestConfig;
import net.sf.bvalid.catalog.MemorySchemaCatalog;
import net.sf.bvalid.catalog.SchemaCatalog;

public class ChainingSchemaLocatorTest extends SchemaLocatorTest {

    private SchemaLocator _locator;
    private SchemaCatalog _catalog1;
    private SchemaCatalog _catalog2;

    public ChainingSchemaLocatorTest(String name) { super (name); }

    public void setUp() throws Exception {

        _catalog1 = new MemorySchemaCatalog();
        _catalog2 = new MemorySchemaCatalog();
        _catalog2.put(TestConfig.EXISTING_SCHEMA_URL,
                      new FileInputStream(new File(TestConfig.EXISTING_SCHEMA_PATH)));
       
        List locators = new ArrayList();
        locators.add(new CatalogSchemaLocator(_catalog1));
        locators.add(new CatalogSchemaLocator(_catalog2));

        _locator = new ChainingSchemaLocator(locators);
    }
            
    public void tearDown() {
    }

    protected SchemaLocator getTestLocator() {
        return _locator;
    }

    public static void main(String[] args) {
        TestRunner.run(ChainingSchemaLocatorTest.class);
    }   

}
