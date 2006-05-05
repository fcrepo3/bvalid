package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;

public abstract class SchemaCatalogTest extends TestCase {

    private static final String _URI = "http://example.org/test-schema.txt";
    private static final String _CONTENT = "Test schema content";

    public SchemaCatalogTest(String name) { super (name); }

    protected abstract SchemaCatalog getCatalog();

    public void testPut() throws Exception {

        InputStream in = new ByteArrayInputStream(_CONTENT.getBytes());

        getCatalog().put(_URI, in);

        int i = 0;
        String uri = "undefined";
        Iterator iter = getCatalog().listURIs();
        while (iter.hasNext()) {
            assertEquals("Catalog should only contain one URI at this point",
                         i,
                         0);
            uri = (String) iter.next();
            i++;
        }
        assertEquals("Catalog contains wrong URI",
                     uri,
                     _URI);

        assertEquals("Catalog.contains should have returned true after put",
                     getCatalog().contains(_URI),
                     true);

    }

    public void testGet() throws Exception {

        InputStream in = new ByteArrayInputStream(_CONTENT.getBytes());
        getCatalog().put(_URI, in);

        InputStream gotIn = getCatalog().get(_URI);

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(gotIn));
            StringBuffer buf = new StringBuffer();
            String line = reader.readLine();
            int i = 0;
            while (line != null) {
                if (i > 0) buf.append("\n");
                buf.append(line);
                line = reader.readLine();
            }
            if (_CONTENT.endsWith("\n")) {
                buf.append(_CONTENT);
            }
            assertEquals("Got different schema content than what was put",
                         buf.toString(),
                         _CONTENT);
        } finally {
            in.close();
        }

    }

    public void testRemove() throws Exception {

        InputStream in = new ByteArrayInputStream(_CONTENT.getBytes());
        getCatalog().put(_URI, in);

        getCatalog().remove(_URI);

        Iterator iter = getCatalog().listURIs();
        assertEquals("Catalog should be empty after remove",
                     iter.hasNext(),
                     false);

        in = getCatalog().get(_URI);
        assertNull("Catalog.get should have returned null after removal",
                   in);

    }

}
