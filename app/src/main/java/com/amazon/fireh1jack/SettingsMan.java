package com.amazon.fireh1jack;

import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.widget.Toast;
import com.amazon.fireh1jack.R;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.UI_MODE_SERVICE;

public class SettingsMan
{
    private static SettingStore settingStore;

    protected static class SettingStore
    {
        public static final String TAG = "*** SettingsMan";
        static boolean HardwareDetection, ApplicationOpenDetection, BroadcastRecieverDetection, OverlayApplicationDetection, MenuButtonOverride, RecentAppOverride, SetLanguage, UseGSearch, RunningOnTV;
        static String uLocale;

        private static Context c;
        private static SharedPreferences settings;

        public SettingStore()
        {
            c = MainActivity.GetContext();
            settings = c.getSharedPreferences("FireH1jack", MODE_PRIVATE);

            RunningOnTV = c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);

            if (!settings.getBoolean("defaultsLoaded", false))
            {
                LoadDefaults();
                return;
            }

            HardwareDetection = settings.getBoolean("HardwareDetection", false);
            ApplicationOpenDetection = settings.getBoolean("ApplicationOpenDetection", false);
            BroadcastRecieverDetection = settings.getBoolean("BroadcastRecieverDetection", false);
            OverlayApplicationDetection = settings.getBoolean("OverlayApplicationDetection", false);
            MenuButtonOverride = settings.getBoolean("MenuButtonOverride", false);
            RecentAppOverride = settings.getBoolean("RecentAppOverride", false);
            SetLanguage = settings.getBoolean("SetLanguage", false);
            uLocale = settings.getString("uLocale", "RU");
            UseGSearch = settings.getBoolean("UseGSearch", false);
        }

        public static void LoadDefaults()
        {
            UiModeManager uiModeManager = (UiModeManager) c.getSystemService(UI_MODE_SERVICE);
            boolean tv = uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION;
            boolean android7 = Build.VERSION.SDK_INT >= 25;

            HardwareDetection = MenuButtonOverride = tv; // Enable hardware detection on TV's
            OverlayApplicationDetection = !android7; // Disabled for new fire tablets and tv
            RecentAppOverride = android7 & !tv; // Enable for new fire tablets
            BroadcastRecieverDetection = true;
            ApplicationOpenDetection = !tv; // Fall back enabled by default for non tv users
            SetLanguage = false; // Override Language
            uLocale = "RU"; // User Locale
            UseGSearch = false; // Swap Alexa with Google

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("defaultsLoaded", true);
            editor.commit(); // Commit the edits!
            SaveSettings();
        }

        public static void SaveSettings()
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("HardwareDetection", HardwareDetection);
            editor.putBoolean("ApplicationOpenDetection", ApplicationOpenDetection);
            editor.putBoolean("BroadcastRecieverDetection", BroadcastRecieverDetection);
            editor.putBoolean("OverlayApplicationDetection", OverlayApplicationDetection);
            editor.putBoolean("MenuButtonOverride", MenuButtonOverride);
            editor.putBoolean("RecentAppOverride", RecentAppOverride);
            editor.putBoolean("SetLanguage", SetLanguage);
            editor.putBoolean("UseGSearch", UseGSearch);
            editor.putString("uLocale", uLocale);
            editor.commit(); // Commit the edits!
            Toast.makeText(MainActivity.GetContext(), R.string.settings_saved, Toast.LENGTH_LONG).show();
            // Apply Language
            MainActivity.SetLanguage();
        }
    }

    public static SettingStore GetSettings()
    {
        if (settingStore == null)
            settingStore = new SettingStore();
        return settingStore;
    }
}
