package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import net.sf.bvalid.ValidatorException;

/**
 * A <code>SchemaCatalog</code> based entirely in memory.
 *
 * @author cwilper@cs.cornell.edu
 */
public class MemorySchemaCatalog implements SchemaCatalog {

    private Map _map;

    public MemorySchemaCatalog() {
        _map = new HashMap();
    }

    public synchronized Iterator listURIs() {
        return new HashSet(_map.keySet()).iterator(); // threadsafe copy
    }

    public synchronized boolean contains(String uri) {
        return _map.containsKey(uri);
    }

    public synchronized InputStream get(String uri) {

        byte[] bytes = (byte[]) _map.get(uri);
        if (bytes == null) {
            return null;
        } else {
            return new ByteArrayInputStream(bytes);
        }
    }

    public synchronized void put(String uri, InputStream in) throws ValidatorException {
        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int len;
            while ( ( len = in.read( buf ) ) > 0 ) {
                out.write( buf, 0, len );
            }
            byte[] bytes = out.toByteArray();

            _map.put(uri, bytes);

        } catch (Throwable th) {
            throw new ValidatorException("Error reading schema inputstream", th);
        } finally {
            try { in.close(); } catch (Exception e) { }
        }
    }    

    public synchronized void remove(String uri) {
        _map.remove(uri);
    }

}
