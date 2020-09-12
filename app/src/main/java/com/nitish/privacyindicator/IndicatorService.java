package com.nitish.privacyindicator;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

public class IndicatorService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {

    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
