package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

import net.sf.bvalid.ValidatorException;

public class FileSchemaIndexTest extends TestCase {

    private File _file;
    private FileSchemaIndex _index;

    public FileSchemaIndexTest(String name) { super (name); }

    public void setUp() throws Exception {

        _file = File.createTempFile("bvalid-test", ".dat");
        _index = new FileSchemaIndex(_file);
    }
            
    public void tearDown() {

        _file.delete();
    }

    //---------------------------------------------------------[ Test methods ]

    public void testPutFilename() throws ValidatorException {

        _index.putFilename("urn:example:1", "file1");

        Set uris = _index.getURISet();
        assertEquals("After putting, URI set had size " + uris.size() + ", not 1 as expected",
                     uris.size(),
                     1);

        assertEquals("Set did not contain expected uri", uris.contains("urn:example:1"), true);

        String filename = _index.getFilename("urn:example:1");
        assertEquals("Got wrong filename", filename, "file1");
    }

    public void testRemoveMapping() throws ValidatorException {

        testPutFilename();

        _index.removeMapping("urn:example:1");

        int size = _index.getURISet().size();
        assertEquals("After removal, set was not empty as expected", size, 0);
    }

    public static void main(String[] args) {
        TestRunner.run(FileSchemaIndexTest.class);
    }   

}
