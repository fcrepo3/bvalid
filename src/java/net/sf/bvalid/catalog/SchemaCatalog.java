package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import net.sf.bvalid.ValidatorException;

public interface SchemaCatalog {

    public boolean contains(String uri) throws ValidatorException;

    public Iterator listURIs() throws ValidatorException;

    public InputStream get(String uri) throws ValidatorException;

    public void put(String uri, InputStream in) throws ValidatorException;

    public void remove(String uri) throws ValidatorException;

}
