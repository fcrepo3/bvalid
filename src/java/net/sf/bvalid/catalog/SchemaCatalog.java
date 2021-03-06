package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import net.sf.bvalid.ValidatorException;

/**
 * A collection of schema files, keyed by URI.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface SchemaCatalog {

    public boolean contains(String uri) throws ValidatorException;

    public Iterator listURIs() throws ValidatorException;

    /**
     * Get the schema InputStream if in the catalog, else return null.
     */
    public InputStream get(String uri) throws ValidatorException;

    public void put(String uri, InputStream in) throws ValidatorException;

    public void remove(String uri) throws ValidatorException;

}
