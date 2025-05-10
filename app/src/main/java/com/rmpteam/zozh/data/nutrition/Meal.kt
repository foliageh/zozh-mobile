package com.rmpteam.zozh.data.nutrition

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.ZonedDateTime

@Entity
data class Meal(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val name: String = "",
    val dateTime: ZonedDateTime,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
)
