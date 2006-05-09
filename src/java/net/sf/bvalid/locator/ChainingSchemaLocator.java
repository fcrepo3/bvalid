package net.sf.bvalid.locator;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;

/**
 * A <code>SchemaLocator</code> that uses multiple locators, in order,
 * to resolve schema files.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ChainingSchemaLocator implements SchemaLocator {

    private static Logger _LOG = Logger.getLogger(ChainingSchemaLocator.class.getName());

    private List _locators;

    public ChainingSchemaLocator(List locators) {
        _locators = locators;
    }

    public InputStream get(String uri, boolean required) throws ValidatorException {

        int i = 0;
        while (i < (_locators.size() - 1)) {
            InputStream in = ((SchemaLocator) _locators.get(i)).get(uri, false);
            if (in != null) return in;
            i++;
        }

        return ((SchemaLocator) _locators.get(i)).get(uri, required);
    }

    public void successfullyUsed(String uri) throws ValidatorException {

        // carry the message down the chain
        int i = 0;
        while (i < (_locators.size() - 1)) {
            ((SchemaLocator) _locators.get(i)).successfullyUsed(uri);
        }
    }

}
