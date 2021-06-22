package com.nitish.privacyindicator.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nitish.privacyindicator.models.AccessLog

@Dao
interface AccessLogsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(accessLog: AccessLog): Long

    @Query("SELECT * FROM access_logs ORDER BY time DESC")
    fun getAllLogs(): LiveData<List<AccessLog>>

    @Query("DELETE FROM access_logs")
    suspend fun clearLogs()
}