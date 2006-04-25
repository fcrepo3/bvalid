package net.sf.bvalid;

import java.io.*;

import net.sf.bvalid.catalog.DiskSchemaCatalog;
import net.sf.bvalid.catalog.FileSchemaIndex;
import net.sf.bvalid.catalog.MemorySchemaCatalog;
import net.sf.bvalid.catalog.SchemaIndex;
import net.sf.bvalid.locator.CachingSchemaLocator;
import net.sf.bvalid.locator.SchemaLocator;
import net.sf.bvalid.locator.WebSchemaLocator;

public class BValid {

    public static void main(String[] args) throws Exception {

        // tell commons-logging to use log4j
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");

        File cacheDir = new File("cache");
        cacheDir.mkdirs();
        SchemaIndex index = new FileSchemaIndex(new File("schema-index.txt"));
        DiskSchemaCatalog cacheCatalog = new DiskSchemaCatalog(index, cacheDir);

        SchemaLocator cachingLocator = 
                new CachingSchemaLocator(new MemorySchemaCatalog(),
                                         cacheCatalog,
                                         new WebSchemaLocator());

        Validator validator = 
                ValidatorFactory.getValidator(SchemaLanguage.XSD,
                                              cachingLocator,
                                              true);

        try {
            validator.validate(new FileInputStream(new File(args[0])));
            validator.validate(new FileInputStream(new File(args[0])));
            validator.validate(new FileInputStream(new File(args[0])));
            validator.validate(new FileInputStream(new File(args[0])));
            System.out.println("OK");
            System.exit(0);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Underlying error:");
                e.getCause().printStackTrace();
            }
            System.exit(1);
        }
    }

}
