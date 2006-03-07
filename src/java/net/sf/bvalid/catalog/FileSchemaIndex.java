package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;

public class FileSchemaIndex implements SchemaIndex {

    private static Logger _LOG = Logger.getLogger(FileSchemaIndex.class.getName());

    private File _indexFile;

    public FileSchemaIndex(File indexFile) {
        _indexFile = indexFile;
    }

    public synchronized Iterator listURIs() throws ValidatorException {

        try {
            Map map = loadIndex(_indexFile);
            return map.keySet().iterator();
        } catch (IOException e) {
            throw new ValidatorException("Error loading schema index file", e);
        }
    }

    public synchronized String getFilename(String uri) throws ValidatorException {
        return null;
    }

    public void putFilename(String uri, 
                            String filename) throws ValidatorException {

    }

    protected static Map loadIndex(File indexFile) throws IOException {

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
                        map.put(parts[0], parts[1]);
                    } else {
                        _LOG.warn("Skipping line #" + lineNum + " in schema "
                                + "index file (it did not contain a space "
                                + "delimiter as expected)");
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

}