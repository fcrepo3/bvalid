package net.sf.bvalid.xsd.xerces;

import java.io.*;
import java.util.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import net.sf.bvalid.SchemaLanguage;
import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.ValidationException;
import net.sf.bvalid.Validator;
import net.sf.bvalid.locator.SchemaLocator;

public class XercesXSDValidator implements EntityResolver, Validator {

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
        try {

            SAXParser parser = _parserFactory.newSAXParser();
            parser.setProperty(
                "http://java.sun.com/xml/jaxp/properties/schemaLanguage", 
                SchemaLanguage.XSD.getURI());
            if (schemaURI != null) {
                parser.setProperty(
                    "http://java.sun.com/xml/jaxp/properties/schemaSource",
                    schemaURI.toString());
            }

            XMLReader reader = parser.getXMLReader();
//            xmlreader.setErrorHandler(new DOValidatorXMLErrorHandler());
            reader.setEntityResolver(this);

            reader.parse(new InputSource(xmlStream));

        } catch (Throwable th) {
            throw new ValidationException("Validation failed", th);
        } finally {
            try { xmlStream.close(); } catch (Exception e) { }
        }
    }

    public InputSource resolveEntity(String publicId, 
                                     String systemId) throws SAXException {

        System.out.println("Resolving entity: pubId: " + publicId + " sysId: " + systemId);
        _LOG.debug("Resolving...");
        
        InputSource source = null;
        if (systemId != null) {
            try {
                InputStream in = _locator.get(systemId, _failOnMissingReferencedSchema);
                if (in != null) {
                    source = new InputSource(in);
                }
            } catch (Exception ex) {
                throw new SAXException("Unable to load schema");
            }
        }

        if (source == null) {
            return new InputSource();
        } else {
            return source;
        }
/*
        if (systemId != null && systemId.startsWith("file:")) {
            return null;
        } else {
            return new InputSource();
        }
*/
    }

}
