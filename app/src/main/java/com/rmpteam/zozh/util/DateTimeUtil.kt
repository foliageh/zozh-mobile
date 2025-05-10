package com.rmpteam.zozh.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

object DateTimeUtil {
    fun now(): ZonedDateTime {
        return ZonedDateTime.now()
    }

    fun epochMilliToDateTime(epochMilli: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault())
    }

    fun ZonedDateTime.toEpochMilli(): Long {
        return this.toInstant().toEpochMilli()
    }

    fun ZonedDateTime.zoneOffsetMilli(): Long {
        return this.offset.totalSeconds * 1000L
    }

    fun ZonedDateTime.startOfDay(): ZonedDateTime {
        return this.truncatedTo(ChronoUnit.DAYS)
    }

    fun ZonedDateTime.endOfDay(): ZonedDateTime {
        return this.withHour(23).withMinute(59).withSecond(59).withNano(999999999)
    }

    fun ZonedDateTime.dateString(): String {
        return this.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT))
    }

    fun ZonedDateTime.timeString(): String {
        return this.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT))
    }

    fun ZonedDateTime.dateTimeString(): String {
        return this.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
    }
}