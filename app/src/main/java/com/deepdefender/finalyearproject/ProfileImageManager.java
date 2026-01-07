package com.deepdefender.finalyearproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

public class ProfileImageManager {

    private static final String PREF = "profile";
    private static final String KEY = "profile_image";

    public static void save(Context context, Uri uri) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY, uri.toString()).apply();
    }

    public static Uri load(Context context) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String uri = prefs.getString(KEY, null);
        return uri != null ? Uri.parse(uri) : null;
    }
}
