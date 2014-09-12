package com.smartfoo.utils;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by Pv on 9/12/2014.
 */
public class FooUtils {

    private FooUtils() {
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    public static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }
}
