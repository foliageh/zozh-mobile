package com.rmpteam.zozh.data.sleep

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rmpteam.zozh.util.DateTimeUtil
import java.time.ZonedDateTime

//@Entity
data class Sleep(
    //@PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: ZonedDateTime = DateTimeUtil.now().minusHours(6),
    val endTime: ZonedDateTime = DateTimeUtil.now(),
    val quality: SleepQuality = SleepQuality.GOOD,
    val notes: String = ""
)

enum class SleepQuality {
    POOR, FAIR, GOOD, EXCELLENT
}