package com.baronkiko.launcherhijack;

import android.accessibilityservice.AccessibilityService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import static android.os.ParcelFileDescriptor.MODE_READ_ONLY;
import static android.os.ParcelFileDescriptor.MODE_WORLD_READABLE;

/**
 * Created by andy on 26/07/2017.
 */

public class HomePress {
    public static Intent GetDesiredIntent(Context c)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            SharedPreferences settings = c.getSharedPreferences("LauncherHijack", MODE_READ_ONLY);
        else
            SharedPreferences settings = c.getSharedPreferences("LauncherHijack", MODE_WORLD_READABLE);
        String s = settings.getString("ChosenLauncher", "com.teslacoilsw.launcher");
        return new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(s).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    public static void Perform(Context c)
    {
        Intent i = GetDesiredIntent(c);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, i, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
