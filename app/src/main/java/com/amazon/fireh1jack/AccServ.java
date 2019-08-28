package com.amazon.fireh1jack;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.jaredrummler.android.device.DeviceName;


public class AccServ extends AccessibilityService {

    static final String TAG = "*** AccServ";
    static boolean HomePressCanceled = false;
    static boolean ActivateSearch = false;
    static long lastHomeDown;
    static long lastHomeDuration;
    static long lastPlayDown;
    static long lastPlayDuration;
    static HomeWatcher homeWatcher;
    static String lastApp, lastClass;
    private static SettingsMan.SettingStore settings;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    
        settings = SettingsMan.GetSettings(); // for cLauncher
        lastApp = (String) event.getPackageName();
        lastClass = (String) event.getClassName();

//        if (!settings.ApplicationOpenDetection)
//            return;
//
//        CharSequence packageName = event.getPackageName();
//        if (
//            packageName.equals("com.amazon.firelauncher") || // ### TAB
//            packageName.equals("com.amazon.tv.launcher") || // ### FTV
//            packageName.equals("com.google.android.leanbacklauncher") && // ### ATV
//            !packageName.equals(settings.cLauncher) // com.amazon.tv.launcher settings workaround
//        ) {
//            Log.v(TAG, "Do HOME (ApplicationOpenDetection). onAccessibilityEvent: " + event + ", package: " + packageName + ", cLauncher: " + settings.cLauncher);
//            HomePress.Perform(getApplicationContext());
//        }
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }

    @Override
    public boolean onKeyEvent(KeyEvent event)
    {
        if (!settings.HardwareDetection)
            return false;

        // Log.v(TAG, "### KeyEvent ### " + event.toString());

        int action = event.getAction();
        switch (event.getKeyCode())
        {
            case KeyEvent.KEYCODE_HOME:

//                if (action == KeyEvent.ACTION_DOWN)
//                    lastHomeDown = System.currentTimeMillis();
//                else if (action == KeyEvent.ACTION_UP) {
//                    lastHomeDuration = System.currentTimeMillis() - lastHomeDown;
//                    HomePressCanceled = false;
//                    ActivateSearch = false;
//                }
//                // Log.v(TAG, "HOME lastDuration = " + lastHomeDuration);
//                if (action == KeyEvent.ACTION_UP && lastHomeDuration > 500) { // long press
//                    HomePressCanceled = true; // LONG PRESS for HUD
//                    ActivateSearch = true; // LONG PRESS for SEARCH
//                    return true; // Override default handling
//                }
                if (action == KeyEvent.ACTION_UP)
                    HomePressCanceled = false;
                else if (action == KeyEvent.ACTION_DOWN && !HomePressCanceled) {
                    // Log.v(TAG, "### HOME ###");
                    HomePress.Perform(getApplicationContext());
                    if (settings.UseGSearch)
                        MainActivity.passOOBE();
                    return true; // Override default handling
                }
                return false;

            case KeyEvent.KEYCODE_MENU: // 82
                if (settings.MenuButtonOverride && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    HomePressCanceled = true;
                }
                return false;

//            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE: // 84
//        		if (!settings.UseGSearch)
//            		return false;
//                if (action == KeyEvent.ACTION_DOWN)
//                    lastPlayDown = System.currentTimeMillis();
//                else if (action == KeyEvent.ACTION_UP) {
//                    lastPlayDuration = System.currentTimeMillis() - lastPlayDown;
//                    ActivateSearch = false;
//                    return true; // Override default handling
//                }
//                // Log.v(TAG, "MEDIA_PLAY_PAUSE lastPlayDown = " + lastPlayDuration + " ActivateSearch = " + ActivateSearch);
//                if ((action == KeyEvent.ACTION_DOWN && lastPlayDuration > 500) || ActivateSearch) {
//                    // Log.v(TAG, "### ASSIST ###");
//                    SearchPress.Perform(getApplicationContext());
//                    HomePressCanceled = false;
//                    return true; // Override default handling
//                }
//                return false;
        }
        return false;
    }
    
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        MainActivity.SetContext(getApplicationContext());

        settings = SettingsMan.GetSettings();

        lastClass = "";
        lastApp = "";

        homeWatcher = new HomeWatcher(getApplicationContext());
        homeWatcher.setOnHomePressedListener(new OnHomePressedListener()
        {
            @Override
            public void onHomePressed()
            {
                if (settings.BroadcastRecieverDetection && !HomePressCanceled &&
                   (!settings.RecentAppOverride | !(lastApp.equals("com.android.systemui") &&
                   lastClass.equals("com.android.systemui.recents.RecentsActivity"))))
                {
                    Log.d(TAG, "Do NEW HOME. LastApp: " + lastApp + "  LastClass: " + lastClass);
                    HomePress.Perform(getApplicationContext());
                }
                // OOBE hack
                if (settings.UseGSearch)
                    MainActivity.passOOBE();
            }

            @Override
            public void onHomeLongPressed()
            {
                    Log.d(TAG, "Do LONG HOME");
                    // performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
            }

            @Override
            public void onRecentAppPressed()
            {
                    Log.d(TAG, "Do RECENTS");
                    // performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
            }
            
            @Override
            public void onSearchPressed()
            {
                    Log.d(TAG, "Do SEARCH");
                    SearchPress.Perform(getApplicationContext());
            }
        });
        homeWatcher.startWatch();

        Log.v(TAG, "FireTVjack Service Started on " + DeviceName.getDeviceName());
        HomePress.Perform(getApplicationContext());
    }
}