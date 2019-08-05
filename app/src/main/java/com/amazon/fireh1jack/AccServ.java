package com.amazon.fireh1jack;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

import com.jaredrummler.android.device.DeviceName;


public class AccServ extends AccessibilityService {

    static final String TAG = "*** AccServ";
    static boolean HomePressCanceled = false;
    static HomeWatcher homeWatcher;
    static String lastApp, lastClass;
    private static SettingsMan.SettingStore settings;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    
        settings = SettingsMan.GetSettings(); // for cLaunch
        lastApp = (String) event.getPackageName();
        lastClass = (String) event.getClassName();

        if (!settings.ApplicationOpenDetection)
            return;

        CharSequence packageName = event.getPackageName();
        if (
            packageName.equals("com.amazon.firelauncher") || // ### TAB
            packageName.equals("com.amazon.tv.launcher") || // ### FTV
            packageName.equals("com.google.android.leanbacklauncher") && // ### ATV
            !packageName.equals(settings.cLaunch)
        ) {
            Log.v(TAG, "Do HOME (ApplicationOpenDetection). onAccessibilityEvent: " + event + ", package: " + packageName + ", cLaunch: " + settings.cLaunch);
            HomePress.Perform(getApplicationContext());
        }
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

        switch (event.getKeyCode())
        {
            case KeyEvent.KEYCODE_HOME:
                int action = event.getAction();
                // Log.v(TAG, "KEYCODE_HOME HomePressCanceled = " + HomePressCanceled);

                if (action == KeyEvent.ACTION_UP) {
                    // Log.v(TAG, "ACTION_UP HomePressCanceled = " + HomePressCanceled);
                    HomePressCanceled = false;
                }
                else if (action == KeyEvent.ACTION_DOWN && !HomePressCanceled)
                {
                    // Log.v(TAG, "ACTION_DN HomePressCanceled = false, onKeyEvent:" + event);
                    HomePress.Perform(getApplicationContext());
                    return true;
                }
                return false; // false

            case KeyEvent.KEYCODE_MENU:
                if (settings.MenuButtonOverride && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    // Log.v(TAG, "KEYCODE_MENU set HomePressCanceled = " + HomePressCanceled);
                    HomePressCanceled = true;
                	return false;
                }

            case KeyEvent.KEYCODE_SEARCH:
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    // Log.v(TAG, "KEYCODE_SEARCH Press (HardwareDetection). onKeyEvent:" + event);
                    SearchPress.Perform(getApplicationContext());
                    return true;
                }
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
                   (!settings.RecentAppOverride | !(lastApp.equals("com.android.systemui") && lastClass.equals("com.android.systemui.recents.RecentsActivity"))))
                {
                    Log.d(TAG, "Do NEW HOME. LastApp: " + lastApp + "  LastClass: " + lastClass);
                    HomePress.Perform(getApplicationContext());
                }
            }

            @Override
            public void onHomeLongPressed()
            {
                    Log.d(TAG, "Do LONG HOME");
            }

            @Override
            public void onRecentAppPressed()
            {
                    Log.d(TAG, "Do RECENTS");
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