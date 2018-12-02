package com.baronkiko.launcherhijack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ServiceMan.Start(context);
    }


}

