package net.sf.bvalid.catalog;

import java.util.*;

import net.sf.bvalid.ValidatorException;

/**
 * A mapping of schema URI-to-filename.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface SchemaIndex {

    public Set getURISet() throws ValidatorException;

    public String getFilename(String uri) throws ValidatorException;

    public String putFilename(String uri, String filename) throws ValidatorException;

    public boolean removeMapping(String uri) throws ValidatorException;

}
