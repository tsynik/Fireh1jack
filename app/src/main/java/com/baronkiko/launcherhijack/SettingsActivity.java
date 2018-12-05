package com.baronkiko.launcherhijack;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

public class SettingsActivity extends AppCompatActivity
{
    private CheckBox hwButtonDetection, launncherOpen, broadcastReciever, overlayDetection, disableWhileMenuHeld, disableInTaskSwitcher;

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settingsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.loadDefaults:
                SettingsMan.SettingStore settings = SettingsMan.GetSettings();
                settings.LoadDefaults();

                hwButtonDetection.setChecked(settings.HardwareDetection);
                launncherOpen.setChecked(settings.ApplicationOpenDetection);
                broadcastReciever.setChecked(settings.BroadcastRecieverDetection);
                overlayDetection.setChecked(settings.OverlayApplicationDetection);
                disableWhileMenuHeld.setChecked(settings.MenuButtonOverride);
                disableInTaskSwitcher.setChecked(settings.RecentAppOverride);

                settings.SaveSettings();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void AddListeners(final View v, final CheckBox c)
    {
        v.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                c.toggle();
            }
        });

        v.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                else if (event.getAction() == MotionEvent.ACTION_UP)
                    v.setBackgroundColor(Color.TRANSPARENT);
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        SettingsMan.SettingStore settings = SettingsMan.GetSettings();

        // Hardware button detection
        View cbv = findViewById(R.id.hardwareCBView);
        hwButtonDetection = (CheckBox)findViewById(R.id.hardwareCB);
        hwButtonDetection.setChecked(settings.HardwareDetection);
        AddListeners(cbv, hwButtonDetection);


        // Launcher open detection
        cbv = findViewById(R.id.openApplicationCBView);
        launncherOpen = (CheckBox)findViewById(R.id.openApplicationCB);
        launncherOpen.setChecked(settings.ApplicationOpenDetection);
        AddListeners(cbv, launncherOpen);

        // Broadcast reciever detection
        cbv = findViewById(R.id.broadcastCBView);
        broadcastReciever = (CheckBox)findViewById(R.id.broadcastCB);
        broadcastReciever.setChecked(settings.BroadcastRecieverDetection);
        AddListeners(cbv, broadcastReciever);

        // Overlay detection
        cbv = findViewById(R.id.overlayCBView);
        overlayDetection = (CheckBox)findViewById(R.id.overlayCB);
        overlayDetection.setChecked(settings.OverlayApplicationDetection);
        AddListeners(cbv, overlayDetection);

        // Disable while menu held
        cbv = findViewById(R.id.menuButtonOverrideCBView);
        disableWhileMenuHeld = (CheckBox)findViewById(R.id.menuButtonOverrideCB);
        disableWhileMenuHeld.setChecked(settings.MenuButtonOverride);
        AddListeners(cbv, disableWhileMenuHeld);

        // Enable in task switcher
        cbv = findViewById(R.id.taskSwitchCBView);
        disableInTaskSwitcher = (CheckBox)findViewById(R.id.taskSwitchCB);
        disableInTaskSwitcher.setChecked(settings.RecentAppOverride);
        AddListeners(cbv, disableInTaskSwitcher);



        // Save Button
        findViewById(R.id.saveButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SettingsMan.SettingStore settings = SettingsMan.GetSettings();

                settings.HardwareDetection = hwButtonDetection.isChecked();
                settings.ApplicationOpenDetection = launncherOpen.isChecked();
                settings.BroadcastRecieverDetection = broadcastReciever.isChecked();
                settings.OverlayApplicationDetection = overlayDetection.isChecked();
                settings.MenuButtonOverride = disableWhileMenuHeld.isChecked();
                settings.RecentAppOverride = disableInTaskSwitcher.isChecked();

                settings.SaveSettings();
                onBackPressed();
            }
        });

        // Cancel Button
        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }
}
