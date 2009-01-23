package com.vivek.file;

import java.io.File;
import java.io.FilenameFilter;

public class JarFilter implements FilenameFilter
{

    public boolean accept(File dir, String name)
    {

        if (name.endsWith(".jar"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

}
