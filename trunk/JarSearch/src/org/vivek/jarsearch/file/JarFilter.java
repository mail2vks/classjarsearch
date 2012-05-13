package org.vivek.jarsearch.file;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author singh.kr.vivek
 * 
 * Implements FilenameFilter to include .jar and .zip files
 * 
 * @author Vivek_Singh
 */
public class JarFilter implements FilenameFilter
{

    @Override
    public boolean accept(File dir, String name)
    {

        if (name.endsWith(".jar") || name.endsWith(".zip"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
