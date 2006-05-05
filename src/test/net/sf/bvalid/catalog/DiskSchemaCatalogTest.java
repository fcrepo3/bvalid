package net.sf.bvalid.catalog;

import java.io.*;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

public class DiskSchemaCatalogTest extends SchemaCatalogTest {

    private SchemaCatalog _catalog;

    private File _storageDir;

    public DiskSchemaCatalogTest(String name) { super (name); }

    public void setUp() throws Exception {

        _storageDir = File.createTempFile("bvalid-testschemas", "");
        _storageDir.delete();
        _storageDir.mkdir();

        _catalog = new DiskSchemaCatalog(
                new FileSchemaIndex(new File(_storageDir, "index.dat")),
                _storageDir);
    }
            
    public void tearDown() {

        if (_storageDir != null && _storageDir.exists()) {
            if (_storageDir.isDirectory()) {
                File[] files = _storageDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            _storageDir.delete();
        }
    }

    protected SchemaCatalog getCatalog() {
        return _catalog;
    }

    public static void main(String[] args) {
        TestRunner.run(DiskSchemaCatalogTest.class);
    }   

}
