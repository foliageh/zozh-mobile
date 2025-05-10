package com.rmpteam.zozh.data.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: String,
    var username: String,
    var password: String,
    var weight: Float? = null,
    var height: Int? = null,
    var gender: Gender? = null,
    var age: Int? = null,
    var goal: WeightGoal? = null
)

enum class Gender {
    MALE, FEMALE
}

enum class WeightGoal {
    LOSE_WEIGHT,
    GAIN_WEIGHT,
    MAINTAIN_WEIGHT
} 