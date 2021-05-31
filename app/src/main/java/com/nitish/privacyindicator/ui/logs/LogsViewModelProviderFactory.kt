package com.nitish.privacyindicator.ui.logs

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nitish.privacyindicator.repository.AccessLogsRepo
import com.nitish.privacyindicator.repository.SharedPrefManager
import com.nitish.privacyindicator.ui.home.HomeViewModel

class LogsViewModelProviderFactory(
        private val application: Application,
        private val accessLogsRepo: AccessLogsRepo
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AccessLogsViewModel(application, accessLogsRepo) as T
    }
}