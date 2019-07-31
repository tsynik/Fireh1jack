package com.amazon.fireh1jack;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import com.amazon.fireh1jack.R;

public class SettingsActivity extends AppCompatActivity
{
    private CheckBox hwButtonDetection, launncherOpen, broadcastReciever, overlayDetection, disableWhileMenuHeld, disableInTaskSwitcher, setLang, useGS;
    private String uLoc;

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
                setLang.setChecked(settings.SetLanguage);
                useGS.setChecked(settings.UseGSearch);

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
        // setTitle(getResources().getText(R.string.settings));
        setTitle(R.string.settings); // R.string.settings
        SettingsMan.SettingStore settings = SettingsMan.GetSettings();

        // Spinner localeSpinner = (Spinner) findViewById(R.id.locale_sspinner);
        // Create an ArrayAdapter using the string array and a default spinner
        // ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
        //        .createFromResource(this, R.array.pref_locale,
        //                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        // localeSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        // localeSpinner.setAdapter(staticAdapter);

        Spinner dynamicSpinner = (Spinner) findViewById(R.id.locale_spinner);
        String[] items = new String[] { "RU", "UK", "DE", "EN" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        dynamicSpinner.setAdapter(adapter);
        // set title
        // dynamicSpinner.setPrompt("Choose Language:");
        uLoc = settings.uLocale;
	    // Toast.makeText(SettingsActivity.this, "uLoc : " +  uLoc, Toast.LENGTH_SHORT).show();
        // set selection
        if (uLoc == "EN")
          dynamicSpinner.setSelection(3);
        else if (uLoc == "DE")
          dynamicSpinner.setSelection(2);
        else if (uLoc == "UK")
          dynamicSpinner.setSelection(1);
        else if (uLoc == "RU")
          dynamicSpinner.setSelection(0);
        dynamicSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v("item", (String) parent.getItemAtPosition(position));
                uLoc = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
                // uLoc = (String) dynamicSpinner.getSelectedItem();
            }
        });
        // override uLoc var on select
        uLoc = (String) dynamicSpinner.getSelectedItem(); // .toString()
	    // Toast.makeText(SettingsActivity.this, "selected : " +  uLoc, Toast.LENGTH_SHORT).show();

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

        // Set Lang (RU)
        cbv = findViewById(R.id.setLangCBView);
        setLang = (CheckBox)findViewById(R.id.setLangCB);
        setLang.setChecked(settings.SetLanguage);
        AddListeners(cbv, setLang);

        // Use GSearch
        cbv = findViewById(R.id.useGSCBView);
        useGS = (CheckBox)findViewById(R.id.useGSCB);
        useGS.setChecked(settings.UseGSearch);
        AddListeners(cbv, useGS);


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
                settings.SetLanguage = setLang.isChecked();
                settings.UseGSearch = useGS.isChecked();
                settings.uLocale = uLoc;

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
