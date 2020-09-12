package com.nitish.privacyindicator;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class FragmentHome extends Fragment {

    //Root Views
    private View root;
    private View contentServiceEnabled;
    private View contentServiceDisabled;

    private SwitchCompat mainSwitch;

    @Override
    public void onResume() {
        setMainContentLayouts();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home,container, false);

        setUpViews();
        setMainContentLayouts();
        setUpListeners();
        return root;
    }

    private void setUpViews() {
        mainSwitch = root.findViewById(R.id.main_switch);
        contentServiceEnabled = root.findViewById(R.id.content_service_enabled);
        contentServiceDisabled = root.findViewById(R.id.content_service_disabled);
    }

    private void setMainContentLayouts() {
        if (isAccessibilityEnabled()){
            mainSwitch.setChecked(true);
            mainSwitch.setText("Enabled");
            contentServiceEnabled.setVisibility(View.VISIBLE);
            contentServiceDisabled.setVisibility(View.GONE);
        }
        else {
            mainSwitch.setChecked(false);
            mainSwitch.setText("Disabled");
            contentServiceEnabled.setVisibility(View.GONE);
            contentServiceDisabled.setVisibility(View.VISIBLE);
        }
    }

    private void setUpListeners() {
        mainSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);
            }
        });
    }


    public boolean isAccessibilityEnabled() {
        String LOGTAG = "ACCESSIBILITY_ERROR";
        int accessibilityEnabled = 0;
        final String ACCESSIBILITY_SERVICE = "com.nitish.privacyindicator/com.nitish.privacyindicator.IndicatorService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(getActivity().getContentResolver(),android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.d(LOGTAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled==1) {

            String settingValue = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOGTAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICE)){
                        return true;
                    }
                }
            }
        }
        else {
            Log.d(LOGTAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }
}
