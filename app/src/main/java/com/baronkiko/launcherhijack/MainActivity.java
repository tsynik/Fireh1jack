package com.baronkiko.launcherhijack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private ListView mListAppInfo;
    private MenuItem launcher, sysApps;

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

        mListAppInfo = (ListView) findViewById(R.id.lvApps);
        // create new adapter
        AppInfoAdapter adapter = new AppInfoAdapter(this, Utilities.getInstalledApplication(this, l, sys), getPackageManager());
        // set adapter to list view
        mListAppInfo.setAdapter(adapter);
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
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
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
                                // We need an Editor object to make preference changes.
                                // All objects are from android.context.Context
                                SharedPreferences settings = getSharedPreferences("LauncherHijack", MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("ChosenLauncher", appInfo.packageName);
                                editor.apply(); // Commit the edits!
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }
}
