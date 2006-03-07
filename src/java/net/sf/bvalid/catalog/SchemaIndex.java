package net.sf.bvalid.catalog;

import java.util.*;

import net.sf.bvalid.ValidatorException;

public interface SchemaIndex {

    public Iterator listURIs() throws ValidatorException;

    public String getFilename(String uri) throws ValidatorException;

    public void putFilename(String uri, String filename) throws ValidatorException;

}