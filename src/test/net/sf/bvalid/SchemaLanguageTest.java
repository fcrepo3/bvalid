package net.sf.bvalid;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

public class SchemaLanguageTest extends TestCase {

    public SchemaLanguageTest(String name) { super (name); }

    public void setUp() {
    }
            
    public void tearDown() {
    }

    //---------------------------------------------------------[ Test methods ]

    public void testForNameXSD() throws ValidatorException {

        SchemaLanguage xsdLower = SchemaLanguage.forName("xsd");
        assertEquals(xsdLower, SchemaLanguage.XSD);

        SchemaLanguage xsdUpper = SchemaLanguage.forName("XSD");
        assertEquals(xsdUpper, SchemaLanguage.XSD);
    }

    public void testForNameUnrecognized() {
        boolean recognized = false;
        try {
            SchemaLanguage.forName("unrecognized");
            recognized = true;
        } catch (ValidatorException e) {
        } finally {
            assertFalse(recognized);
        }
    }

    public void testGetNameXSD() {
        assertEquals(SchemaLanguage.XSD.getName(), "XSD");
    }

    public void testGetURIXSD() {
        assertEquals(SchemaLanguage.XSD.getURI(), "http://www.w3.org/2001/XMLSchema");
    }

    public void testSupportedListContainsXSD() {

        boolean foundXSD = false;
        SchemaLanguage[] langs = SchemaLanguage.getSupportedList();
        for (int i = 0; i < langs.length; i++) {
            if (langs[i] == SchemaLanguage.XSD) {
                foundXSD = true;
            }
        }

        assertTrue(foundXSD);
    }

    public static void main(String[] args) {
        TestRunner.run(SchemaLanguageTest.class);
    }   

}
