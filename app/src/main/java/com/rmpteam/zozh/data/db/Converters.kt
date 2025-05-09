package com.rmpteam.zozh.data.db

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    @TypeConverter
    fun toLocalDateTime(value: LocalDateTime?): Long? {
        return value?.atZone(ZoneId.systemDefault())?.toEpochSecond()
    }

    @TypeConverter
    fun fromLocalDateTime(value: Long?): LocalDateTime? {
        return value?.let {
            Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneId.systemDefault())?.toEpochSecond()
    }
}