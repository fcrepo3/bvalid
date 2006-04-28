package net.sf.bvalid;

import java.io.*;
import java.util.*;

import net.sf.bvalid.catalog.DiskSchemaCatalog;
import net.sf.bvalid.catalog.FileSchemaIndex;
import net.sf.bvalid.catalog.MemorySchemaCatalog;
import net.sf.bvalid.catalog.SchemaCatalog;
import net.sf.bvalid.catalog.SchemaIndex;
import net.sf.bvalid.locator.CachingSchemaLocator;
import net.sf.bvalid.locator.SchemaLocator;
import net.sf.bvalid.locator.URLSchemaLocator;
import net.sf.bvalid.xsd.XercesXSDValidator;

/**
 * Provides methods for getting Validator instances.
 *
 * @author cwilper@cs.cornell.edu
 */
public abstract class ValidatorFactory {

    /**
     * Get a validator without any schema file caching.
     */
    public static Validator getValidator(SchemaLanguage lang,
                                         Map validatorOptions)
            throws ValidatorException {

        return getValidator(lang, 
                            new URLSchemaLocator(), 
                            validatorOptions);
    }

    /**
     * Get a validator with automatic schema file caching.
     */
    public static Validator getValidator(SchemaLanguage lang,
                                         File cacheDir,
                                         Map options)
            throws ValidatorException {

        // create the cache directory if it doesn't already exist
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
            if (!cacheDir.exists()) {
                throw new ValidatorException("Unable to create schema "
                        + "cache directory: " + cacheDir.getPath());
            }
        }

        // use a file-based index for the cache, in cacheDir
        File indexFile = new File(cacheDir, "index.dat");
        SchemaIndex index = new FileSchemaIndex(indexFile);

        // the disk-based catalog we'll use for the cache
        SchemaCatalog catalog = 
                new DiskSchemaCatalog(index,
                                      cacheDir);

        // use a caching locator
        SchemaLocator locator = 
                new CachingSchemaLocator(new MemorySchemaCatalog(),
                                         catalog,
                                         new URLSchemaLocator());

        return getValidator(lang, locator, options);
    }

    /**
     * Get a validator that uses the provided <code>SchemaLocator</code>.
     */
    public static Validator getValidator(SchemaLanguage lang,
                                         SchemaLocator locator,
                                         Map options)
            throws ValidatorException {

        if (options == null) options = new HashMap();

        boolean failOnMissingReferenced =
            getBoolean(options, ValidatorOption.FAIL_ON_MISSING_REFERENCED);

        boolean cacheParsedGrammars =
            getBoolean(options, ValidatorOption.CACHE_PARSED_GRAMMARS);

        if (lang == SchemaLanguage.XSD) {
            return new XercesXSDValidator(locator, 
                                          failOnMissingReferenced,
                                          cacheParsedGrammars);
        } else {
            throw new ValidatorException("Unrecognized schema language: " 
                    + lang.toString());
        }
    }

    private static boolean getBoolean(Map options, 
                                      ValidatorOption option)
            throws ValidatorException {

        return getBoolean((String) options.get(option),
                          option.getDefaultValue());
    }

    private static boolean getBoolean(String value, 
                                      String defaultValue)
            throws ValidatorException {

        if (value == null) {
            value = defaultValue;
        }
        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            throw new ValidatorException("Unrecognized boolean value: '" 
                    + value + "', expected 'true' or 'false'");
        }
    }

}
