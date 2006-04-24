package net.sf.bvalid.locator;

import java.io.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.util.WebClient;
import net.sf.bvalid.util.HttpInputStream;

public class WebSchemaLocator implements SchemaLocator {

    private static Logger _LOG = Logger.getLogger(WebSchemaLocator.class.getName());

    private WebClient _client;

    public WebSchemaLocator() {
        _client = new WebClient();
        _client.USER_AGENT = "BValid Schema Validator - http://bvalid.sf.net/";
    }

    public WebSchemaLocator(WebClient client) {
        _client = client;
    }

    public InputStream get(String uri, boolean required) throws ValidatorException {
        try {
            HttpInputStream in = _client.get(uri, required);
            if (!required && in.getStatusCode() != 200) {
                _LOG.debug("Failed to load schema (" + uri + ") from web, "
                        + "but it wasn't required (HTTP status code:" 
                        + in.getStatusCode() + ")");
                in.close();
                return null;
            } else {
                _LOG.info("Got schema from web: " + uri);
                return in;
            }
        } catch (Throwable th) {
            throw new ValidatorException("Unable to load schema from web: " 
                    + uri, th);
        }
    }

}
