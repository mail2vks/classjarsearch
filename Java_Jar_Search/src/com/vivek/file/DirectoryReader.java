package com.vivek.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.swt.widgets.Text;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author singh.kr.vivek
 * 
 */
public class DirectoryReader extends Observable
{

    /**
     * Constant to store number of milliseconds in a second
     */
    private static final int MILLISECONDS = 1000;

    private Multimap<String, String> results = new ArrayListMultimap<String, String>();

    private Text statusText;

    /**
     * @return the results
     */
    public Multimap<String, String> getResults()
    {
        return results;
    }

    public void search(FilenameFilter filter, String searchString,
        String directory)
    {
        results.clear();
        File file = new File(directory);
        long startTime = System.currentTimeMillis();
        parse(file, filter, searchString);
        long endTime = System.currentTimeMillis();
        long timeTaken = (endTime - startTime) / MILLISECONDS;
        this.statusText.setText("Time taken " + timeTaken + "s");
        this.statusText.update();
        setChanged();
        notifyObservers(results);
        System.out.println("Time taken " + timeTaken + "s");
    }

    private void parse(File f, FilenameFilter filter,
        String searchString)
    {
        if (f.isFile())
        {
            searchClassInJar(f, searchString);
        }
        else if (f.isDirectory() && !f.isHidden())
        {
            getContent(f, filter, searchString);
        }
    }

    private void getContent(File f, FilenameFilter filter,
        String searchString)
    {
        setStatus(f.getAbsolutePath());
        File[] fileArr = f.listFiles(filter);
        if (fileArr != null)
        {
            for (File fi : fileArr)
            {
                if (fi.isFile())
                {
                    searchClassInJar(fi, searchString);
                }

            }
        }

        File[] dirArr = f.listFiles();
        if (dirArr != null)
        {
            for (File fi : dirArr)
            {
                if (fi.isDirectory())
                {
                    parse(fi, filter, searchString);
                }
            }
        }
    }

    private void searchClassInJar(File f, String searchString)
    {
        setStatus(f.getAbsolutePath());
        String fileName = null;
        try
        {
            if (f.canRead())
            {
                JarFile jarFile = new JarFile(f);
                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements())
                {
                    fileName = entries.nextElement().getName();
                    if (fileName.indexOf(searchString) != -1)
                    {
                        System.out.println("Found "
                                           + searchString
                                           + " as "
                                           + fileName
                                           + " in "
                                           + f.getPath());
                        results.put(fileName, f.getPath());
                    }
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {

        if (args.length < 2)
        {
            System.out.println("Usage:"
                               + " java -jar jarSearch.jar"
                               + " <Location to Search>"
                               + " <Class File Name>");
        }
        else
        {
            DirectoryReader reader = new DirectoryReader();
            reader.search(new JarFilter(), args[1], args[0]);
        }
    }

    public void setStatusText(Text value)
    {
        this.statusText = value;
    }

    private void setStatus(String value)
    {
        this.statusText.setText("Searching ... " + value);
        this.statusText.update();
    }
}
