package net.sf.bvalid;

import java.util.*;

import org.apache.log4j.Logger;

public abstract class TestConfig {

    public final static String EXISTING_SCHEMA = "existing-schema.xsd";
    public final static String MISSING_SCHEMA = "missing-schema.xsd";

    // jetty.fork
    public static boolean JETTY_FORK = true;

    // test.datadir
    public static String TEST_DATADIR = "src/test/data/";

    // test.port
    public static int TEST_PORT = 7375;

    public static String BASE_URL;
    public static String EXISTING_SCHEMA_PATH;
    public static String EXISTING_SCHEMA_URL;
    public static String MISSING_SCHEMA_PATH;
    public static String MISSING_SCHEMA_URL;

    private static final Logger _LOG = Logger.getLogger(TestConfig.class.getName());

    static {

        String jettyFork = System.getProperty("jetty.fork");
        if (jettyFork != null) {
            boolean wasInvalid = false;
            if (jettyFork.equalsIgnoreCase("true")
                    || jettyFork.equalsIgnoreCase("yes")) {
                JETTY_FORK = true;
            } else if (jettyFork.equalsIgnoreCase("false")
                    || jettyFork.equalsIgnoreCase("no")) {
                JETTY_FORK = false;
            } else {
                _LOG.warn("Specified jetty.fork was invalid, using default: " + JETTY_FORK);
                wasInvalid = true;
            }
            if (!wasInvalid) {
                _LOG.info("Using specified jetty.fork: " + JETTY_FORK);
            }
        } else {
            _LOG.info("Using default jetty.fork: " + JETTY_FORK);
        }

        String testDatadir = System.getProperty("test.datadir");
        if (testDatadir != null) {
            TEST_DATADIR = testDatadir;
            if (!TEST_DATADIR.endsWith("/")) {
                TEST_DATADIR += "/";
            }
            _LOG.info("Using specified test.datadir: " + TEST_DATADIR);
        } else {
            _LOG.info("Using default test.datadir: " + TEST_DATADIR);
        }

        String testPort = System.getProperty("test.port");
        if (testPort != null) {
            try {
                TEST_PORT = Integer.parseInt(testPort);
                _LOG.info("Using specified test.port: " + TEST_PORT);
            } catch (Exception e) {
                _LOG.warn("Specified test.port was invalid, using default: " + TEST_PORT);
            }
        } else {
            _LOG.info("Using default test.port: " + TEST_PORT);
        }

        BASE_URL = "http://localhost:" + TEST_PORT + "/";

        EXISTING_SCHEMA_PATH = TEST_DATADIR + EXISTING_SCHEMA;
        EXISTING_SCHEMA_URL = BASE_URL + EXISTING_SCHEMA;
        MISSING_SCHEMA_PATH = TEST_DATADIR + MISSING_SCHEMA;
        MISSING_SCHEMA_URL = BASE_URL + MISSING_SCHEMA;

    }

}
