package org.vivek.jarsearch.file;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;

/**
 * @author singh.kr.vivek
 *
 * This class has logic to parse directories using FileNameFilter
 * implementation. This is also responsible for setting status text at the
 * bottom of UI.
 *
 */
public class DirectoryReader {

    /**
     * Constant to store number of milliseconds in a second
     */
    private static final int MILLISECONDS = 1000;
    /**
     * Guava Multimap which is used for storing results. This internally stores
     * results with Key as Object and Value within a Collection.
     * http://docs.guava-libraries.googlecode.com/git-history/v12.0/javadoc/com/google/common/collect/Multimap.html
     */
    private Multimap<String, String> results = ArrayListMultimap.create();
    /**
     * Default JDK Logger Instance.
     */
    private static final Logger log = Logger.getLogger(DirectoryReader.class.getCanonicalName());
    /**
     * Local reference to JLabel.
     */
    private JLabel jLabelRef;

    /**
     * @return MultiMap which contains search results
     */
    public Multimap<String, String> getResults() {
        return results;
    }

    /**
     * This method is invoked when Search button is clicked in UI.
     *
     * @param filter FilenameFilter instance.
     * @param searchString Partial or complete class name provided
     * @param directory Directory in which to perform search operations.
     * @param statusLabel JLabel reference to be updated when search is
     * performed.
     */
    public void search(FilenameFilter filter, String searchString,
            String directory, JLabel statusLabel) {
        results.clear();
        jLabelRef = statusLabel;
        File file = new File(directory);
        long startTime = System.currentTimeMillis();
        parse(file, filter, searchString);
        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / MILLISECONDS;
        if (results.isEmpty()) {
            this.setStatusText("In " + timeTaken + " sec no match found");
        } else {
            this.setStatusText("In " + timeTaken + " sec some matches found");
        }
    }

    /**
     * Logic : 1.Identify file type 2. If type is 'File' search for file with
     * name same as searchString and add to results MultiMap. 3. If type is
     * 'Directory' get content using filter and search for searchString.
     *
     * @param f File Handle
     * @param filter FilenameFilter instance.
     * @param searchString Partial or complete class name provided
     */
    private void parse(File f, FilenameFilter filter,
            String searchString) {
        if (searchString.length() > 0) {
            if (f.isFile()) {
                searchStringInFile(f, searchString);
            } else if ((f.isDirectory() && !f.isHidden())
                    || f.isAbsolute()) {
                getContent(f, filter, searchString);
            }
        }
    }

    /**
     * Logic: 1. List all files using Filter and for each result perform search
     * with searchString 2. For all directories call parse to get files and
     * directories
     *
     * @param f File Handle
     * @param filter FilenameFilter instance.
     * @param searchString Partial or complete class name provided
     */
    private void getContent(File f, FilenameFilter filter,
            String searchString) {
        setStatus(f.getAbsolutePath());
        File[] fileArr = f.listFiles(filter);
        if (fileArr != null) {
            for (File fi : fileArr) {
                if (fi.isFile()) {
                    searchStringInFile(fi, searchString);
                }

            }
        }

        File[] dirArr = f.listFiles();
        if (dirArr != null) {
            for (File fi : dirArr) {
                if (fi.isDirectory()) {
                    parse(fi, filter, searchString);
                }
            }
        }
    }

    /**
     * Logic: 1. List all files in Jar and search for searchString
     *
     * @param f File Handle
     * @param searchString Partial or complete class name provided
     */
    private void searchStringInFile(File f, String searchString) {
        searchString = searchString.replaceAll("\\.", "/");
        setStatus(f.getAbsolutePath());
        try {
            if (f.canRead()) {
                JarFile jarFile = new JarFile(f);
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    String fileName = entries.nextElement().getName();
                    // No inner classes
                    if (fileName.indexOf("$") == -1
                            && fileName.toLowerCase().indexOf(
                            searchString.toLowerCase()) != -1) {
                        log.log(Level.INFO, "Found {0} as {1} in {2}", new Object[]{searchString, fileName, f.getPath()});
                        results.put(fileName, f.getPath());
                    }
                }

            }
        } catch (IOException e) {
            log.log(Level.SEVERE, "Error while searching :{0}", e.getMessage());
        }
    }

    /**
     * Method is used to set Status Label in UI.
     *
     * @param value
     */
    public void setStatusText(String value) {
        jLabelRef.setText(value);
        jLabelRef.paintImmediately(jLabelRef.getVisibleRect());
    }

    /**
     * Invokes setStatusText with 'Searching in ' prefixed to input
     *
     * @param value
     */
    private void setStatus(String value) {
        this.setStatusText("Searching in " + value);
    }
}
