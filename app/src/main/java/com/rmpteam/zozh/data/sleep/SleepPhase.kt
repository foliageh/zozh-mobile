package com.rmpteam.zozh.data.sleep

import java.time.ZonedDateTime

data class SleepPhase(
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val type: SleepPhaseType
)

enum class SleepPhaseType {
    LIGHT, DEEP, REM
}