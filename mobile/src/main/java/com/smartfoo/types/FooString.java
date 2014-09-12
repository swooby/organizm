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


    public static char toChar(boolean value)
    {
        return (value) ? '1' : '0';
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

    /**
     * Identical to {@link #repr}, but grammatically intended for Strings.
     * @param value
     * @return "null", or '\"' + value.toString + '\"', or value.toString()
     */
    public static String quote(Object value)
    {
        return repr(value, false);
    }

    /**
     * Identical to {@link #quote}, but grammatically intended for Objects.
     * @param value
     * @return "null", or '\"' + value.toString + '\"', or value.toString()
     */
    public static String repr(Object value)
    {
        return repr(value, false);
    }

    /**
     * @param value
     * @param typeOnly
     * @return "null", or '\"' + value.toString + '\"', or value.toString(), or getShortClassName(value)
     */
    public static String repr(Object value, boolean typeOnly)
    {
        return (value == null) ? "null" : (value instanceof String) ? ('\"' + value.toString() + '\"') //
                : ((typeOnly) ? getShortClassName(value) : value.toString());
    }

    public static String toString(Object[] items)
    {
        StringBuffer sb = new StringBuffer();

        if (items == null)
        {
            sb.append("null");
        }
        else
        {
            sb.append('[');
            for (int i = 0; i < items.length; i++)
            {
                Object item = items[i];
                if (i != 0)
                {
                    sb.append(", ");
                }
                sb.append(quote(item));
            }
            sb.append(']');
        }
        return sb.toString();
    }

    public static String capitalize(String s)
    {
        if (s == null || s.length() == 0)
        {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first))
        {
            return s;
        }
        else
        {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
