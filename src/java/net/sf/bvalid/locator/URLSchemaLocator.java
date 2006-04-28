package net.sf.bvalid.locator;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.util.WebClient;

/**
 *
 * A <code>SchemaLocator</code> that resolves file:// URLs using the local
 * filesystem, and http:// URLs using a <code>WebClient</code>.
 *
 * @author cwilper@cs.cornell.edu
 */
public class URLSchemaLocator implements SchemaLocator {

    private static Logger _LOG = Logger.getLogger(URLSchemaLocator.class.getName());

    private WebClient _client;

    public URLSchemaLocator() {
        _client = new WebClient();
        _client.USER_AGENT = "BValid Schema Validator - http://bvalid.sf.net/";
    }

    public URLSchemaLocator(WebClient client) {
        _client = client;
    }

    public InputStream get(String uri, boolean required) throws ValidatorException {

        try {
            if (uri.startsWith("http://") || uri.startsWith("https://")) {
                InputStream in = _client.get(uri, true);
                _LOG.info("Resolved from web via http URL: " + uri);
                return in;
            } else if (uri.startsWith("file:")) {
                URL fileURL = new URL(uri);
                File localFile = new File(fileURL.getFile());
                if (!localFile.exists()) {
                    throw new IOException("File does not exist: " 
                            + localFile.getPath());
                } else {
                    _LOG.info("Resolved from filesystem via file URL: " 
                            + uri);
                    return new FileInputStream(localFile);
                }
            } else {
                throw new ValidatorException("Unrecognized scheme in schema "
                        + "URL: " + uri);
            }
        } catch (Exception e) {
            if (!required) {
                _LOG.info("Not resolved, but not required: " + uri + ": " 
                        + " (cause: " + e.getClass().getName() + ": " 
                        + e.getMessage() + ")");
                return null;
            } else {
                throw new ValidatorException("Unable to resolve schema: " + uri, e);
            }
        }
    }

    public void successfullyUsed(String uri) { }

}
