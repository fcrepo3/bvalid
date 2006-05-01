package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;

/**
 * A file-backed <code>SchemaIndex</code> implementation.
 *
 * @author cwilper@cs.cornell.edu
 */
public class FileSchemaIndex implements SchemaIndex {

    public static final int DEFAULT_REFRESH_SECONDS = 60;

    private static Logger _LOG = 
            Logger.getLogger(FileSchemaIndex.class.getName());

    private File _indexFile;
    private Map _indexMap;
    private int _refreshSeconds;
    private long _nextRefreshTime;

    //------------------------------------------------------[ Initialization ]

    public FileSchemaIndex(File indexFile)
            throws ValidatorException {

        this(indexFile, DEFAULT_REFRESH_SECONDS);
    }

    public FileSchemaIndex(File indexFile,
                           int refreshSeconds) 
            throws ValidatorException {

        _indexFile = indexFile;
        _refreshSeconds = refreshSeconds;
        loadIndex(true);
    }

    //---------------------------------------------[ Package-visible methods ]

    protected File getIndexFile() {
        return _indexFile;
    }

    //------------------------------------------[ SchemaIndex implementation ]

    public synchronized Set getURISet() 
            throws ValidatorException {

        loadIndex(false);
        return new HashSet(_indexMap.keySet()); // threadsafe copy
    }

    public synchronized String getFilename(String uri) 
            throws ValidatorException {

        loadIndex(false);
        return (String) _indexMap.get(uri);
    }

    public synchronized String putFilename(String uri, 
                                           String filename) 
            throws ValidatorException {

        loadIndex(true);
        String oldFilename = (String) _indexMap.put(uri, filename);
        saveIndex();
        return oldFilename;
    }

    public synchronized boolean removeMapping(String uri)
            throws ValidatorException {
        loadIndex(true);
        if (_indexMap.remove(uri) != null) {
            saveIndex();
            return true;
        } else {
            return false;
        }
    }

    //------------------------------------------------------[ Helper methods ] 

    private void loadIndex(boolean force) 
            throws ValidatorException {

        if (force || System.currentTimeMillis() >= _nextRefreshTime) {

            try {
                _indexMap = loadIndex(_indexFile);
                _LOG.debug("Index loaded, size = " + _indexMap.size());
                if (_refreshSeconds > 0) {
                    _nextRefreshTime = System.currentTimeMillis() 
                            + (1000 * _refreshSeconds);
                }
            } catch (IOException e) {
                throw new ValidatorException(
                        "Error loading schema index file", e);
            }
        }
    }

    private void saveIndex() 
            throws ValidatorException {

        try {
            saveIndex(_indexFile, _indexMap);
            _LOG.debug("Index saved, size = " + _indexMap.size());
        } catch (IOException e) {
            throw new ValidatorException(
                    "Error saving schema index file", e);
        }
    }

    protected static Map loadIndex(File indexFile) 
            throws IOException {

        if (!indexFile.exists()) return new HashMap();
        InputStream in = new FileInputStream(indexFile);
        try {

            Map indexMap = new HashMap();
            BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(in, "UTF-8"));
            String line = reader.readLine();
            int lineNum = 0;

            while (line != null) {

                lineNum++;
                line = line.trim();
                if (line.length() > 0) {

                    String[] parts = line.split(" ");
                    if (parts.length == 2) {
                        indexMap.put(parts[0], parts[1]);
                    } else {
                        _LOG.warn("Skipping line #" + lineNum + " in schema "
                                + "index file (it did not contain a space "
                                + "delimiter as expected)");
                    }
                }
                line = reader.readLine();
            }

            reader.close();
            return indexMap;

        } finally {
            try { in.close(); } catch (Exception e) { }
        }
    }

    protected static void saveIndex(File indexFile, 
                                    Map indexMap) 
            throws IOException {

        OutputStream out = new FileOutputStream(indexFile);
        try {

            PrintWriter writer = new PrintWriter(
                                     new OutputStreamWriter(out, "UTF-8"));
            Iterator iter = indexMap.keySet().iterator();

            while (iter.hasNext()) {

                String uri = (String) iter.next();
                String filename = (String) indexMap.get(uri);
                writer.println(uri + " " + filename);
            }

            writer.close();

        } finally {
            try { out.close(); } catch (Exception e) { }
        }
    }

}
