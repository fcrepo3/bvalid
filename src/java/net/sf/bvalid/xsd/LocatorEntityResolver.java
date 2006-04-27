package net.sf.bvalid.xsd;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import net.sf.bvalid.ValidatorException;
import net.sf.bvalid.locator.SchemaLocator;

/**
 * An <code>EntityResolver</code> that does its work using a
 * <code>SchemaLocator</code>.
 *
 * @author cwilper@cs.cornell.edu
 */
public class LocatorEntityResolver implements EntityResolver {

    private static Logger _LOG = Logger.getLogger(LocatorEntityResolver.class.getName());

    private SchemaLocator _locator;
    private boolean _mustResolveAll;
    private String _primarySchemaURI;

    private String _lastResolvedSchemaURL;
    private Set _resolvedURIs;

    public LocatorEntityResolver(SchemaLocator locator,
                                 boolean mustResolveAll,
                                 String primarySchemaURI) {
        _locator = locator;
        _mustResolveAll = mustResolveAll;
        _primarySchemaURI = primarySchemaURI;

        _resolvedURIs = new HashSet();
    }

    // 
    public Set getResolvedURIs() {
        return _resolvedURIs;
    }

    //-------------------------------------------[ org.xml.sax.EntityResolver ]

    /**
     * Resolve the entity using our <code>SchemaLocator</code>.
     *
     * If the URI cannot be deferenced, and:
     * <ul>
     *   <li> <code>mustResolveAll</code> is <code>true</code>, an exception 
     *        will be thrown.</li>
     *   <li> the URI matches the <code>primarySchemaURI</code> (if provided),
     *        an exception will be thrown.</li>
     *   <li> <code>mustResolveAll</code> is <code>false</code>, an empty
     *        InputSource will be returned.</li>
     * </ul>
     */ 
    public InputSource resolveEntity(String publicId, 
                                     String systemId) throws SAXException {


        if (publicId != null) {
            try {
                return resolveDTD(publicId, systemId);
            } catch (ValidatorException e) {
                throw new SAXException("Error resolving DTD", e);
            }
        }

        _LOG.info("Resolving schema: " + systemId);
     
        InputStream in;
        try {
            in = _locator.get(systemId, _mustResolveAll);
        } catch (ValidatorException e) {
            throw new SAXException("Unable to resolve required schema", e);
        }

        if (in == null) {
            if (_primarySchemaURI != null 
                    && _primarySchemaURI.equals(systemId)) {
                throw new SAXException("Unable to resolve primary schema");
            } else {
                // wasn't primary schema and wasn't required
                return new InputSource();
            }
        } else {
            // successfully resolved
            if (systemId.startsWith("http://")) {
                _lastResolvedSchemaURL = systemId;
            }
            _resolvedURIs.add(systemId);
            return new InputSource(in);
        }

    }

    /**
     * Resolve the DTD or throw a ValidatorException.
     */
    private InputSource resolveDTD(String publicId, String systemId) throws ValidatorException {

        String uri = getDTDURI(publicId, systemId);

        // TODO: Implement DTD whitelisting???

        _LOG.info("Resolving DTD: " + uri);
        InputSource inputSource = new InputSource(_locator.get(uri, true));
        _resolvedURIs.add(uri);
        return inputSource;

    }

    /**
     * Get the URI indicating the location of the DTD.
     */
    private String getDTDURI(String publicId, String systemId) throws ValidatorException {

        _LOG.debug("Determining URI for DTD (publicId=" + publicId + ", systemId=" + systemId + ")");

        if (systemId.startsWith("http:")) return systemId;

        if (!systemId.startsWith("file:")) {
            throw new ValidatorException("Unrecognized scheme for DTD systemId: " + systemId);
        }

        // first, check if it's really a local file.
        URL url;
        try {
            url = new URL(systemId);
        } catch (Exception e) { 
            throw new ValidatorException("Cannot convert to file URL: " + systemId);
        }
        File f = new File(url.getFile());

        if (f.exists() || _lastResolvedSchemaURL == null) {
            // assume it should be resolved as-is
            return systemId;
        } else {
            // see if we can convert to url
            String cwd = new File("").getAbsolutePath();
            String fPath = f.getPath();
            if (fPath.startsWith(cwd)) {
                // yes... so use last url as prefix, and diff as suffix
                try {
                    URL lastURL = new URL(_lastResolvedSchemaURL);
                    URL thisURL = new URL(lastURL, fPath.substring(cwd.length() + 1));
                    return thisURL.toString();
                } catch (Exception e) {
                    throw new ValidatorException("Error converting to relative URL: " + systemId, e);
                }
            } else {
                // no, so assume it should be resolved as-is
                return systemId;
            }
        }

    }

}
