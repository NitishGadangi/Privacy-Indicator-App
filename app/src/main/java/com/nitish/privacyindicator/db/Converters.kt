package com.nitish.privacyindicator.db

import androidx.room.TypeConverter
import com.nitish.privacyindicator.models.IndicatorType

class Converters {

    @TypeConverter
    fun fromIndicatorType(indicatorType: IndicatorType): String {
        return indicatorType.name
    }

    @TypeConverter
    fun toIndicatorType(indicatorType: String): IndicatorType {
        return IndicatorType.valueOf(indicatorType)
    }
}