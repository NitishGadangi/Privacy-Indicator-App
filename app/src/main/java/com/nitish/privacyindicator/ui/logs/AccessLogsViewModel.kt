package com.nitish.privacyindicator.ui.logs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nitish.privacyindicator.repository.AccessLogsRepo
import kotlinx.coroutines.launch

class AccessLogsViewModel(app: Application, private val accessLogsRepo: AccessLogsRepo): AndroidViewModel(app) {

    val allAccessLogs = accessLogsRepo.fetchAll()

    fun clearAllLogs() = viewModelScope.launch {
        accessLogsRepo.clear()
    }

}