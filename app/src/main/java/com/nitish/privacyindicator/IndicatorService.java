package com.nitish.privacyindicator;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.AudioRecordingConfiguration;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
    private FrameLayout mLayout;
    private ImageView iv_cam, iv_mic;

    private CameraManager cameraManager;
    private AudioManager audioManager;
    private SharedPrefManager sharedPrefManager;

    private WindowManager.LayoutParams lp;
    private WindowManager wm;

    @Override
    protected void onServiceConnected() {
        fetchData();
        createOverlay();
        setUpInnerViews();
        startCallBacks();
    }

    private void fetchData() {
        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
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
                updateLayoutGravity();
                setupDotTints();
                if (sharedPrefManager.isCameraIndicatorEnabled()) iv_cam.setVisibility(View.VISIBLE);
                triggerVibration();
            }
        }, null);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        audioManager.registerAudioRecordingCallback(new AudioManager.AudioRecordingCallback() {
            @Override
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                if (configs.size() > 0) {
                    updateLayoutGravity();
                    setupDotTints();
                    if (sharedPrefManager.isMicIndicatorEnabled()) iv_mic.setVisibility(View.VISIBLE);
                    triggerVibration();
                }else {
                    iv_mic.setVisibility(View.GONE);
                }
            }
        }, null);
    }

    private void setupDotTints(){
        setViewTint(iv_cam, sharedPrefManager.getCameraIndicatorColor());
        setViewTint(iv_mic, sharedPrefManager.getMicIndicatorColor());
    }

    private void setViewTint(ImageView imageView, String hex){
        imageView.setColorFilter(Color.parseColor(hex), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void triggerVibration(){
        if (sharedPrefManager.isVibrationEnabled()){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }
    }

    private void setUpInnerViews() {
        iv_cam = mLayout.findViewById(R.id.iv_cam);
        iv_mic = mLayout.findViewById(R.id.iv_mic);
        setupDotTints();
        iv_cam.postDelayed(new Runnable() {
            @Override
            public void run() {
                iv_cam.setVisibility(View.GONE);
                iv_mic.setVisibility(View.GONE);
            }
        },1000);

    }

    private void createOverlay() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        lp = new WindowManager.LayoutParams();
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

    private void updateLayoutGravity(){
        lp.gravity = getLayoutGravity();
        wm.updateViewLayout(mLayout,lp);
    }

    //0-TopRight 1-BotRight 2-BotLeft 3-TopLeft
    private int getLayoutGravity() {
        int position = sharedPrefManager.getPosition();
        if (position == 0){
            return Gravity.TOP | Gravity.END;
        }else if (position == 1){
            return Gravity.BOTTOM | Gravity.END;
        }else if (position == 2){
            return Gravity.BOTTOM | Gravity.START;
        }else if (position == 3){
            return Gravity.TOP | Gravity.START;
        }
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
