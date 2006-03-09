package net.sf.bvalid;

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

    public String getName() { 
        return _name;
    }

    public String getURI() {
        return _uri;
    }

    public static SchemaLanguage[] getSupportedList() {
        return _SUPPORTED_LIST;
    }

    public static SchemaLanguage forName(String name) throws ValidatorException {

        String ucName = name.toUpperCase();

        if (ucName.equals(XSD.getName())) {
            return XSD;
        } else {
            throw new ValidatorException("Unsupported schema language: " + name);
        }
    }

}
