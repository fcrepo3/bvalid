package net.sf.bvalid.xsd;

import java.util.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;

public class URLBasedGrammarPoolTest extends TestCase {

    public URLBasedGrammarPoolTest(String name) { super (name); }

    public void setUp() {
    }

    public void tearDown() {
    }

    public static void main(String[] args) {
        TestRunner.run(URLBasedGrammarPoolTest.class);
    }

    public void testGrammarPooling() {

        //
        // First cache two sets of grammars (two dtds, two xsds)
        //

        URLBasedGrammarPool pool = new URLBasedGrammarPool();


        // add two test dtd grammars

        XMLGrammarDescription dtdDesc1 = 
                new TestGrammarDescription(XMLGrammarDescription.XML_DTD,
                                           "http://localhost/",
                                           "http://localhost/dtd1.dtd",
                                           "dtd1.dtd",
                                           "urn:publicId:dtd1");
        XMLGrammarDescription dtdDesc2 = 
                new TestGrammarDescription(XMLGrammarDescription.XML_DTD,
                                           "http://localhost/",
                                           "http://localhost/dtd2.dtd",
                                           "dtd2.dtd",
                                           "urn:publicId:dtd2");

        Grammar dtdGrammar1 = new TestGrammar(dtdDesc1);        
        Grammar dtdGrammar2 = new TestGrammar(dtdDesc2);        

        Grammar[] dtdGrammars = new Grammar[] { dtdGrammar1, dtdGrammar2 };

        pool.cacheGrammars(XMLGrammarDescription.XML_DTD, dtdGrammars);


        // add two test xsd grammars

        XMLGrammarDescription xsdDesc1 = 
                new TestGrammarDescription(XMLGrammarDescription.XML_SCHEMA,
                                           "http://localhost/",
                                           "http://localhost/xsd1.dtd",
                                           "xsd1.dtd",
                                           "urn:publicId:xsd1");
        XMLGrammarDescription xsdDesc2 = 
                new TestGrammarDescription(XMLGrammarDescription.XML_SCHEMA,
                                           "http://localhost/",
                                           "http://localhost/xsd2.dtd",
                                           "xsd2.dtd",
                                           "urn:publicId:xsd2");

        Grammar xsdGrammar1 = new TestGrammar(xsdDesc1);        
        Grammar xsdGrammar2 = new TestGrammar(xsdDesc2);        

        Grammar[] xsdGrammars = new Grammar[] { xsdGrammar1, xsdGrammar2 };

        pool.cacheGrammars(XMLGrammarDescription.XML_SCHEMA, xsdGrammars);


        //
        // Next, make sure they're all retrievable
        //

        XMLGrammarDescription desc;

        assertEquals("retrieveGrammar(dtdDesc1) did not return dtdGrammar1",
                     pool.retrieveGrammar(dtdDesc1),
                     dtdGrammar1);

        assertEquals("retrieveGrammar(dtdDesc2) did not return dtdGrammar2",
                     pool.retrieveGrammar(dtdDesc2),
                     dtdGrammar2);

        assertEquals("retrieveGrammar(xsdDesc1) did not return xsdGrammar1",
                     pool.retrieveGrammar(xsdDesc1),
                     xsdGrammar1);

        assertEquals("retrieveGrammar(xsdDesc2) did not return xsdGrammar2",
                     pool.retrieveGrammar(xsdDesc1),
                     xsdGrammar1);

        //
        // Finally, clear the pool and make sure none are retrievable.
        //

        pool.clear();

        assertNull("retrieveGrammar(dtdDesc1) returned value after pool was cleared",
                   pool.retrieveGrammar(dtdDesc1));
        assertNull("retrieveGrammar(dtdDesc2) returned value after pool was cleared",
                   pool.retrieveGrammar(dtdDesc1));
        assertNull("retrieveGrammar(xsdDesc1) returned value after pool was cleared",
                   pool.retrieveGrammar(xsdDesc1));
        assertNull("retrieveGrammar(xsdDesc2) returned value after pool was cleared",
                   pool.retrieveGrammar(xsdDesc1));
    }

    public class TestGrammar implements Grammar {

        private XMLGrammarDescription _description;

        public TestGrammar(XMLGrammarDescription description) {
            _description = description;
        }

        public XMLGrammarDescription getGrammarDescription() {
            return _description;
        }

    }

    public class TestGrammarDescription implements XMLGrammarDescription {

        private String _type;
        private String _baseSystemId;
        private String _expandedSystemId;
        private String _literalSystemId;
        private String _publicId;

        private String _namespace;

        public TestGrammarDescription(String type,
                                      String baseSystemId,
                                      String expandedSystemId,
                                      String literalSystemId,
                                      String publicId) {
            _type = type;
            _baseSystemId = baseSystemId;
            _expandedSystemId = expandedSystemId;
            _literalSystemId = literalSystemId;
            _publicId = publicId;
            
            _namespace = _baseSystemId;
        }

        public String getGrammarType() { return _type; }

        // XMLResourceIdentifier methods
        public String getBaseSystemId() { return _baseSystemId; }
        public void setBaseSystemId(String val) { _baseSystemId = val; }

        public String getExpandedSystemId() { return _expandedSystemId; }
        public void setExpandedSystemId(String val) { _expandedSystemId = val; }

        public String getLiteralSystemId() { return _literalSystemId; }
        public void setLiteralSystemId(String val) { _literalSystemId = val; }

        public String getPublicId() { return _publicId; }
        public void setPublicId(String val) { _publicId = val; }

        public String getNamespace() { return _namespace; }
        public void setNamespace(String val) { _namespace = val; }
        
    }

}
