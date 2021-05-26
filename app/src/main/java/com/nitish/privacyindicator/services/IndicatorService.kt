package com.nitish.privacyindicator.services

import android.accessibilityservice.AccessibilityService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraManager.AvailabilityCallback
import android.media.AudioManager
import android.media.AudioManager.AudioRecordingCallback
import android.media.AudioRecordingConfiguration
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nitish.privacyindicator.R
import com.nitish.privacyindicator.databinding.IndicatorsLayoutBinding
import com.nitish.privacyindicator.helpers.updateOpacity
import com.nitish.privacyindicator.helpers.updateSize
import com.nitish.privacyindicator.repository.SharedPrefManager
import com.nitish.privacyindicator.ui.HomeActivity

class IndicatorService : AccessibilityService() {
    private lateinit var binding: IndicatorsLayoutBinding
    private var cameraManager: CameraManager? = null
    private var cameraCallback: AvailabilityCallback? = null
    private var audioManager: AudioManager? = null
    private var micCallback: AudioRecordingCallback? = null
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var layoutParams: WindowManager.LayoutParams
    private lateinit var windowManager: WindowManager
    private val notification_channel_id = "PRIVACY_INDICATORS_NOTIFICATION"
    private var notifManager: NotificationManagerCompat? = null
    private var notificationBuilder: NotificationCompat.Builder? = null
    private val notificationID = 256
    private var isCameraOn = false
    private var isMicOn = false


    override fun onServiceConnected() {
        fetchData()
        createOverlay()
        setUpInnerViews()
        startCallBacks()
    }

    private fun fetchData() {
        sharedPrefManager = SharedPrefManager.getInstance(applicationContext)
    }

    private fun startCallBacks() {
        if (cameraManager == null) cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraManager!!.registerAvailabilityCallback(getCameraCallback(), null)
        if (audioManager == null) audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager!!.registerAudioRecordingCallback(getMicCallback(), null)
    }

    private fun getCameraCallback(): AvailabilityCallback {
        cameraCallback = object : AvailabilityCallback() {
            override fun onCameraAvailable(cameraId: String) {
                super.onCameraAvailable(cameraId)
                isCameraOn = false
                hideCam()
                dismissNotification()
            }

            override fun onCameraUnavailable(cameraId: String) {
                super.onCameraUnavailable(cameraId)
                isCameraOn = true
                showCam()
                triggerVibration()
                showNotification()
            }
        }
        return cameraCallback as AvailabilityCallback
    }

    private fun getMicCallback(): AudioRecordingCallback {
        micCallback = object : AudioRecordingCallback() {
            override fun onRecordingConfigChanged(configs: List<AudioRecordingConfiguration>) {
                if (configs.size > 0) {
                    isMicOn = true
                    showMic()
                    triggerVibration()
                    showNotification()
                } else {
                    isMicOn = false
                    hideMic()
                    dismissNotification()
                }
            }
        }
        return micCallback as AudioRecordingCallback
    }

