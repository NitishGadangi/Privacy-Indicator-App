package com.nitish.privacyindicator.services;

import android.accessibilityservice.AccessibilityService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.nitish.privacyindicator.HomeActivity;
import com.nitish.privacyindicator.R;
import com.nitish.privacyindicator.SharedPrefManager;

import java.util.List;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

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

    private String notification_channel_id = "PRIVACY_INDICATORS_NOTIFICATION";
    private NotificationManagerCompat notifManager;
    private NotificationCompat.Builder notificationBuilder;
    private int notificationID = 256;

    private boolean isCameraOn = false;
    private boolean isMicOn = false;

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
                isCameraOn = false;
                hideCam();
                dismissNotification();
            }

            @Override
            public void onCameraUnavailable(String cameraId) {
                super.onCameraUnavailable(cameraId);
                isCameraOn = true;
                showCam();
                triggerVibration();
                showNotification();
            }
        };
        return cameraCallback;
    }

    private AudioManager.AudioRecordingCallback getMicCallback(){
        micCallback = new AudioManager.AudioRecordingCallback() {
            @Override
            public void onRecordingConfigChanged(List<AudioRecordingConfiguration> configs) {
                if (configs.size() > 0) {
                    isMicOn = true;
                    showMic();
                    triggerVibration();
                    showNotification();
                }else {
                    isMicOn = false;
                    hideMic();
                    dismissNotification();
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
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.indicators_layout, mLayout);
        windowManager.addView(mLayout, layoutParams);
    }

    private void updateLayoutGravity(){
        layoutParams.gravity = getLayoutGravity();
        windowManager.updateViewLayout(mLayout, layoutParams);
    }

    //0-TopRight 1-BotRight 2-BotLeft 3-TopLeft 4-TopCenter 5-BottomCenter
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
        }else if (position == 4){
            return Gravity.TOP | Gravity.CENTER;
        }else if (position == 5){
            return Gravity.BOTTOM | Gravity.CENTER;
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
        fade_in.setDuration(350);
        fade_in.setFillAfter(true);
        view.startAnimation(fade_in);
    }

    public void downScaleView(View view) {
        ScaleAnimation fade_in =  new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        fade_in.setDuration(350);
        fade_in.setFillAfter(true);
        view.startAnimation(fade_in);
    }

    private void setupNotification(){
        createNotificationChannel();
        notificationBuilder =
                new NotificationCompat.Builder(getApplicationContext(), notification_channel_id)
                        .setSmallIcon(R.drawable.ic_ring)
                        .setContentTitle(getNotificationTitle())
                        .setContentText(getNotificationDescription())
                        .setContentIntent(getPendingIntent())
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        notifManager = NotificationManagerCompat.from(getApplicationContext());
    }

    private String getNotificationTitle(){
        if (isCameraOn && isMicOn) return "Your Camera and Mic is ON";
        if (isCameraOn && !isMicOn) return "Your Camera is ON";
        if (!isCameraOn && isMicOn) return "Your MIC is ON";
        return "Your Camera or Mic is ON";
    }

    private String getNotificationDescription(){
        if (isCameraOn && isMicOn) return "A third-party app is using your Camera and Microphone";
        if (isCameraOn && !isMicOn) return "A third-party app is using your Camera";
        if (!isCameraOn && isMicOn) return "A third-party app is using your Microphone";
        return "A third-party app is using your Camera or Microphone";
    }

    private void showNotification(){
        if (sharedPrefManager.isNotificationEnabled()) {
            setupNotification();
            if (notifManager != null) notifManager.notify(notificationID, notificationBuilder.build());
        }
    }

    private void dismissNotification(){
        if (isCameraOn || isMicOn) {
            showNotification();
        } else {
            if (notifManager != null) notifManager.cancel(notificationID);
        }
    }

    private PendingIntent getPendingIntent(){
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        return PendingIntent.getActivity(getApplicationContext(), 1, intent, FLAG_UPDATE_CURRENT);
    }

    private void createNotificationChannel(){
        final String notification_channel = "Notifications for Privacy Indicators";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(notification_channel_id, notification_channel, importance);
            String description = getString(R.string.notification_alert_summary);
            channel.setDescription(description);
            channel.setLightColor(Color.RED);
            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
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
