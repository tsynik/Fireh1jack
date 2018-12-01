package com.baronkiko.launcherhijack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

public class Utilities {
	
	/*
	 * Get all installed application on mobile and return a list
	 * @param	c	 Context of application
	 * @return	list of installed applications
	 */
	public static List<ResolveInfo> getInstalledApplication(Context c, boolean launchers, boolean systemApps) {
        List<ResolveInfo> results;


        PackageManager pm = c.getPackageManager();
        Intent main=new Intent(Intent.ACTION_MAIN, null);

        if (launchers)
            main.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> launchables= pm.queryIntentActivities(main, 0);


        Collections.sort(launchables, new ResolveInfo.DisplayNameComparator(pm));

        if (!systemApps)
            return launchables;

        results = new ArrayList<>();
        for (int n = 0; n < launchables.size();n++)
            if((launchables.get(n).activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) !=1)
                results.add(launchables.get(n));


        return results;
	}
	
	/*
	 * Launch an application
	 * @param	c	Context of application
	 * @param	pm	the related package manager of the context
	 * @param	pkgName	Name of the package to run
	 */
	public static boolean launchApp(Context c, PackageManager pm, String pkgName) {
		// query the intent for lauching 
		Intent intent = pm.getLaunchIntentForPackage(pkgName);
		// if intent is available
		if(intent != null) {
			try {
				// launch application
				c.startActivity(intent);
				// if succeed
				return true;
			
			// if fail
			} catch(ActivityNotFoundException ex) {
				// quick message notification
				Toast toast = Toast.makeText(c, "Application Not Found", Toast.LENGTH_LONG);
				// display message
				toast.show();
			}
		}
		// by default, fail to launch
		return false;
	}
}
