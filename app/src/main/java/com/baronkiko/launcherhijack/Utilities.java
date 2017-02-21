package com.baronkiko.launcherhijack;

import java.util.ArrayList;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

public class Utilities {
	
	/*
	 * Get all installed application on mobile and return a list
	 * @param	c	 Context of application
	 * @param   type 0 = all, 1 = user apps, 2 = launchers
	 * @return	list of installed applications
	 */
	public static List<ApplicationInfo> getInstalledApplication(Context c, int type) {
		List<ApplicationInfo> list, results;

        if (type < 2) // Get all apps
          list = c.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        else // Get launchers
        {
            list = new ArrayList<>();
            PackageManager pm = c.getPackageManager();
            Intent i = new Intent(Intent.ACTION_MAIN);
            i.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> lst = pm.queryIntentActivities(i, 0);
            for (ResolveInfo resolveInfo : lst)
                list.add(resolveInfo.activityInfo.applicationInfo);
        }

        if (type == 0)
            return list;

        // Filter system apps
        results = new ArrayList<ApplicationInfo>();
        for (int n = 0; n < list.size();n++)
            if((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) !=1)
                results.add(list.get(n));
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
