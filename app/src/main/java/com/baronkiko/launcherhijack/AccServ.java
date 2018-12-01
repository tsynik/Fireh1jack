package com.baronkiko.launcherhijack;

import android.accessibilityservice.AccessibilityService;
import android.app.UiModeManager;
import android.content.res.Configuration;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;


public class AccServ extends AccessibilityService {

    static final String TAG = "AccServ";
    static boolean HomePressCanceled = false;
    static boolean RunningOnTV = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!RunningOnTV)
        {
            CharSequence packageName = event.getPackageName();
            if (packageName.equals("com.amazon.firelauncher"))
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
        switch (event.getKeyCode())
        {
            case KeyEvent.KEYCODE_HOME:
                int action = event.getAction();

                if (action == KeyEvent.ACTION_UP)
                    HomePressCanceled = false;
                else if (action == KeyEvent.ACTION_DOWN && !HomePressCanceled)
                {
                    HomePress.Perform(getApplicationContext());
                    return true;
                }

                return false;

            case KeyEvent.KEYCODE_MENU:
                if (RunningOnTV && event.getAction() == KeyEvent.ACTION_DOWN)
                    HomePressCanceled = true;
                return false;
        }
        return false;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        UiModeManager uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        RunningOnTV = (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        Log.v(TAG, "Launcher Hijack Service Started");
        HomePress.Perform(getApplicationContext());
    }

}