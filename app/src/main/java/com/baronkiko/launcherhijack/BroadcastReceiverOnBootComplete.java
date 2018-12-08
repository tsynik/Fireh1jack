package com.baronkiko.launcherhijack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        MainActivity.SetContext(context);
        ServiceMan.Start(context);
    }


}

