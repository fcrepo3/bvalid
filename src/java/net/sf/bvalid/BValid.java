package net.sf.bvalid;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * Command-line utility for validating instance documents.
 *
 * @author cwilper@cs.cornell.edu
 */
public class BValid {

    private static Logger _LOG = Logger.getLogger(BValid.class.getName());

    private Options _opts;
    private String _version;
    private String _buildDate;

    public BValid(String[] args) throws ArgException {

        // get version and buildDate from properties
        Properties props = new Properties();
        InputStream in = BValid.class.getClassLoader().getResourceAsStream("net/sf/bvalid/BValid.properties");
        try {
            props.load(in);
            _version = props.getProperty("bvalid.version");
            _buildDate = props.getProperty("bvalid.buildDate");
        } catch (Exception e) {
            throw new RuntimeException("Error loading bvalid.properties");
        } finally {
            try { in.close(); } catch (Exception e) { }
        }

        _opts = new Options(args);
    }

    private String getVersionLine() {
        return "BValid version " + _version + " (build date: " + _buildDate + ")";
    }

    private void showVersion() {
        System.out.println(getVersionLine());
    }

    private void showUsage() {
        System.out.println("Usage: bvalid [OPTIONS] LANG XMLFILE");
        System.out.println("   Or: bvalid --version");
        System.out.println("   Or: bvalid --help");
        System.out.println("");
        System.out.println("Where:");
        System.out.println("  LANG            a supported schema language, such as xsd");
        System.out.println("  XMLFILE         the path to the instance file to validate");
        System.out.println("");
        System.out.println("Options:");
        System.out.println("  -cf, --cache-files     Cache schema files locally");
        System.out.println("  -co, --cache-objects   Cache parsed grammar objects in memory");
        System.out.println("  -am, --allow-missing   Allow missing referenced schemas.  If the instance");
        System.out.println("                         includes references to schemas that can't be found,");
        System.out.println("                         this will skip them rather than failing validation.");
        System.out.println("  --repeat=n             Repeat the validation n times (for testing)");
        System.out.println("  --schema=file          Use the given schema file (url or filename)");
        System.out.println("  -v, --version          Print version and exit (exclusive option)");
        System.out.println("  -h, --help             Print help and exit (exclusive option)");
        System.out.println("");
    }

