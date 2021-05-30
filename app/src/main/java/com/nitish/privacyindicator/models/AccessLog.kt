package com.nitish.privacyindicator.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
        tableName = "access_logs"
)
data class AccessLog(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val time: Long,
        val appId: String,
        val indicatorType: IndicatorType
) {
    constructor(time: Long, appId: String, indicatorType: IndicatorType) : this(0, time, appId, indicatorType)
}