    private fun triggerVibration() {
        if (sharedPrefManager.isVibrationEnabled) {
            val v = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                //deprecated in API 26
                v.vibrate(500)
            }
        }
    }

    private fun setUpInnerViews() {
        setViewColors()
        binding.ivCam.postDelayed({
            binding.ivCam.visibility = View.GONE
            binding.ivMic.visibility = View.GONE
            binding.ivLoc.visibility = View.GONE
        }, 1000)
    }

    private fun createOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams()
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags = layoutParams!!.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.gravity = layoutGravity
        layoutParams.flags = (WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        binding = IndicatorsLayoutBinding.inflate(LayoutInflater.from(this))
        windowManager.addView(binding.root, layoutParams)
    }

    private fun updateLayoutGravity() {
        layoutParams.gravity = layoutGravity
        windowManager.updateViewLayout(binding.root, layoutParams)
    }

    private val layoutGravity: Int
        get() = sharedPrefManager.indicatorPosition.layoutGravity

    private fun showMic() {
        if (sharedPrefManager.isMicIndicatorEnabled) {
            updateIndicatorProperties()
            binding.ivMic.visibility = View.VISIBLE
        }
    }

    private fun hideMic() {
        binding.ivMic.visibility = View.GONE
    }

    private fun showCam() {
        if (sharedPrefManager.isCameraIndicatorEnabled) {
            updateIndicatorProperties()
            binding.ivCam.visibility = View.VISIBLE
        }
    }

    private fun hideCam() {
        binding.ivCam.visibility = View.GONE
    }

    private fun showLocation() {
        if (sharedPrefManager.isLocationEnabled) {
            updateIndicatorProperties()
            binding.ivLoc.visibility = View.VISIBLE
        }
    }

    private fun hideLocation() {
        binding.ivLoc.visibility = View.GONE
    }

    private fun updateIndicatorProperties(){
        updateLayoutGravity()
        updateIndicatorsSize()
        updateIndicatorsOpacity()
        setViewColors()
    }

    private fun setViewColors() {
        val dotsTint = sharedPrefManager.indicatorColor
        val indicatorBackground = sharedPrefManager.indicatorBackgroundColor
        setViewTint(binding.ivCam, dotsTint)
        setViewTint(binding.ivMic, dotsTint)
        setViewTint(binding.ivLoc, dotsTint)
        binding.llBackground.setBackgroundColor(Color.parseColor(indicatorBackground))
    }

    private fun setViewTint(imageView: ImageView, hex: String) {
        imageView.setColorFilter(Color.parseColor(hex), PorterDuff.Mode.SRC_IN)
    }

    private fun updateIndicatorsOpacity() {
        binding.root.updateOpacity(sharedPrefManager.indicatorOpacity.opacity)
    }

    private fun updateIndicatorsSize() {
        val size = sharedPrefManager.indicatorSize.size
        binding.cvIndicators.radius = (size/2).toFloat()
        binding.ivCam.updateSize(size)
        binding.ivMic.updateSize(size)
        binding.ivLoc.updateSize(size)
    }

    fun upScaleView(view: View) {
        val fade_in = ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        fade_in.duration = 350
        fade_in.fillAfter = true
        view.startAnimation(fade_in)
    }

    fun downScaleView(view: View) {
        val fade_in = ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        fade_in.duration = 350
        fade_in.fillAfter = true
        view.startAnimation(fade_in)
    }

    private fun setupNotification() {
        createNotificationChannel()
        notificationBuilder = NotificationCompat.Builder(applicationContext, notification_channel_id)
                .setSmallIcon(R.drawable.camera_indicator2)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        notifManager = NotificationManagerCompat.from(applicationContext)
    }

    private val notificationTitle: String
        get() {
            if (isCameraOn && isMicOn) return "Your Camera and Mic is ON"
            if (isCameraOn && !isMicOn) return "Your Camera is ON"
            return if (!isCameraOn && isMicOn) "Your MIC is ON" else "Your Camera or Mic is ON"
        }
    private val notificationDescription: String
        get() {
            if (isCameraOn && isMicOn) return "A third-party app is using your Camera and Microphone"
            if (isCameraOn && !isMicOn) return "A third-party app is using your Camera"
            return if (!isCameraOn && isMicOn) "A third-party app is using your Microphone" else "A third-party app is using your Camera or Microphone"
        }

    private fun showNotification() {
        if (sharedPrefManager.isNotificationEnabled) {
            setupNotification()
            if (notifManager != null) notifManager!!.notify(notificationID, notificationBuilder!!.build())
        }
    }

    private fun dismissNotification() {
        if (isCameraOn || isMicOn) {
            showNotification()
        } else {
            if (notifManager != null) notifManager!!.cancel(notificationID)
        }
    }

    private val pendingIntent: PendingIntent
        get() {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            return PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

    private fun createNotificationChannel() {
        val notificationChannel = "Notifications for Privacy Indicators"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(notification_channel_id, notificationChannel, importance)
            val description = getString(R.string.notification_alert_summary)
            channel.description = description
            channel.lightColor = Color.RED
            val notificationManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onInterrupt() {}
    override fun onAccessibilityEvent(accessibilityEvent: AccessibilityEvent) {}
    private fun unRegisterCameraCallBack() {
        if (cameraManager != null
                && cameraCallback != null) {
            cameraManager!!.unregisterAvailabilityCallback(cameraCallback!!)
        }
    }

    private fun unRegisterMicCallback() {
        if (audioManager != null
                && micCallback != null) {
            audioManager!!.unregisterAudioRecordingCallback(micCallback!!)
        }
    }

    override fun onDestroy() {
        unRegisterCameraCallBack()
        unRegisterMicCallback()
        super.onDestroy()
    }
}