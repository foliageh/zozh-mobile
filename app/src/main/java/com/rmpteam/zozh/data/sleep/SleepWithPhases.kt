package com.rmpteam.zozh.data.sleep

data class SleepWithPhases(
    val sleep: Sleep,
    val phases: List<SleepPhase> = emptyList()
)