package com.vivek.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author singh.kr.vivek
 * 
 */
public class DirectoryReader
{

    /**
     * Constant to store number of milliseconds in a second
     */
    private static final int MILLISECONDS = 1000;

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

    private void search(FilenameFilter filter, String searchString,
        String directory)
    {

        File file = new File(directory);

        long startTime = System.currentTimeMillis();

        parse(file, filter, searchString);

        long endTime = System.currentTimeMillis();

        long timeTaken = (endTime - startTime) / MILLISECONDS;
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
                    }
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
