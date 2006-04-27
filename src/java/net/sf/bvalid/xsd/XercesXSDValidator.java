package net.sf.bvalid.xsd;

import java.io.*;
import java.util.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;

import org.apache.xerces.xni.grammars.XMLGrammarPool;

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

    private boolean _failOnMissingReferenced;

    private SAXParserFactory _parserFactory;

    private XMLGrammarPool _gPool;

    public XercesXSDValidator(SchemaLocator locator,
                              boolean failOnMissingReferenced,
                              boolean cacheParsedGrammars)
            throws ValidatorException {

        _failOnMissingReferenced = failOnMissingReferenced;

        _locator = locator;

        try {
            _parserFactory = SAXParserFactory.newInstance();
            _parserFactory.setNamespaceAware(true);
            _parserFactory.setValidating(true);

            if (cacheParsedGrammars) {
                _gPool = new URLBasedGrammarPool();
            }

        } catch (Throwable th) {
            throw new ValidatorException("Unable to initialize SAX parsing");
        }
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

        LocatorEntityResolver entityResolver = 
                new LocatorEntityResolver(_locator,
                                          _failOnMissingReferenced,
                                          schemaURI);
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
            if (_gPool != null) {
                parser.setProperty("http://apache.org/xml/properties/internal/grammar-pool", _gPool);
            }

            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler(errorHandler);
            reader.setEntityResolver(entityResolver);

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
            // if validation failed, throw appropriate exception
            StringBuffer msg = new StringBuffer();
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) msg.append("\n");
                msg.append((String) errors.get(i));
            }
            throw new ValidationException(msg.toString());
        } else {
            // if successful, notify the locator of the schemas that
            // were obtained from it during validation
            Iterator iter = entityResolver.getResolvedURIs().iterator();
            while (iter.hasNext()) {
                String uri = (String) iter.next();
                try {
                    _locator.successfullyUsed(uri);
                } catch (ValidatorException e) {
                    throw new ValidationException("Validation failed due to validator error", e);
                }
            }
        }

    }

}
