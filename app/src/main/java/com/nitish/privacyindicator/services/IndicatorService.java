package com.nitish.privacyindicator.services;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.nitish.privacyindicator.R;
import com.nitish.privacyindicator.SharedPrefManager;

import java.util.List;

public class IndicatorService extends AccessibilityService {
    private FrameLayout mLayout;
    private ImageView iv_cam, iv_mic;

    private CameraManager cameraManager;
    private CameraManager.AvailabilityCallback cameraCallback;
    private AudioManager audioManager;
    private AudioManager.AudioRecordingCallback micCallback;
    private SharedPrefManager sharedPrefManager;

    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;

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
        if (cameraManager == null) cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        cameraManager.registerAvailabilityCallback(getCameraCallback(), null);

        if (audioManager == null) audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerAudioRecordingCallback(getMicCallback(), null);
    }

    private CameraManager.AvailabilityCallback getCameraCallback(){
        cameraCallback = new CameraManager.AvailabilityCallback() {
            @Override
            public void onCameraAvailable(String cameraId) {
                super.onCameraAvailable(cameraId);
                hideCam();
            }

            @Override
            public void onCameraUnavailable(String cameraId) {
                super.onCameraUnavailable(cameraId);
                showCam();
                triggerVibration();
            }
        };
        return cameraCallback;
    }

    private AudioManager.AudioRecordingCallback getMicCallback(){
        micCallback = new AudioManager.AudioRecordingCallback() {
            @Override
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                if (configs.size() > 0) {
                    showMic();
                    triggerVibration();
                }else {
                    hideMic();
                }
            }
        };
        return micCallback;
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
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = getLayoutGravity();
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.indicators_layout, mLayout);
        windowManager.addView(mLayout, layoutParams);
    }

    private void updateLayoutGravity(){
        layoutParams.gravity = getLayoutGravity();
        windowManager.updateViewLayout(mLayout, layoutParams);
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

    private void showMic(){
        if (sharedPrefManager.isMicIndicatorEnabled()){
            updateLayoutGravity();
            updateMicIndicatorSize();
            setupDotTints();
            iv_mic.setVisibility(View.VISIBLE);
//            upScaleView(iv_mic);
        }
    }

    private void updateMicIndicatorSize() {
        int size = sharedPrefManager.getMicIndicatorSize();
        iv_mic.requestLayout();
        iv_mic.getLayoutParams().height = size;
        iv_mic.getLayoutParams().width = size;
    }

    private void hideMic(){
//        downScaleView(iv_mic);
        iv_mic.setVisibility(View.GONE);
    }

    private void showCam(){
        if (sharedPrefManager.isCameraIndicatorEnabled()){
            updateLayoutGravity();
            updateCamIndicatorSize();
            setupDotTints();
            iv_cam.setVisibility(View.VISIBLE);
//            upScaleView(iv_cam);
        }
    }

    private void updateCamIndicatorSize() {
        int size = sharedPrefManager.getCameraIndicatorSize();
        iv_cam.requestLayout();
        iv_cam.getLayoutParams().height = size;
        iv_cam.getLayoutParams().width = size;
    }

    private void hideCam(){
//        downScaleView(iv_cam);
        iv_cam.setVisibility(View.GONE);
    }

    public void upScaleView(View view) {
        ScaleAnimation fade_in =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(350);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        view.startAnimation(fade_in);
    }

    public void downScaleView(View view) {
        ScaleAnimation fade_in =  new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(350);     // animation duration in milliseconds
        fade_in.setFillAfter(true);    // If fillAfter is true, the transformation that this animation performed will persist when it is finished.
        view.startAnimation(fade_in);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

    }

    private void unRegisterCameraCallBack(){
        if (cameraManager != null
            && cameraCallback !=null) {
            cameraManager.unregisterAvailabilityCallback(cameraCallback);
        }
    }

    private void unRegisterMicCallback(){
        if (audioManager != null
            && micCallback != null) {
            audioManager.unregisterAudioRecordingCallback(micCallback);
        }
    }

    @Override
    public void onDestroy() {
        unRegisterCameraCallBack();
        unRegisterMicCallback();
        super.onDestroy();
    }
}
