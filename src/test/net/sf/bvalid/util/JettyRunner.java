package net.sf.bvalid.util;

import java.io.*;

import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;

/**
 * Runs a Jetty instance for testing.
 *
 * With this utility, Jetty can be run within the current VM or
 * in a subprocess.  Running it within a subprocess is useful
 * when classloader conflicts arise (as is the case with JUnit's
 * GUI test runner).
 *
 * FIXME: Currently, error reporting is weak in forked mode -- only
 *        the top-level error message is reported.
 *
 * cwilper@cs.cornell.edu
 */ 
public class JettyRunner {

    private Server _server;

    private Process _runnerProcess;
    private BufferedReader _stdout;
    private PrintWriter _stdin;

    private boolean _running;

    /**
     * Create a <code>JettyRunner</code>.
     *
     * If <code>fork</code> is true, it will be launched in a subprocess.
     */ 
    public JettyRunner(int port,
                       String contextPath,
                       String webappPath,
                       boolean fork) throws IOException {

        if (fork) {

            String cmd = "java -cp " + System.getProperty("java.class.path")
                    + " " + getClass().getName() + " "
                    + port + " " + contextPath + " " + webappPath + "";

            _runnerProcess = Runtime.getRuntime().exec(cmd, null, new File("."));

            _stdout = new BufferedReader(new InputStreamReader(_runnerProcess.getInputStream()));
            _stdin = new PrintWriter(new OutputStreamWriter(_runnerProcess.getOutputStream()));

        } else {
            _server = new Server();
            _server.addListener(new InetAddrPort(port));
            _server.addWebApplication(contextPath, webappPath);
        }
    }

    /**
     * Start the Jetty instance.
     */
    public void start() throws Exception {
        if (_runnerProcess != null) {
            readUntil("[Press ENTER");
            _stdin.println();
            _stdin.flush();
            readUntil("STARTED");
        } else {
            _server.start();
        }
        _running = true;
    }

    private void readUntil(String lineStart) throws Exception {

        String line = _stdout.readLine();
        while (!line.startsWith(lineStart)) {
            if (line.startsWith("ERROR: ")) {
                _runnerProcess.waitFor();
                throw new Exception("Error from subprocess: " + line.substring(7));
            }
            line = _stdout.readLine();
        }
    }

    /**
     * Stop the Jetty instance.
     */
    public void stop() throws Exception {
        if (_running) {
            if (_runnerProcess != null) {
                readUntil("[Press ENTER");
                _stdin.println();
                _stdin.flush();
                readUntil("STOPPED");
                _runnerProcess.waitFor();
            } else {
                _server.stop();
            }
            _running = false;
        }
    }

    /**
     * Ensure Jetty is stopped at GC time.
     */
    public void finalize() {
        if (_running) try { stop(); } catch (Exception e) { }
    }

    /**
     * Command-line entry point.
     *
     * This is used to support forking.  It can also be used for testing.
     */
    public static void main(String[] args) {

        try {
            if (args.length < 3 || args.length > 4) {
                System.out.println("ERROR: Wrong number of arguments, need port contextPath webappPath [fork]");
                System.exit(1);
            }
            int port = Integer.parseInt(args[0]);
            boolean fork = false;
            if (args.length == 4) {
                if (args[3].equalsIgnoreCase("true") 
                        || args[3].equalsIgnoreCase("yes")
                        || args[3].equalsIgnoreCase("fork")) {
                    fork = true;
                }
            }
            System.out.println("Server Port  : " + port);
            System.out.println("Context Path : " + args[1]);
            System.out.println("Webapp Path  : " + args[2]);
            JettyRunner runner = new JettyRunner(port, args[1], args[2], fork);

            System.out.println("[Press ENTER to start]");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            runner.start();
            System.out.println("STARTED");
            System.out.println("[Press ENTER to stop]");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            runner.stop();
            System.out.println("STOPPED");
            System.exit(0);
        } catch (Exception e) {
            String msg = e.getClass().getName();
            if (e.getMessage() != null) msg += ": " + e.getMessage();
            System.out.println("ERROR: " + e.getMessage());
            System.exit(1);
        }
    }

}
