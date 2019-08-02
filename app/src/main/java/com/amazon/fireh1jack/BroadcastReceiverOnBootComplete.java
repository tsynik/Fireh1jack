package com.amazon.fireh1jack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import static android.content.Context.MODE_PRIVATE;

public class BroadcastReceiverOnBootComplete extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        switch (intent.getAction())
        {
            case Intent.ACTION_PACKAGE_REMOVED:
                SharedPreferences settings = context.getSharedPreferences("FireH1jack", MODE_PRIVATE);
                String s = settings.getString("ChosenLauncher", "");

                if (intent.getData().getSchemeSpecificPart().equals(s))
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("ChosenLauncher", "com.amazon.fireh1jack");
                    editor.putString("ChosenLauncherName", "com.amazon.fireh1jack.MainActivity");
                    editor.commit(); // Commit the edits!
                }
                break;

            case Intent.ACTION_BOOT_COMPLETED:
                Log.d("*** FireTVjack", "BOOT_COMPLETED");
                MainActivity.SetContext(context);
                MainActivity.SetLanguage();
                ServiceMan.Start(context);
                break;
        }
    }
}
