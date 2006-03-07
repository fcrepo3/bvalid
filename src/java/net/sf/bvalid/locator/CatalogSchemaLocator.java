package net.sf.bvalid.locator;

import java.io.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.catalog.SchemaCatalog;

public class CatalogSchemaLocator implements SchemaLocator {

    private static Logger _LOG = Logger.getLogger(CatalogSchemaLocator.class.getName());

    private SchemaCatalog _catalog;

    public CatalogSchemaLocator(SchemaCatalog catalog) {
        _catalog = catalog;
    }

    public InputStream get(String uri, boolean required) throws ValidatorException {
        InputStream in = _catalog.get(uri);
        if (in == null && required) {
            throw new ValidatorException("Schema not found in catalog: " + uri);
        } else {
            _LOG.info("Got schema from catalog: " + uri);
            return in;
        }
    }

}