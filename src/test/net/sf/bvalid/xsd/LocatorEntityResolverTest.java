package net.sf.bvalid.xsd;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.bvalid.TestConfig;
import net.sf.bvalid.catalog.MemorySchemaCatalog;
import net.sf.bvalid.locator.CatalogSchemaLocator;

public class LocatorEntityResolverTest extends TestCase {

    public static final String PRIMARY_XSD_URI = TestConfig.EXISTING_SCHEMA_URL;
    public static final String SECONDARY_XSD_URI = TestConfig.BASE_URL + TestConfig.GOOD_SCHEMA_XSD;
    public static final String DTD_URI = TestConfig.BASE_URL + TestConfig.GOOD_DTD;
    public static final String MISSING_XSD_URI = TestConfig.BASE_URL + "missing-schema.xsd";

    private LocatorEntityResolver _strictResolver;
    private LocatorEntityResolver _laxResolver;

    private LocatorEntityResolver _emptyStrictResolver;
    private LocatorEntityResolver _emptyLaxResolver;

    private File _existingXSD;
    private File _goodXSD;
    private File _goodDTD;

    public LocatorEntityResolverTest(String name) { super (name); }

    /**
     * Create a strict and lax resolver for use in tests.
     */
    public void setUp() throws Exception {

        MemorySchemaCatalog catalog = new MemorySchemaCatalog();

        _existingXSD = new File(TestConfig.EXISTING_SCHEMA_PATH);
        _goodXSD = new File(TestConfig.TEST_DATADIR + TestConfig.GOOD_SCHEMA_XSD);
        _goodDTD = new File(TestConfig.TEST_DATADIR + TestConfig.GOOD_DTD);

        catalog.put(PRIMARY_XSD_URI, new FileInputStream(_existingXSD));
        catalog.put(SECONDARY_XSD_URI, new FileInputStream(_goodXSD));
        catalog.put(DTD_URI, new FileInputStream(_goodDTD));

        CatalogSchemaLocator locator = new CatalogSchemaLocator(catalog);
        CatalogSchemaLocator emptyLocator = new CatalogSchemaLocator(new MemorySchemaCatalog());

        _strictResolver = new LocatorEntityResolver(locator, true, PRIMARY_XSD_URI);
        _laxResolver = new LocatorEntityResolver(locator, false, PRIMARY_XSD_URI);

        _emptyStrictResolver = new LocatorEntityResolver(emptyLocator, true, PRIMARY_XSD_URI);
        _emptyLaxResolver = new LocatorEntityResolver(emptyLocator, false, PRIMARY_XSD_URI);
    }

    public void tearDown() {

        _strictResolver.close();
        _laxResolver.close();
    }

    public static void main(String[] args) {
        TestRunner.run(LocatorEntityResolverTest.class);
    }

    public void testGetResolvedURIs() throws Exception {

        // has not resolved any yet
        assertTrue("resolvedURIs should be empty before any have been resolved",
                   _strictResolver.getResolvedURIs().size() == 0);

        // do four resolutions total, using three distinct entities
        _strictResolver.resolveEntity(null, PRIMARY_XSD_URI);
        _strictResolver.resolveEntity(null, PRIMARY_XSD_URI);
        _strictResolver.resolveEntity(null, SECONDARY_XSD_URI);
        _strictResolver.resolveEntity("publicId:" + TestConfig.GOOD_DTD, DTD_URI);

        // make sure three are reported as resolved
        Set resolved = _strictResolver.getResolvedURIs();
        assertTrue("resolvedURIs should have size 3 after resolution",
                   _strictResolver.getResolvedURIs().size() == 3);

        // make sure each of the three, specifically, is in the resolved set
        assertTrue("resolvedURIs does not contain " + PRIMARY_XSD_URI,
                   resolved.contains(PRIMARY_XSD_URI));

        assertTrue("resolvedURIs does not contain " + SECONDARY_XSD_URI,
                   resolved.contains(SECONDARY_XSD_URI));

        assertTrue("resolvedURIs does not contain " + DTD_URI,
                   resolved.contains(DTD_URI));

    }

