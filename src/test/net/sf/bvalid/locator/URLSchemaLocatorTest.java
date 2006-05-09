package net.sf.bvalid.locator;

import java.io.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import net.sf.bvalid.TestConfig;

public class URLSchemaLocatorTest extends SchemaLocatorTest {

    private SchemaLocator _locator;

    public URLSchemaLocatorTest(String name) { super (name); }

    public void setUp() throws Exception {
        _locator = new URLSchemaLocator();
    }
            
    public void tearDown() {
    }

    protected SchemaLocator getTestLocator() {
        return _locator;
    }

    public static void main(String[] args) {
        TestRunner.run(URLSchemaLocatorTest.class);
    }   

}
