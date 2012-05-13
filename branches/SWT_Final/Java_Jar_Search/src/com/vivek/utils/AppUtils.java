package com.vivek.utils;

import java.io.InputStream;

public class AppUtils
{

    public static InputStream loadFromClassPath(String fileName)
    {
        InputStream is = AppUtils.class.getClassLoader()
            .getResourceAsStream(fileName);

        return is;
    }

}
