package com.baronkiko.launcherhijack;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class AccServ extends AccessibilityService {

    static final String TAG = "AccServ";

    public Intent GetDesiredIntent()
    {
        SharedPreferences settings = getSharedPreferences("LauncherHijack", MODE_WORLD_READABLE);
        String s = settings.getString("ChosenLauncher", "com.teslacoilsw.launcher");
        return new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setPackage(s).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    }

    private void PerformHomePress()
    {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Log.wtf(TAG,e);
        }
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        startActivity(GetDesiredIntent());
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(event.getPackageName().equals("com.amazon.firelauncher"))
            PerformHomePress();
    }

    @Override
    public void onInterrupt() {
        Log.v(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.packageNames = new String[]{"com.amazon.firelauncher"};
        setServiceInfo(info);
        PerformHomePress();
    }

}