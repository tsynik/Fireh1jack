package com.baronkiko.launcherhijack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class HomeButtonService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("A", "X");
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
        ServiceMan.Stop(getApplicationContext());
        ServiceMan.StartSlow(getApplicationContext());
    }
}