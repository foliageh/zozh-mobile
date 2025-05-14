package com.rmpteam.zozh.data.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rmpteam.zozh.util.DateTimeUtil
import java.time.ZonedDateTime

@Entity
data class Meal(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String = "",
    val dateTime: ZonedDateTime = DateTimeUtil.now(),
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
)