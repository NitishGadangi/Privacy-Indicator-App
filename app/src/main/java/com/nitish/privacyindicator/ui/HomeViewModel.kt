package com.nitish.privacyindicator.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nitish.privacyindicator.repository.SharedPrefManager

class HomeViewModel(application: Application,
                    private val sharedPrefManager: SharedPrefManager) : AndroidViewModel(application) {

    val cameraIndicatorStatus = MutableLiveData(sharedPrefManager.isCameraIndicatorEnabled)

    val microphoneIndicatorStatus = MutableLiveData(sharedPrefManager.isMicIndicatorEnabled)

    val locationIndicatorStatus = MutableLiveData(sharedPrefManager.isLocationEnabled)

    val vibrationAlertStatus = MutableLiveData(sharedPrefManager.isVibrationEnabled)

    val notificationAlertStatus = MutableLiveData(sharedPrefManager.isNotificationEnabled)

    fun setCameraIndicatorStatus(isEnabled: Boolean) {
        sharedPrefManager.isCameraIndicatorEnabled = isEnabled
        cameraIndicatorStatus.value = isEnabled
    }

    fun setMicrophoneIndicatorStatus(isEnabled: Boolean) {
        sharedPrefManager.isMicIndicatorEnabled = isEnabled
        microphoneIndicatorStatus.value = isEnabled
    }

    fun setLocationIndicatorStatus(isEnabled: Boolean) {
        sharedPrefManager.isLocationEnabled = isEnabled
        locationIndicatorStatus.value = isEnabled
    }

    fun setVibrationAlertStatus(isEnabled: Boolean) {
        sharedPrefManager.isVibrationEnabled = isEnabled
        vibrationAlertStatus.value = isEnabled
    }

    fun setNotificationAlertStatus(isEnabled: Boolean) {
        sharedPrefManager.isNotificationEnabled = isEnabled
        notificationAlertStatus.value = isEnabled
    }

    companion object {
        const val GITHUB_REPO = "https://github.com/NitishGadangi/Privacy-Indicator-App/"
        const val GITHUB_PROFILE = "https://github.com/NitishGadangi"
        const val LINKEDIN = "https://www.linkedin.com/in/nitish-gadangi/"
        const val SHARING_TEXT = "Checkout this app which provides you the Privacy Features of iOS 14 and Android 12 on your device. https://play.google.com/store/apps/details?id=com.nitish.privacyindicator"
    }

}