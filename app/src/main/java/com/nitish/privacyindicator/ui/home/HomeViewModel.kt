package com.nitish.privacyindicator.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.nitish.privacyindicator.models.IndicatorOpacity
import com.nitish.privacyindicator.models.IndicatorPosition
import com.nitish.privacyindicator.models.IndicatorSize
import com.nitish.privacyindicator.repository.SharedPrefManager

class HomeViewModel(application: Application,
                    private val sharedPrefManager: SharedPrefManager) : AndroidViewModel(application) {

    val cameraIndicatorStatus = MutableLiveData(sharedPrefManager.isCameraIndicatorEnabled)

    val microphoneIndicatorStatus = MutableLiveData(sharedPrefManager.isMicIndicatorEnabled)

    val locationIndicatorStatus = MutableLiveData(sharedPrefManager.isLocationEnabled)

    val vibrationAlertStatus = MutableLiveData(sharedPrefManager.isVibrationEnabled)

    val notificationAlertStatus = MutableLiveData(sharedPrefManager.isNotificationEnabled)

    val indicatorForegroundColor = MutableLiveData(sharedPrefManager.indicatorColor)

    val indicatorBackgroundColor = MutableLiveData(sharedPrefManager.indicatorBackgroundColor)

    val indicatorPosition = MutableLiveData(sharedPrefManager.indicatorPosition)

    val indicatorSize = MutableLiveData(sharedPrefManager.indicatorSize)

    val indicatorOpacity = MutableLiveData(sharedPrefManager.indicatorOpacity)

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

    fun setIndicatorForegroundColor(color: String) {
        sharedPrefManager.indicatorColor = color
        indicatorForegroundColor.value = color
    }

    fun setIndicatorBackgroundColor(color: String) {
        sharedPrefManager.indicatorBackgroundColor = color
        indicatorBackgroundColor.value = color
    }

    fun setIndicatorPosition(position: IndicatorPosition) {
        sharedPrefManager.indicatorPosition = position
        indicatorPosition.value = position
    }

    fun setIndicatorSize(size: IndicatorSize) {
        sharedPrefManager.indicatorSize = size
        indicatorSize.value = size
    }

    fun setIndicatorOpacity(opacity: IndicatorOpacity) {
        sharedPrefManager.indicatorOpacity = opacity
        indicatorOpacity.value = opacity
    }

    companion object {
        const val GITHUB_REPO = "https://github.com/NitishGadangi/Privacy-Indicator-App/"
        const val GITHUB_PROFILE = "https://github.com/NitishGadangi"
        const val LINKEDIN = "https://www.linkedin.com/in/nitish-gadangi/"
        const val FOSSHACKS = "https://forum.fossunited.org/t/foss-hack-2020-results/424"
        const val SHARING_TEXT = "Checkout this app which provides you the Privacy Features of iOS 14 and Android 12 on your device. https://play.google.com/store/apps/details?id=com.nitish.privacyindicator"
    }

}