package net.sf.bvalid.catalog;

import junit.framework.TestCase;
import junit.swingui.TestRunner;

public class MemorySchemaCatalogTest extends SchemaCatalogTest {

    private SchemaCatalog _catalog;

    public MemorySchemaCatalogTest(String name) { super (name); }

    public void setUp() {
        _catalog = new MemorySchemaCatalog();
    }
            
    public void tearDown() {
    }

    protected SchemaCatalog getCatalog() {
        return _catalog;
    }

    public static void main(String[] args) {
        TestRunner.run(MemorySchemaCatalogTest.class);
    }   

}