    public void testResolveXSD() throws Exception {

        try {
            InputSource src = _strictResolver.resolveEntity(null, SECONDARY_XSD_URI);
            assertTrue("Incorrect content for resolved entity", sameContent(src, _goodXSD));
        } catch (SAXException e) {
            fail("Did not resolve expected schema: " + SECONDARY_XSD_URI);
        }
    }

    public void testResolveXSDPrimary() throws Exception {

        try {
            InputSource src = _strictResolver.resolveEntity(null, PRIMARY_XSD_URI);
            assertTrue("Incorrect content for resolved entity", sameContent(src, _existingXSD));
        } catch (SAXException e) {
            fail("Did not resolve expected schema: " + PRIMARY_XSD_URI);
        }
    }

    public void testResolveMissingXSDPrimary() {

        // should throw exception for strict
        try {
            InputSource src = _emptyStrictResolver.resolveEntity(null, PRIMARY_XSD_URI);
            fail("Resolved missing schema: " + PRIMARY_XSD_URI);
        } catch (SAXException e) {
            // expected
        }

        // should throw exception for lax, too, since it's the primary schema
        try {
            InputSource src = _emptyLaxResolver.resolveEntity(null, PRIMARY_XSD_URI);
            fail("Resolved missing schema: " + PRIMARY_XSD_URI);
        } catch (SAXException e) {
            // expected
        }

    }

    public void testResolveMissingXSDRequired() {

        // should throw exception because the resolve is strict
        try {
            InputSource src = _emptyStrictResolver.resolveEntity(null, SECONDARY_XSD_URI);
            fail("Resolved missing schema: " + SECONDARY_XSD_URI);
        } catch (SAXException e) {
            // expected
        }
    }

    public void testResolveMissingXSDOptional() throws Exception {

        // should not throw exception because the resolver is lax
        try {
            InputSource src = _emptyLaxResolver.resolveEntity(null, SECONDARY_XSD_URI);
            // good, but also make sure it doesn't have an inputstream
            assertNull("Missing optional datasource should have null underlying InputStream",
                       src.getByteStream());
        } catch (SAXException e) {
            fail("Should not have thrown exception (lax resolver): " + SECONDARY_XSD_URI);
        }
    }

    public void testResolveDTD() throws Exception {

        try {
            InputSource src = _strictResolver.resolveEntity("publicId:" + TestConfig.GOOD_DTD, DTD_URI);
            assertTrue("Incorrect content for resolved entity", sameContent(src, _goodDTD));
        } catch (SAXException e) {
            fail("Did not resolve expected dtd: " + DTD_URI);
        }
    }

    public void testResolveMissingDTDRequired() {

        try {
            InputSource src = _emptyStrictResolver.resolveEntity("publicId:" + TestConfig.GOOD_DTD, DTD_URI);
            fail("Resolved missing dtd: " + DTD_URI);
        } catch (SAXException e) {
            // expected
        }
    }

    public void testResolveMissingDTDOptional() {

        // should not throw exception because the resolver is lax
        try {
            InputSource src = _emptyLaxResolver.resolveEntity("publicId:" + TestConfig.GOOD_DTD, DTD_URI);
            // good, but also make sure it doesn't have an inputstream
            assertNull("Missing optional datasource should have null underlying InputStream",
                       src.getByteStream());
        } catch (SAXException e) {
            fail("Should not have thrown exception (lax resolver): " + DTD_URI);
        }
    }

    private static boolean sameContent(InputSource src,
                                       File file) throws IOException {

        String srcString = getString(src.getByteStream());
        String fileString = getString(new FileInputStream(file));
        return (srcString.equals(fileString));
    }

    private static String getString(InputStream in) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            StringBuffer out = new StringBuffer();
            
            String line = reader.readLine();
            while (line != null) {
                out.append(line + "\n");
                line = reader.readLine();
            }

            return out.toString();
        } finally {
            reader.close();
        }
    }
                                       

}
