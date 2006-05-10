package net.sf.bvalid.xsd;

import java.util.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class XSDErrorHandlerTest extends TestCase {

    public XSDErrorHandlerTest(String name) { super (name); }

    public void setUp() {
    }

    public void tearDown() {
    }

    public static void main(String[] args) {
        TestRunner.run(XSDErrorHandlerTest.class);
    }

    public void testGetEmptyErrorList() {

        XSDErrorHandler handler = new XSDErrorHandler();

        assertNull("No errors, so error list should have been null",
                   handler.getErrorList());
    }

    public void testGetErrorList() {

        XSDErrorHandler handler = new XSDErrorHandler();

        String eMessage = "example of error";
        String fMessage = "example of fatalError";
        String wMessage = "example of warning";
        SAXParseException e = new SAXParseException(eMessage, null, null, 0, 0);
        SAXParseException f = new SAXParseException(fMessage, null, null, 1, 1);
        SAXParseException w = new SAXParseException(wMessage, null, null, 2, 2);

        handler.error(e);
        handler.fatalError(f);
        handler.warning(w);

        assertEquals("Expected error list to have three elements",
                     handler.getErrorList().size(),
                     3);

        assertTrue("Error list did not contain error",
                   findString(handler.getErrorList(), eMessage));

        assertTrue("Error list did not contain fatalError",
                   findString(handler.getErrorList(), fMessage));

        assertTrue("Error list did not contain warning",
                   findString(handler.getErrorList(), wMessage));
    }

    private boolean findString(List list, String string) {

        for (int i = 0; i < list.size(); i++) {
            String element = (String) list.get(i);
            if (element.indexOf(string) != -1) {
                return true;
            }
        }
        return false;
    }

}
