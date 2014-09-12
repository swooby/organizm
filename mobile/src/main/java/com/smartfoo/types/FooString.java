package com.smartfoo.types;

import java.io.UnsupportedEncodingException;
import java.util.Vector;

/**
 * Created by Pv on 9/11/2014.
 */
public class FooString {

    private FooString()
    {
    }

    /**
     * Tests if a String value is null or empty.
     *
     * @param value
     *            the String value to test
     * @return true if the String is null, zero length, or ""
     */
    public static boolean isNullOrEmpty(String value)
    {
        return (value == null || value.length() == 0 || value == "");
    }

    public static String getShortClassName(String className)
    {
        if (isNullOrEmpty(className))
        {
            return "null";
        }
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static String getShortClassName(Object o)
    {
        Class c = (o == null) ? null : o.getClass();
        return getShortClassName(c);
    }

    public static String getShortClassName(Class c)
    {
        String className = (c == null) ? null : c.getName();
        return getShortClassName(className);
    }

    public static String getMethodName(String methodName)
    {
        if (methodName == null)
        {
            methodName = "()";
        }
        if (methodName.compareTo("()") != 0)
        {
            methodName = "." + methodName;
        }
        return methodName;
    }

    public static String getShortClassAndMethodName(Object o, String methodName)
    {
        return getShortClassName(o) + getMethodName(methodName);
    }
}
