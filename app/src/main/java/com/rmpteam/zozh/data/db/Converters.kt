package com.rmpteam.zozh.data.db

import androidx.room.TypeConverter
import com.rmpteam.zozh.util.DateTimeUtil.epochMilliToDateTime
import com.rmpteam.zozh.util.DateTimeUtil.toEpochMilli
import java.time.ZonedDateTime

class Converters {
    @TypeConverter
    fun fromZonedDateTime(dateTime: ZonedDateTime?): Long? {
        return dateTime?.toEpochMilli()?.div(1000)
    }

    @TypeConverter
    fun toZonedDateTime(epochSecond: Long?): ZonedDateTime? {
        return epochSecond?.let { epochMilliToDateTime(it * 1000) }
    }
}