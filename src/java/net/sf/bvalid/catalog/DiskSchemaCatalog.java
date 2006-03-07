package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;

public class DiskSchemaCatalog implements SchemaCatalog {

    private static Logger _LOG = Logger.getLogger(DiskSchemaCatalog.class.getName());

    private static final String _INDEX_FILENAME = "index.txt";

    /** Where schemas and the index file are stored */
    private File _storageDir;

    /** Memory storage of uri-to-file mapping */
    private Map _indexMap;

    public DiskSchemaCatalog(File storageDir) throws ValidatorException {

        _storageDir = storageDir;

        if (!_storageDir.exists()) {
            _LOG.info("Schema storage directory does not yet exist; creating...");
            _storageDir.mkdirs();
            if (!_storageDir.exists()) {
                throw new ValidatorException("Unable to create directory: " 
                        + _storageDir);
            }
        } else if (!_storageDir.isDirectory()) {
            throw new ValidatorException("Not a directory: " + _storageDir);
        }

        loadIndex(); 
    }


    public synchronized boolean contains(String uri) {
        return _indexMap.containsKey(uri);
    }

    public synchronized InputStream get(String uri) throws ValidatorException {
        if (_indexMap.containsKey(uri)) {
            try {
                _LOG.debug("Schema " + uri + " found in catalog; reading...");
                return new FileInputStream((File) _indexMap.get(uri));
            } catch (Throwable th) {
                throw new ValidatorException("Error reading file in catalog", th);
            }
        } else {
            return null;
        }
    }

    public synchronized void put(String uri, 
                                 InputStream in) throws ValidatorException {

        // Get rid of previous if needed 
        if (removeMappingAndFile(uri)) {
            _LOG.info("Replacing schema in catalog: " + uri);
        } else {
            _LOG.info("Adding new schema to catalog: " + uri);
        }

        // Make sure the new file is unique
        long num = System.currentTimeMillis();
        File file = new File(_storageDir, num + "");
        while (file.exists()) {
            num++;
            file = new File(_storageDir, num + "");
        }

        // Dump the stream to the new file
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[4096];
            int len;
            while ( ( len = in.read( buf ) ) > 0 ) {
                out.write( buf, 0, len );
            }
            _LOG.debug("Wrote content for schema (" + uri + ") to file: " 
                    + file.getPath());
        } catch (Throwable th) {
            throw new ValidatorException("Unable to write schema content to "
                    + "file: " + file.getPath(), th);
        } finally {
            try { in.close(); } catch (Exception e) { }
            if (out != null) try { out.close(); } catch (Exception e) { }
        }

        // Finally, add it to the memory index and save the index to disk
        _indexMap.put(uri, file);
        saveIndex();
    }

    public synchronized void remove(String uri) throws ValidatorException {
        if (removeMappingAndFile(uri)) {
            _LOG.debug("Removed schema mapping and file from catalog: " + uri);
            saveIndex();
        } else {
            _LOG.warn("Non-existing schema not removed: " + uri);
        }
    }

    /**
     * If it exists in the memory map, remove it from the memory map,
     * attempt to delete the underlying file, then return true.
     * Otherwise, return false.
     */
    private synchronized boolean removeMappingAndFile(String uri) {

        if (_indexMap.containsKey(uri)) {
            File oldFile = (File) _indexMap.remove(uri);
            boolean deleted = oldFile.delete();
            if (!deleted) {
                _LOG.warn("Unable to delete the file (" + oldFile.getPath()
                        + ") that contains the schema (" + uri + ")... will "
                        + "mark it for deletion later");
                oldFile.deleteOnExit();
            }
            return true;
        } else {
            return false;
        }
    }

    private synchronized void loadIndex() throws ValidatorException {
        try {
            _indexMap = loadIndex(_storageDir);
            _LOG.debug("Index loaded with " + _indexMap.size() + " entries");
        } catch (Throwable th) {
            throw new ValidatorException("Unable to load schema index", th);
        }
    }

    private synchronized void saveIndex() throws ValidatorException {
        try {
             saveIndex(_storageDir, _indexMap);
            _LOG.debug("Index saved with " + _indexMap.size() + " entries");
        } catch (Throwable th) {
            throw new ValidatorException("Unable to save schema index", th);
        }
    }

    protected static Map loadIndex(File storageDir) throws IOException {

        File indexFile = new File(storageDir, _INDEX_FILENAME);
        InputStream in = new FileInputStream(indexFile);
        try {
            Map map = new HashMap();
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
                        String uri = parts[0];
                        File file = new File(storageDir, parts[1]);
                        if (!file.exists()) {
                            _LOG.warn("Skipping line #" + lineNum + " in schema "
                                    + "index (the file does not exist)");
                        } else {
                            map.put(parts[0], parts[1]);
                        }
                    } else {
                        _LOG.warn("Skipping line #" + lineNum + " in schema "
                                + "index (it did not contain a space delimiter "
                                + "as expected)");
                    }
                }
                line = reader.readLine();
            }
            reader.close();
            return map;
        } finally {
            try { in.close(); } catch (Exception e) { }
        }
    }

    protected static void saveIndex(File storageDir, 
                                    Map map) throws IOException {

        File indexFile = new File(storageDir, _INDEX_FILENAME);
        OutputStream out = new FileOutputStream(indexFile);
        try {
            PrintWriter writer = new PrintWriter(
                                     new OutputStreamWriter(out, "UTF-8"));
            Iterator iter = map.keySet().iterator();
            while (iter.hasNext()) {
                String uri = (String) iter.next();
                File file = (File) map.get(uri);
                writer.println(uri + " " + file.getName());
            }
            writer.close();
        } finally {
            try { out.close(); } catch (Exception e) { }
        }
    }

}