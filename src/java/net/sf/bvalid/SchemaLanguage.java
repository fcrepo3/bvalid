package net.sf.bvalid;

/**
 * An XML schema language.
 *
 * @author cwilper@cs.cornell.edu
 */
public class SchemaLanguage {

    public static final SchemaLanguage XSD = 
            new SchemaLanguage("XSD", "http://www.w3.org/2001/XMLSchema");

    private static final SchemaLanguage[] _SUPPORTED_LIST = 
            new SchemaLanguage[] { XSD };

    private String _name;
    private String _uri;

    private SchemaLanguage(String name, String uri) {
        _name = name;
        _uri = uri;
    }

    /**
     * The name of the language.
     *
     * By convention, this is a short, lowercase string.
     */
    public String getName() { 
        return _name;
    }

    /**
     * The URI of the language.
     */
    public String getURI() {
        return _uri;
    }

    /**
     * Return all supported schema languages.
     */
    public static SchemaLanguage[] getSupportedList() {
        return _SUPPORTED_LIST;
    }

    /**
     * Return the schema language with the given name.
     */
    public static SchemaLanguage forName(String name) throws ValidatorException {

        String ucName = name.toUpperCase();

        if (ucName.equals(XSD.getName())) {
            return XSD;
        } else {
            throw new ValidatorException("Unsupported schema language: " + name);
        }
    }

}
