package net.sf.bvalid.locator;

import java.io.*;

import junit.framework.TestCase;

import net.sf.bvalid.TestConfig;
import net.sf.bvalid.ValidatorException;

public abstract class SchemaLocatorTest extends TestCase {

    public SchemaLocatorTest(String name) { super(name); }

    /**
     * Get a locator that can resolve EXISTING_SCHEMA_LOCATION but
     * but cannot resolve MISSING_SCHEMA_LOCATION
     */
    protected abstract SchemaLocator getTestLocator();

    public void testGetExisting() throws Exception {

        SchemaLocator locator = getTestLocator();
        String testMethod = locator.getClass().getName() + ".get";

        InputStream in = null;
        try {
            in = locator.get(TestConfig.EXISTING_SCHEMA_URL, true);
            assertNotNull(testMethod + "(..) should never return null if required is true", in);
        } catch (ValidatorException e) {
            fail(testMethod + "(..) for existing schema threw ValidatorException");
        } finally {
            try { in.close(); } catch (Exception e) { }
        }
    }

    public void testGetMissingRequired() throws Exception {

        SchemaLocator locator = getTestLocator();
        String testMethod = locator.getClass().getName() + ".get";

        boolean threwException = false;
        InputStream in = null;
        try {
            in = locator.get(TestConfig.MISSING_SCHEMA_URL, true);
        } catch (ValidatorException e) {
            threwException = true;
        } finally {
            try { in.close(); } catch (Exception e) { }
        }
        assertEquals(testMethod + "(..) should have thrown exception for missing required schema",
                     threwException,
                     true);
    }


    public void testGetMissingOptional() throws Exception {

        SchemaLocator locator = getTestLocator();
        String testMethod = locator.getClass().getName() + ".get";

        InputStream in = null;
        try {
            in = locator.get(TestConfig.MISSING_SCHEMA_URL, false);
            assertNull(testMethod + "(..) returned non-null value for missing optional schema",
                       in);
        } catch (ValidatorException e) {
            fail(testMethod + "(..) threw exception for missing optional schema");
        } finally {
            try { in.close(); } catch (Exception e) { }
        }
    }

}
