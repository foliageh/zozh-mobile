package com.rmpteam.zozh.util

import java.time.LocalDateTime
import java.time.LocalTime

fun LocalTime.timeString() : String {
    return this.toString().substring(0, 5)
}

fun LocalDateTime.timeString() : String {
    return this.toLocalTime().toString().substring(0, 5)
}