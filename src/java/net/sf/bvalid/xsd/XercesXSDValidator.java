package net.sf.bvalid.xsd;

import java.io.*;
import java.util.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import net.sf.bvalid.SchemaLanguage;
import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.ValidationException;
import net.sf.bvalid.Validator;
import net.sf.bvalid.locator.SchemaLocator;

public class XercesXSDValidator implements Validator {

    private static Logger _LOG = Logger.getLogger(XercesXSDValidator.class.getName());

    private SchemaLocator _locator;
    private boolean _failOnMissingReferencedSchema;

    private SAXParserFactory _parserFactory;

    public XercesXSDValidator() throws ValidatorException {
        _failOnMissingReferencedSchema = true;

        try {
            _parserFactory = SAXParserFactory.newInstance();
            _parserFactory.setNamespaceAware(true);
            _parserFactory.setValidating(true);
        } catch (Throwable th) {
            throw new ValidatorException("Unable to initialize SAX parsing");
        }
    }

    public void setSchemaLocator(SchemaLocator locator) {
        _locator = locator;
    }

    public void setFailOnMissingReferencedSchema(boolean value) {
        _failOnMissingReferencedSchema = value;
    }

    public void validate(InputStream xmlStream, 
                         String schemaURI) throws ValidationException {
        doValidation(xmlStream, schemaURI);
    }

    public void validate(InputStream xmlStream) throws ValidationException {
        doValidation(xmlStream, null);
    }

    private void doValidation(InputStream xmlStream,
                              String schemaURI) throws ValidationException {

        XSDErrorHandler errorHandler = new XSDErrorHandler();
        try {

            SAXParser parser = _parserFactory.newSAXParser();
            parser.setProperty(
                "http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                SchemaLanguage.XSD.getURI());
            if (schemaURI != null) {
                parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaSource",
                    schemaURI);
            }

            XMLReader reader = parser.getXMLReader();

            reader.setErrorHandler(errorHandler);

            reader.setEntityResolver(
                    new LocatorEntityResolver( 
                            _locator, 
                            _failOnMissingReferencedSchema, 
                            schemaURI));

            reader.parse(new InputSource(xmlStream));


        } catch (SAXParseException e) {
            throw new ValidationException(XSDErrorHandler.getErrorText(e));
        } catch (Throwable th) {
            throw new ValidationException("Validation failed due to underlying error", th);
        } finally {
            try { xmlStream.close(); } catch (Exception e) { }
        }

        List errors = errorHandler.getErrorList();
        if (errors != null && errors.size() > 0) {
            StringBuffer msg = new StringBuffer();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) msg.append("\n");
                msg.append((String) errors.get(i));
            }
            throw new ValidationException(msg.toString());
        }

    }

}
