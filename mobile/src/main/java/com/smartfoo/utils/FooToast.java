package com.smartfoo.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Pv on 9/11/2014.
 */
public class FooToast {

    private FooToast() {
    }

    public static void showLong(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
