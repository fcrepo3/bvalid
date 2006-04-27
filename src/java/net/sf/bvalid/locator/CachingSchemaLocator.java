package net.sf.bvalid.locator;

import java.io.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.catalog.SchemaCatalog;

/**
 * A <code>SchemaLocator</code> that automatically caches successfully-used
 * schemas to an underlying catalog.
 *
 * @author cwilper@cs.cornell.edu
 */
public class CachingSchemaLocator implements SchemaLocator {

    private static Logger _LOG = Logger.getLogger(CachingSchemaLocator.class.getName());

    private SchemaCatalog _candidateCatalog;
    private SchemaCatalog _cacheCatalog;
    private SchemaLocator _locator;

    public CachingSchemaLocator(SchemaCatalog candidateCatalog,
                                SchemaCatalog cacheCatalog,
                                SchemaLocator locator) {
        _candidateCatalog = candidateCatalog;
        _cacheCatalog = cacheCatalog;
        _locator = locator;
    }

    public synchronized InputStream get(String uri, 
                           boolean required) throws ValidatorException {

        InputStream in = _cacheCatalog.get(uri);
        if (in != null) {
            // found in cache catalog, just return it
            _LOG.info("Got from cache: " + uri);
            return in;
        } else {
            // not found in catalog, try locator
            in = _locator.get(uri, required);
            if (in == null) {
                return null;
            } else {
                // found by locator; add to candidateCatalog, then re-read it
                _LOG.debug("Added to candidate catalog: " + uri);
                _candidateCatalog.put(uri, in);
                return _candidateCatalog.get(uri);
            }
        }

    }

    public synchronized void successfullyUsed(String uri) throws ValidatorException {

        if (!_cacheCatalog.contains(uri)) {
            // not yet cached
            if (_candidateCatalog.contains(uri)) {
                // is candidate, so move from candidate to cache catalog
                InputStream in = _candidateCatalog.get(uri);
                _cacheCatalog.put(uri, in);
                _candidateCatalog.remove(uri);
                _LOG.debug("Cached because successfully used: " + uri);
            }
        }
    }

}
