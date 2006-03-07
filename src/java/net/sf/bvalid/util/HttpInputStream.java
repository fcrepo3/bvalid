package net.sf.bvalid.util;

import java.io.*;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

/**
 * An InputStream from an HttpMethod.
 *
 * When this InputStream is close()d, the underlying http connection is
 * automatically released.
 */
public class HttpInputStream extends InputStream {

    private HttpClient _client;
    private HttpMethod _method;
    private String _url;

    private int _code;
    private InputStream _in;

    public HttpInputStream(HttpClient client,
                           HttpMethod method,
                           String url) throws IOException {
        _client = client;
        _method = method;
        _url = url;
        try {
            _code = _client.executeMethod(_method);
            _in = _method.getResponseBodyAsStream();
            if (_in == null) _in = new ByteArrayInputStream(new byte[0]);
        } catch (IOException e) {
            _method.releaseConnection();
            throw e;
        }
    }

    /**
     * Get the http method name (GET or POST).
     */
    public String getMethodName() {
        return _method.getName();
    }

    /**
     * Get the original URL of the http request this InputStream is based on.
     */
    public String getURL() {
        return _url;
    }

    /**
     * Get the http status code.
     */
    public int getStatusCode() {
        return _code;
    }

    /**
     * Get the "reason phrase" associated with the status code.
     */
    public String getStatusText() {
        return _method.getStatusLine().getReasonPhrase();
    }

    /**
     * Get a specific response header.
     */
    public Header getResponseHeader(String name) {
        return _method.getResponseHeader(name);
    }

    /**
     * Get a response header value string, or <code>defaultValue</code>
     * if the header is undefined or empty.
     */
    public String getResponseHeaderValue(String name, String defaultValue) {
        Header header = _method.getResponseHeader(name);
        if (header == null) {
            return defaultValue;
        } else {
            String value = header.getValue();
            if (value == null || value.length() == 0) {
                return defaultValue;
            } else {
                return header.getValue();
            }
        }
    }

    /**
     * Get all response headers.
     */
    public Header[] getResponseHeaders() {
        return _method.getResponseHeaders();
    }

    /**
     * Automatically close on garbage collection.
     */
    public void finalize() {
        try { close(); } catch (Exception e) { }
    }

    //////////////////////////////////////////////////////////////////////////
    /////////////////// Methods from java.io.InputStream /////////////////////
    //////////////////////////////////////////////////////////////////////////

    public int read() throws IOException { return _in.read(); }
    public int read(byte[] b) throws IOException { return _in.read(b); }
    public int read(byte[] b, int off, int len) throws IOException { return _in.read(b, off, len); }
    public long skip(long n) throws IOException { return _in.skip(n); }
    public int available() throws IOException { return _in.available(); }
    public void mark(int readlimit) { _in.mark(readlimit); }
    public void reset() throws IOException { _in.reset(); }
    public boolean markSupported() { return _in.markSupported(); }

    /**
     * Release the underlying http connection and close the InputStream.
     */
    public void close() throws IOException {
        _method.releaseConnection();
        _in.close();
    }
}