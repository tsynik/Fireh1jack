package com.baronkiko.launcherhijack;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class HomeButtonService extends Service {
    private LinearLayout layout;
    private WindowManager wm;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (SettingsMan.GetSettings().OverlayApplicationDetection)
        {
            layout = new LinearLayout(getApplicationContext())
            {
                //home or recent button
                public void onCloseSystemDialogs(String reason)
                {
                    Log.d("HomeButtonService", "Close System Dialog: " + reason);
                    if (reason.contains("homekey"))
                        HomePress.Perform(getApplicationContext());
                }

                @Override
                public boolean dispatchKeyEvent(KeyEvent event)
                {
                    Log.d("HomeButtonService", "Dispatch Key Event");
                    return false;
                }
            };

            layout.setFocusable(false);

            wm = (WindowManager) getSystemService(WINDOW_SERVICE);

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    0,
                    0,
                    WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_FULLSCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            wm.addView(layout, params);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");

        wm.removeView(layout);

        ServiceMan.Stop(getApplicationContext());
        ServiceMan.StartSlow(getApplicationContext());
    }
}