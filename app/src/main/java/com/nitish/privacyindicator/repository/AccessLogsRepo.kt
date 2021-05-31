package com.nitish.privacyindicator.repository

import com.nitish.privacyindicator.db.AccessLogsDatabase
import com.nitish.privacyindicator.models.AccessLog

class AccessLogsRepo(
        val db: AccessLogsDatabase
) {
    suspend fun save(accessLog: AccessLog) = db.getAccessLogsDao().upsert(accessLog)

    suspend fun clear() = db.getAccessLogsDao().clearLogs()

    fun fetchAll() = db.getAccessLogsDao().getAllLogs()
}