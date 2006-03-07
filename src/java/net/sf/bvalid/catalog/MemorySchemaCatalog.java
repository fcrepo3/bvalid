package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import net.sf.bvalid.ValidatorException;

public class MemorySchemaCatalog implements SchemaCatalog {

    private Map _map;

    public MemorySchemaCatalog() {
        _map = new HashMap();
    }

    public boolean contains(String uri) {
        return _map.containsKey(uri);
    }

    public InputStream get(String uri) {

        byte[] bytes = (byte[]) _map.get(uri);
        if (bytes == null) {
            return null;
        } else {
            return new ByteArrayInputStream(bytes);
        }
    }

    public void put(String uri, InputStream in) throws ValidatorException {
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

    public void remove(String uri) {
        _map.remove(uri);
    }

}