package com.smartfoo.logging;

import android.util.Log;

import com.smartfoo.types.FooString;

/**
 * Created by Pv on 9/11/2014.
 */
public class FooLog {

    private FooLog() {
    }

    public static String TAG(Object o)
    {
        return TAG((o == null) ? null : o.getClass());
    }

    public static String TAG(Class c)
    {
        return FooString.getShortClassName(c);
    }

    public static void info(String TAG, String format, Object... args) {
        Log.i(TAG, String.format(format, args));
    }
}
