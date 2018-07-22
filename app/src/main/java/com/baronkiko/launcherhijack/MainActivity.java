package com.baronkiko.launcherhijack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private ListView mListAppInfo;
    private MenuItem launcher, sysApps;
    private int prevSelectedIndex = 0;

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
                if (launcher.isChecked())
                    sysApps.setChecked(true);
                UpdateList();
                return true;

            case R.id.sysApps:
                sysApps.setChecked(!sysApps.isChecked());
                UpdateList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void UpdateList()
    {
        boolean sys = sysApps.isChecked();
        boolean l = launcher.isChecked();
        List<ApplicationInfo> appInfo = Utilities.getInstalledApplication(this, l, sys); // Get available apps

        mListAppInfo = (ListView) findViewById(R.id.lvApps);

        // create new adapter
        AppInfoAdapter adapter = new AppInfoAdapter(this, appInfo, getPackageManager());

        // set adapter to list view
        mListAppInfo.setAdapter(adapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            SharedPreferences settings = getSharedPreferences("LauncherHijack", MODE_PRIVATE);
        else
            SharedPreferences settings = getSharedPreferences("LauncherHijack", MODE_WORLD_READABLE);

        String selectedPackage = settings.getString("ChosenLauncher", "com.teslacoilsw.launcher");

        for (int i = 0; i < appInfo.size(); i++) {
            if (appInfo.get(i).packageName.equals(selectedPackage)) {
                prevSelectedIndex = i;
                mListAppInfo.setSelection(i);
                mListAppInfo.setItemChecked(i, true);
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ServiceMan.Start(this);

        setContentView(com.baronkiko.launcherhijack.R.layout.activity_main);

        mListAppInfo = (ListView) findViewById(R.id.lvApps);

        // implement event when an item on list view is selected
        mListAppInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
                // get the list adapter
                AppInfoAdapter appInfoAdapter = (AppInfoAdapter) parent.getAdapter();
                // get selected item on the list
                final ApplicationInfo appInfo = (ApplicationInfo) appInfoAdapter.getItem(pos);

                // Notify User
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Set Launcher");
                alertDialog.setMessage("Set your launcher to " + appInfo.loadLabel(getPackageManager()) + " (" + appInfo.packageName + ")");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                prevSelectedIndex = pos;

                                // We need an Editor object to make preference changes.
                                // All objects are from android.context.Context
                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
                                    SharedPreferences settings = getSharedPreferences("LauncherHijack", MODE_PRIVATE);
                                else
                                    SharedPreferences settings = getSharedPreferences("LauncherHijack", MODE_WORLD_READABLE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("ChosenLauncher", appInfo.packageName);
                                editor.commit(); // Commit the edits!
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
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
}
