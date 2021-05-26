package com.nitish.privacyindicator.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nitish.privacyindicator.repository.SharedPrefManager

class ViewModelProviderFactory(
        private val application: Application,
        private val sharedPrefManager: SharedPrefManager
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(application, sharedPrefManager) as T
    }
}