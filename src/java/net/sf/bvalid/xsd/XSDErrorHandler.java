package net.sf.bvalid.xsd;

import java.io.*;
import java.util.*;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Recieves error messages during validation and makes them
 * available for later use.
 * 
 * @author cwilper@cs.cornell.edu
 */
public class XSDErrorHandler implements ErrorHandler {

    private List _errorList;

    public XSDErrorHandler() {
    }

    /**
     * Get the list of errors that occurred during validation.
     *
     * A null value indicates no errors.
     */
    public List getErrorList() {
        return _errorList;
    }

    private void addError(SAXParseException e) {
        if (_errorList == null) _errorList = new ArrayList();
        _errorList.add(getErrorText(e));

    }

    protected static String getErrorText(SAXParseException e) {
        StringBuffer msg = new StringBuffer();
        msg.append("Error");
        if (e.getSystemId() != null) {
            msg.append(" in " + e.getSystemId());
        }
        msg.append(" on line " + e.getLineNumber());
        // NOTE: in test, xerces seemed to give wrong column #, so omitting to avoid confusion
        // msg.append(", column " + e.getColumnNumber());
        msg.append(": " + e.getMessage());
        return msg.toString();
    }

    // org.xml.sax.ErrorHandler#error
    public void error(SAXParseException e) {
        addError(e);
    }

    // org.xml.sax.ErrorHandler#fatalError
    public void fatalError(SAXParseException e) {
        addError(e);
    }

    // org.xml.sax.ErrorHandler#warning
    public void warning(SAXParseException e) {
        addError(e);
    }

}
