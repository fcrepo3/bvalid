package net.sf.bvalid.locator;

import java.io.*;

import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.catalog.SchemaCatalog;

public class CachingSchemaLocator implements SchemaLocator {

    private SchemaCatalog _catalog;
    private SchemaLocator _locator;

    public CachingSchemaLocator(SchemaCatalog catalog,
                                SchemaLocator locator) {
        _catalog = catalog;
        _locator = locator;
    }

    public InputStream get(String uri, 
                           boolean required) throws ValidatorException {

        InputStream in = _catalog.get(uri);
        if (in != null) {
            // found in catalog, just return it
            return in;
        } else {
            // not found in catalog, try locator
            in = _locator.get(uri, required);
            if (in == null) {
                return null;
            } else {
                // found by locator; cache it, then re-read it from catalog
                _catalog.put(uri, in);
                return _catalog.get(uri);
            }
        }

    }

}