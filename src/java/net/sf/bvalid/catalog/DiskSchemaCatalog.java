package net.sf.bvalid.catalog;

import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;

import net.sf.bvalid.ValidatorException;

/**
 * A <code>SchemaCatalog</code> that uses a given <code>SchemaIndex</code>
 * in conjunction with files on disk.
 * 
 * @author cwilper@cs.cornell.edu
 */
public class DiskSchemaCatalog implements SchemaCatalog {

    private static Logger _LOG = 
            Logger.getLogger(DiskSchemaCatalog.class.getName());

    private SchemaIndex _index;
    private File _storageDir;

    public DiskSchemaCatalog(SchemaIndex index,
                             File storageDir)
            throws ValidatorException {

        _index = index;
        _storageDir = storageDir;

        pruneStorageDir();
    }

    private void pruneStorageDir() 
            throws ValidatorException {

        Set keepNames = new HashSet();

        // keep the index itself, if stored in same dir
        if (_index instanceof FileSchemaIndex) {
            File indexFile = ((FileSchemaIndex) _index).getIndexFile();
            keepNames.add(indexFile.getName());
        }

        // keep all files currently referenced by the index
        Iterator iter = _index.getURISet().iterator();
        while (iter.hasNext()) {
            String uri = (String) iter.next();
            keepNames.add(_index.getFilename(uri));
        }

        int pruneCount = pruneDir(_storageDir, keepNames);
        _LOG.info("Pruned " + pruneCount + " unused file(s) from "
                + "schema storage directory");
    }

    public synchronized boolean contains(String uri) 
            throws ValidatorException {

        return (_index.getFilename(uri) != null);
    }

    public synchronized Iterator listURIs() 
            throws ValidatorException {

        return _index.getURISet().iterator();
    }

    public synchronized InputStream get(String uri) 
            throws ValidatorException {

        String filename = _index.getFilename(uri);

        if (filename == null) {
            return null;
        } else {
            try {
                return new FileInputStream(new File(_storageDir, filename));
            } catch (IOException e) {
                throw new ValidatorException("Error reading schema from disk", e);
            }
        }
    }

    public synchronized void put(String uri, InputStream in) throws ValidatorException {

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

        // Add/replace the mapping in the index
        String oldFilename = _index.putFilename(uri, file.getName());

        // Get rid of previous file if needed
        if (oldFilename != null) {
            removeSchemaFile(oldFilename);
        }
    }

    public synchronized void remove(String uri) throws ValidatorException {

        String filename = _index.getFilename(uri);
        
        if (filename == null) {
            _LOG.warn("Cannot remove schema; not in index: " + uri);
        } else {
            removeSchemaFile(filename);
            _index.removeMapping(uri);
        }
    }

    private void removeSchemaFile(String filename) {

        File file = new File(filename);

        boolean deleted = file.delete();
        if (!deleted) {
            if (file.exists()) {
                _LOG.warn("Cannot remove schema file: " + file.getPath());
            } else {
                _LOG.warn("Schema file already deleted: " + file.getPath());
            }
        }
    }

    private static int pruneDir(File dir, Set keepNames) {

        int count = 0;
        try {
            File[] files = dir.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String name = file.getName();
                if (file.isFile() && !keepNames.contains(name)) {
                    boolean deleted = file.delete();
                    String msgSuffix = " unused file from storage directory: "
                            + name;
                    if (deleted) {
                        _LOG.info("Pruned" + msgSuffix);
                        count++;
                    } else {
                        _LOG.warn("Unable to prune" + msgSuffix);
                    }
                }
            }
        } catch (Throwable th) {
            _LOG.warn("Error pruning schema storage directory", th);
        }
        return count;
    }

}
