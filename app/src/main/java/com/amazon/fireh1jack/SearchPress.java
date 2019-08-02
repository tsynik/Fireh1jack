package com.amazon.fireh1jack;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by andy on 26/07/2017.
 */

public class SearchPress {

    static final String TAG = "*** SearchPress";
    private static long LastActivate = 0;

    public static Intent GetDesiredIntent(Context c)
    {
        PackageManager packageManager = c.getPackageManager();
//        SharedPreferences settings = c.getSharedPreferences("FireH1jack", MODE_PRIVATE);
//        String s = settings.getString("ChosenSearch", "com.google.android.katniss");
//        String name = settings.getString("ChosenSearchName", "com.amazon.fireh1jack.MainActivity");
//        ComponentName componentName = new ComponentName(s, name);
//
//        Intent i = new Intent(Intent.ACTION_MAIN);
//        i.addCategory(Intent.CATEGORY_LAUNCHER);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//        i.setComponent(componentName);

        Intent i = new Intent();
        i.setAction(Intent.ACTION_ASSIST);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        ResolveInfo resolveActivity = packageManager.resolveActivity(i, 0);
        if (resolveActivity != null) {
            Log.d(TAG, "Search Intent: " + resolveActivity.activityInfo.packageName + "/" + resolveActivity.activityInfo.name);
            i.setComponent(new ComponentName(resolveActivity.activityInfo.packageName, resolveActivity.activityInfo.name));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED|Intent.FLAG_ACTIVITY_NO_HISTORY|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		}
        return i;
    }

    public static void Perform(Context c)
    {
        // Simple debounce
        long time = System.currentTimeMillis();
        if (time - LastActivate < 200)
            return;
        LastActivate = time;

        Intent i = GetDesiredIntent(c);
        PendingIntent pendingIntent = PendingIntent.getActivity(c, 0, i, 0);
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
