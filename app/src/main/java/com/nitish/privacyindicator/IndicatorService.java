package com.nitish.privacyindicator;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.AudioRecordingConfiguration;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class IndicatorService extends AccessibilityService {
    FrameLayout mLayout;
    ImageView iv_cam, iv_mic;

    CameraManager cameraManager;
    AudioManager audioManager;

    @Override
    protected void onServiceConnected() {
        createOverlay();
        setUpInnerViews();
        startCallBacks();
    }

    private void startCallBacks() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraManager.registerAvailabilityCallback(new CameraManager.AvailabilityCallback() {
            @Override
            public void onCameraAvailable(String cameraId) {
                super.onCameraAvailable(cameraId);
                iv_cam.setVisibility(View.GONE);
            }

            @Override
            public void onCameraUnavailable(String cameraId) {
                super.onCameraUnavailable(cameraId);
                iv_cam.setVisibility(View.VISIBLE);
                triggerVibration();
            }
        }, null);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.registerAudioRecordingCallback(new AudioManager.AudioRecordingCallback() {
            @Override
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                if (configs.size() > 0) {
                    iv_mic.setVisibility(View.VISIBLE);
                    triggerVibration();
                }else {
                    iv_mic.setVisibility(View.GONE);
                }
            }
        }, null);
    }

    private void triggerVibration(){

    }

    private void setUpInnerViews() {
        iv_cam = mLayout.findViewById(R.id.iv_cam);
        iv_mic = mLayout.findViewById(R.id.iv_mic);
        iv_cam.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_cam.setVisibility(View.GONE);
                iv_mic.setVisibility(View.GONE);
            }
        },1000);
    }

    private void createOverlay() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = getLayoutGravity();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.indicators_layout, mLayout);
        wm.addView(mLayout, lp);
    }

    private int getLayoutGravity() {
        return Gravity.TOP | Gravity.END;
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
