package net.sf.bvalid.util;

import junit.extensions.TestSetup;
import junit.framework.Test;

public class JettyTestSetup extends TestSetup {

    private JettyRunner _jetty;

    public JettyTestSetup(Test test,
                          int port,
                          String contextPath,
                          String webappPath,
                          boolean fork) throws Exception {
        super(test);
        _jetty = new JettyRunner(port, contextPath, webappPath, fork);
    }

    protected void setUp() throws Exception {
        _jetty.start();
    }

    protected void tearDown() throws Exception {
        _jetty.stop();
    }

}
