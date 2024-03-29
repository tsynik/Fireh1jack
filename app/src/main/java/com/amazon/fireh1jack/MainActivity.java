package com.amazon.fireh1jack;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.amazon.fireh1jack.R;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private static Context context;

    private ListView mListAppInfo;
    private MenuItem launcher, sysApps;
    private int prevSelectedIndex = 0;

    public final static int REQUEST_CODE = 5466;

    public static void SetContext(Context c)
    {
        if (context == null)
            context = c;
    }
    public static Context GetContext()
    {
        return context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.mainmenu, menu);
        sysApps = menu.getItem(0);
        launcher = menu.getItem(1);
        launcher.setChecked(true);
        sysApps.setChecked(true);
        UpdateList();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.launcher:
                launcher.setChecked(!launcher.isChecked());
                if (!launcher.isChecked())
                    sysApps.setChecked(true);
                UpdateList();
                break;

            case R.id.sysApps:
                sysApps.setChecked(!sysApps.isChecked());
                UpdateList();
                break;

//            case R.id.help:
//                OpenHelp();
//                break;
//
//            case R.id.donate:
//                OpenDonate();
//                break;

            case  R.id.settings:
                Intent myIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(myIntent);
                break;

            case  R.id.sys_settings:
                    // https://ourcodeworld.com/articles/read/318/how-to-open-android-settings-programmatically-with-java
                    startActivityForResult(new Intent(Settings.ACTION_SETTINGS), 0);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void OpenHelp()
    {
        Intent helpIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BaronKiko/LauncherHijack/blob/master/HELP.md"));
        startActivity(helpIntent);
    }

    private void OpenDonate()
    {
        Intent donateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/BaronKiko/LauncherHijack/blob/master/README.md#donations"));
        startActivity(donateIntent);
    }

    private void UpdateList()
    {
        boolean sys = sysApps.isChecked();
        boolean l = launcher.isChecked();
        List<ResolveInfo> appInfo = Utilities.getInstalledApplication(this, l, sys); // Get available apps

        mListAppInfo = findViewById(R.id.lvApps);

        // create new adapter
        AppAdapter adapter = new AppAdapter(this, appInfo, getApplicationContext().getPackageManager());

        // set adapter to list view
        mListAppInfo.setAdapter(adapter);

        SharedPreferences settings = getSharedPreferences("FireH1jack", MODE_PRIVATE);
        String selectedPackage = settings.getString("ChosenLauncher", "com.teslacoilsw.launcher");

        for (int i = 0; i < appInfo.size(); i++) {
            if (appInfo.get(i).activityInfo.packageName.equals(selectedPackage)) {
                prevSelectedIndex = i;
                mListAppInfo.setSelection(i);
                mListAppInfo.setItemChecked(i, true);
                break;
            }
        }
    }

    private void showSecurityAlert() {
        // Notify User
        CharSequence welcomeMessage = getResources().getText(R.string.welcome);
        CharSequence welcomeMessage2 = getResources().getText(R.string.welcome2);
        String adbCommand1 = "adb tcpip 5555";
        String adbCommand2 = "adb connect (FireTV IP)";
        String adbCommand3 = "adb shell";
        String adbCommand4 = "# pm grant com.amazon.fireh1jack android.permission.WRITE_SECURE_SETTINGS";
        String adbCommand5 = "# pm grant com.amazon.fireh1jack android.permission.CHANGE_CONFIGURATION";
        String adbCommand6 = "# pm grant com.amazon.fireh1jack android.permission.SYSTEM_ALERT_WINDOW";
        String alertExtra = " ";

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(R.string.perm_notice);

        LinearLayout alertContents = new LinearLayout(this);
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        TextView alertHeader = new TextView(this);
        TextView alertMessage = new TextView(this);
        TextView alertMessageExtraLine = new TextView(this);

        alertHeader.setText(welcomeMessage + " " + welcomeMessage2 + "\n");
        alertHeader.setGravity(Gravity.LEFT);
        alertHeader.setTextColor(Color.WHITE);
        alertHeader.setPaddingRelative(20, 20, 10, 10);

        alertMessage.setText(adbCommand1 + "\n" + adbCommand2 + "\n" + adbCommand3 + "\n" + adbCommand4 + "\n" + adbCommand5  + "\n" + adbCommand6);
        alertMessage.setGravity(Gravity.LEFT);
        alertMessage.setTextColor(Color.WHITE);
        alertMessage.setPaddingRelative(20, 10, 10, 10);

        alertMessageExtraLine.setText(alertExtra);
        alertMessageExtraLine.setGravity(Gravity.LEFT);
        alertMessageExtraLine.setTextColor(Color.WHITE);

        alertContents.setLayoutParams(lllp);
        alertContents.setOrientation(LinearLayout.VERTICAL);
        alertContents.removeAllViews();
        alertContents.addView(alertHeader);
        alertContents.addView(alertMessage);
        alertContents.addView(alertMessageExtraLine);

        alertDialog.setView(alertContents);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                try {
                    startActivityForResult(intent, REQUEST_CODE);
                }
                catch(SecurityException | ActivityNotFoundException e) {
                    showSecurityAlert();
                }
                return false;
            }
            else {
                return true;
            }
        }
        else {
            return true;
        }
    }

    public static boolean isAccessibilityEnabled(Context context, String id) {

        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> runningServices = am
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : runningServices) {
            if (id.equals(service.getId())) {
                return true;
            }
        }
        return false;
    }
    
    // AVAIL
    public boolean isPackageEnabled(String str) {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        try {
            return packageManager.getApplicationInfo(str, 0).enabled;
        } catch (Exception unused) {
            return false;
        }
    }
    public static boolean isPackageEnabled(String str, PackageManager packageManager) {
        try {
            return packageManager.getApplicationInfo(str, 0).enabled;
        } catch (Exception unused) {
            return false;
        }
    }
	// LOCALE
    public static boolean setLocale(Locale loc) {
        try {
            Class<?> activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Object am = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
            Configuration config = (Configuration) am.getClass().getMethod("getConfiguration").invoke(am);
            config.locale = loc;
            config.getClass().getDeclaredField("userSetLocale").setBoolean(config, true);
            am.getClass().getMethod("updateConfiguration", new Class[]{Configuration.class}).invoke(am, new Object[]{config});
            // am.getClass().getMethod("updatePersistentConfiguration", new Class[]{Configuration.class}).invoke(am, new Object[]{config});
            BackupManager.dataChanged("com.android.providers.settings");
            return true;
        } catch(Exception e2) { 
            e2.printStackTrace();
            return false;
        }
    }
    // LANGUAGE
    public static boolean SetLanguage() {
        if (SettingsMan.GetSettings().SetLanguage)
        {
            // Locale newLocale = new Locale("ru");
            // Locale newLocale = new Locale(SettingsMan.GetSettings().uLocale);
            String lang = context.getSharedPreferences("FireH1jack", MODE_PRIVATE).getString("uLocale", "en").toLowerCase();
            if(lang != null && !lang.equals("")) {
                try {
                Locale newLocale;
                // If lang is <= 3 chars, it is a language code
                if (lang.length() <= 3) {
                    String ccode = "US";
                    switch(lang)
                    {
                    case "ru":
                        ccode = "RU";
                        break;
                    case "uk":
                        ccode = "UA";
                        break;
                    case "de":
                        ccode = "DE";
                        break;
                    default: 
                        break;
                    }
                    newLocale = new Locale(lang, ccode);
                } else {
                    newLocale = (Locale) Locale.class.getField(lang).get(Locale.getDefault());
                }
                Locale.setDefault(newLocale);

                if (setLocale(newLocale)) {
                    Toast.makeText(context, R.string.lang_set_ok, Toast.LENGTH_LONG).show();
                    return true;
                } else {
                    Toast.makeText(context, R.string.lang_not_ok, Toast.LENGTH_LONG).show();
                //  showSecurityAlert();
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    // KB: https://stackoverflow.com/questions/18486130/detect-if-input-method-has-been-selected
    public static boolean isThisKeyboardEnabled(Context context, String pkg) {
        final String enabledIME = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS);
        if (TextUtils.isEmpty(enabledIME))
            return false;
        //ComponentName defaultInputMethod = ComponentName.unflattenFromString(enabledIME);
        // Log.d(TAG, "enabledIME: " + enabledIME + ", ComponentName: " + defaultInputMethod.toString());
        // return defaultInputMethod.getPackageName().equals(pkg);
        return enabledIME.contains(pkg);
    }
    public static boolean isThisKeyboardSelected(Context context, String pkg) {
        final String defaultIME = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        if (TextUtils.isEmpty(defaultIME))
            return false;
        // ComponentName defaultInputMethod = ComponentName.unflattenFromString(defaultIME);
        // Log.d(TAG, "defaultIME: " + defaultIME + ", ComponentName: " + defaultInputMethod.toString());
        // return defaultInputMethod.getPackageName().equals(pkg);
        return defaultIME.contains(pkg);
    }
    public static boolean SetKeyBoard(String pkg, String cmp) {
        if (Utilities.isPackageEnabled(context, pkg))
        {
            try {        
                String im = Settings.Secure.getString(context.getContentResolver(), Secure.ENABLED_INPUT_METHODS);
                if (!isThisKeyboardEnabled(context, pkg)) {
                    // Toast.makeText(context, "Enable " + pkg, Toast.LENGTH_LONG).show();
                    if (!TextUtils.isEmpty(im))
                        im = im + ":" + pkg + "/" + cmp;
                    else
                        im = pkg + "/" + cmp;
                    Settings.Secure.putString(context.getContentResolver(), Settings.Secure.ENABLED_INPUT_METHODS, im);
                }
                if (!isThisKeyboardSelected(context, pkg)) {
                    // Toast.makeText(context, "Set " + pkg + " as default", Toast.LENGTH_LONG).show();
                    im = pkg + "/" + cmp;
                    Settings.Secure.putString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD, im);
                }
                return true;
            } catch(Exception e) {
                Toast.makeText(context, "Keyboard not set", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        return false;
    }
    // Freeze KFTV Launcher
    public static boolean FreezeLauncher(boolean mode) {
        try {
            Settings.Global.putString(context.getContentResolver(), "frozenMode", mode ? "enabled" : "disabled");
            return true;
        } catch(Exception e) {
            Toast.makeText(context, "Can't change launcher mode!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return false;
    }
    // Alexa TERMS hack
    public static void setupSearch() {
        if (SettingsMan.GetSettings().UseGSearch)
        {
            if (Utilities.isPackageEnabled(context, "com.amazon.vizzini"))
            try {
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear com.amazon.vizzini");
            } catch (Exception e) {
                e.printStackTrace();
            }
            passOOBE();
        }
    }
    // override OOBE keys block
    public static boolean passOOBE() {
        try {
            Settings.Global.putString(context.getContentResolver(), "com.amazon.tv.oobe.RESTRICT_REMOTE_BUTTONS", "0");
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void clearSelfData() {
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // note: it has a return value!
            } else {
                String packageName = getApplicationContext().getPackageName();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec("pm clear "+packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        SetContext(getApplicationContext());

        super.onCreate(savedInstanceState);

        // GOOGLE
        // PackageManager pm = getApplicationContext().getPackageManager();
        // if (SettingsMan.GetSettings().UseGSearch)
        //	try
        //    {
                // disable Alexa
                // https://www.xda-developers.com/replace-alexa-google-assistant-amazon-fire-7-hd-8-hd-10/
                // getApplicationContext().getPackageManager().setApplicationEnabledSetting("com.amazon.vizzini", PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0)
                // alexa setting off
                // Secure.putString(getContentResolver(), "alexa_enabled", "0");
                // alexa package off
                // if (isPackageEnabled("com.amazon.vizzini", pm))
                //    pm.setApplicationEnabledSetting("com.amazon.vizzini", PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                
                // if (isPackageEnabled("com.google.android.katniss", pm)) {
                    // Secure.putString(getContentResolver(), "voice_interaction_service", "com.google.android.katniss/.search.serviceapi.KatnissVoiceInteractionService");
                    // Secure.putString(getContentResolver(), "voice_recognition_service", "com.google.android.katniss/.search.serviceapi.KatnissRecognitionService");
                // }

        //    } catch(SecurityException e) {
        //        showSecurityAlert();
        //        } catch(Exception e1) {
        //            e1.printStackTrace();
        //        }

        // } else {
        //    try {
                // alexa setting on
                // Secure.putString(getContentResolver(), "alexa_enabled", "1");
                // alexa package on
                // if (!isPackageEnabled("com.amazon.vizzini", pm))
                //    pm.setApplicationEnabledSetting("com.amazon.vizzini", PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 0);
                // Google OFF
                // if (isPackageEnabled("com.google.android.katniss", pm))
                //    pm.setApplicationEnabledSetting("com.google.android.katniss", PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                
        //    } catch(SecurityException e) {
        //        showSecurityAlert();
        //        } catch(Exception e1) {
        //             e1.printStackTrace();
        //    }
        // }

        // PERMS
        if (!isAccessibilityEnabled(context, "com.amazon.fireh1jack/.AccServ"))
        {
            try {
                String as = Settings.Secure.getString(context.getContentResolver(), Secure.ENABLED_ACCESSIBILITY_SERVICES);
                if (!TextUtils.isEmpty(as))
                    as = as + ":" + "com.amazon.fireh1jack/.AccServ";
                else
                    as = "com.amazon.fireh1jack/.AccServ";
                Settings.Secure.putString(getContentResolver(), Secure.ENABLED_ACCESSIBILITY_SERVICES, as);
                Settings.Secure.putString(getContentResolver(), Secure.ACCESSIBILITY_ENABLED, "1");
            } catch(SecurityException e) {
                showSecurityAlert();
                } catch(Exception e1) {
                    e1.printStackTrace();
            }
        }
        if (!isAccessibilityEnabled(context, "com.amazon.fireh1jack/.AccServ"))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle(R.string.acc_disabled)
                    .setMessage(R.string.acc_disabled_message)
                    .setCancelable(true)
                    .setNegativeButton(getResources().getText(R.string.close), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {

                        }
                    })
                    .setPositiveButton(getResources().getText(R.string.help), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            OpenHelp();
                        }
                    });
            if (!SettingsMan.GetSettings().isFireTV) // ex. RunningOnTV
            {
                builder.setNeutralButton(getResources().getText(R.string.open_settings), new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        startActivityForResult(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), 0);
                    }
                });
            }
            AlertDialog alert = builder.create();
            alert.show();
        }
        // No launcher selected - TOAST
        else if (getApplicationContext().getSharedPreferences("FireH1jack", MODE_PRIVATE).getString("ChosenLauncher", "com.amazon.fireh1jack").equals("com.amazon.fireh1jack"))
            Toast.makeText(getApplicationContext(), R.string.choose_launcher, Toast.LENGTH_LONG).show();

        setContentView(com.amazon.fireh1jack.R.layout.activity_main);

        mListAppInfo = findViewById(R.id.lvApps);

        // implement event when an item on list view is selected
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                // get the list adapter
                AppAdapter appInfoAdapter = (AppAdapter) parent.getAdapter();
                // get selected item on the list
                final ResolveInfo appInfo = (ResolveInfo) appInfoAdapter.getItem(pos);

                // Notify User
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle(R.string.set_launcher);
                alertDialog.setMessage(getResources().getText(R.string.set_launcher_to) + " " + appInfo.loadLabel(getPackageManager()) + " (" + appInfo.activityInfo.packageName + ")");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getText(R.string.apply),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                prevSelectedIndex = pos;

                                // We need an Editor object to make preference changes.
                                // All objects are from android.context.Context
                                SharedPreferences settings = getSharedPreferences("FireH1jack", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("ChosenLauncher", appInfo.activityInfo.applicationInfo.packageName);
                                editor.putString("ChosenLauncherName", appInfo.activityInfo.name);
                                editor.commit(); // Commit the edits!
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getText(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mListAppInfo.setSelection(prevSelectedIndex);
                                mListAppInfo.setItemChecked(prevSelectedIndex, true);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (Settings.canDrawOverlays(this)) {
                ServiceMan.Start(this);
            }
        }
    }    
}