    public void run() throws Exception {

        if (_opts.showVersion()) {
            showVersion();
        } else if (_opts.showUsage()) {
            showVersion();
            System.out.println();
            showUsage();
        } else {
            _LOG.info(getVersionLine());
            
            // construct our validator
            Validator validator;
            Map vopts = new HashMap();
            if (_opts.cacheObjects()) {
                vopts.put(ValidatorOption.CACHE_PARSED_GRAMMARS, "true");
            }
            if (_opts.allowMissing()) {
                vopts.put(ValidatorOption.FAIL_ON_MISSING_REFERENCED, "false");
            }
            File cacheDir = null;
            if (_opts.cacheFiles()) {
                cacheDir = File.createTempFile("bvalid-schemaCache", "");
                cacheDir.delete();
                cacheDir.mkdir();
                validator = ValidatorFactory.getValidator(_opts.getLang(),
                                                           cacheDir,
                                                           vopts);
            } else {
                validator = ValidatorFactory.getValidator(_opts.getLang(),
                                                           vopts);
            }

            try {
                runValidation(validator, 
                              _opts.getXMLFile(),
                              _opts.getSchema(),
                              _opts.getRepeat());
            } finally {

                // if we created cache dir, clean it up
                if (cacheDir != null && cacheDir.isDirectory()) {
                    try {
                        File[] files = cacheDir.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            files[i].delete();
                        }
                        cacheDir.delete();
                    } catch (Exception e) { }
                }
            }
        }
    }

    private void runValidation(Validator validator,
                               File xmlFile,
                               String schema,
                               int times) throws Exception {

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < times; i++) {

            long st = System.currentTimeMillis();
            try {
                InputStream in = new FileInputStream(xmlFile);
                if (schema != null) {
                    validator.validate(in, schema);
                } else {
                    validator.validate(in);
                }
                long ms = System.currentTimeMillis() - st;
                _LOG.info("Validation of " + xmlFile.getPath() + " succeeded in " + ms + "ms.");
            } catch (ValidationException e) {
                long ms = System.currentTimeMillis() - st;
                if (e.getCause() != null) {
                    _LOG.error("Validation of " + xmlFile.getPath() + " failed in " + ms + "ms.", e.getCause());
                } else {
                    _LOG.error("Validation of " + xmlFile.getPath() + " failed in " + ms + "ms.\n" + e.getMessage());
                }
            }
        }
        long total = System.currentTimeMillis() - startTime;
        long msPerDoc = total / times;
        _LOG.info("Overall validation rate: " + msPerDoc + "ms/doc");
    }

    public static void main(String[] args) {

        // tell commons-logging to use log4j
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");

        int errorLevel = 1;
        try {
            BValid bvalid = new BValid(args);
            bvalid.run();
            errorLevel = 0;
        } catch (ArgException e) {
            System.out.println("bvalid: " + e.getMessage() + " ( -h to show usage )");
        } catch (Exception e) {
            _LOG.fatal("Unexpected error", e);
        }
        System.exit(errorLevel);
    }

    public class Options {

        private SchemaLanguage _lang;
        private File _xmlFile;
        private boolean _cacheFiles;
        private boolean _cacheObjects;
        private boolean _allowMissing;
        private int _repeat = 1;
        private String _schema;

        private boolean _showVersion;
        private boolean _showUsage;

        public Options(String[] args) throws ArgException {

            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-")) {
                    if (args[i].equals("-cf") || args[i].equals("--cache-files")) {
                        _cacheFiles = true;
                    } else if (args[i].equals("-co") || args[i].equals("--cache-objects")) {
                        _cacheObjects = true;
                    } else if (args[i].equals("-am") || args[i].equals("--allow-missing")) {
                        _allowMissing=true;
                    } else if (args[i].startsWith("--repeat=")) {
                        String value = args[i].substring(9);
                        try {
                            _repeat = Integer.parseInt(value);
                        } catch (Exception e) {
                            throw new ArgException("must specify integer value for repeat option");
                        }
                    } else if (args[i].startsWith("--schema=")) {
                        String value = args[i].substring(9);
                        if (!value.startsWith("/") && value.indexOf(":") != -1) {
                            _schema = value; // assume they gave uri
                        } else {
                            try {
                                _schema = new File(value).toURI().toString(); 
                            } catch (Exception e) {
                                throw new RuntimeException("cannot get URI for local schema file: " + value, e);
                            }
                        }
                    } else if (args[i].equals("-h") || args[i].equals("--help")) {
                        _showUsage = true;
                        if (args.length > 1) {
                            throw new ArgException("option is exclusive: " + args[i]);
                        }
                    } else if (args[i].equals("-v") || args[i].equals("--version")) {
                        _showVersion = true;
                        if (args.length > 1) {
                            throw new ArgException("option is exclusive: " + args[i]);
                        }
                    } else {
                        throw new ArgException("unrecognized argument: " + args[i]);
                    }
                } else {
                    if (_lang == null) {
                        try {
                            _lang = SchemaLanguage.forName(args[i]);
                        } catch (Exception e) {
                            throw new ArgException(e.getMessage());
                        }
                    } else if (_xmlFile == null) {
                        _xmlFile = new File(args[i]);
                    } else {
                        throw new ArgException("Too many arguments");
                    }
                }

            }

            if (!_showVersion && !_showUsage && _xmlFile == null) {
                throw new ArgException("Too few arguments");
            }
        }

        public SchemaLanguage getLang() {
            return _lang;
        }

        public File getXMLFile() {
            return _xmlFile;
        }

        public boolean cacheFiles() {
            return _cacheFiles;
        }

        public boolean cacheObjects() {
            return _cacheObjects;
        }

        public boolean allowMissing() {
            return _allowMissing;
        }

        public int getRepeat() {
            return _repeat;
        }

        public String getSchema() {
            return _schema;
        }

        public boolean showVersion() {
            return _showVersion;
        }

        public boolean showUsage() {
            return _showUsage;
        }

    }

    public class ArgException extends Exception {

        public ArgException(String message) {
            super(message);
        }

    }

}
