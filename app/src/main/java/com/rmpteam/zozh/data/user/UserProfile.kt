package com.rmpteam.zozh.data.user

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    var username: String,
    var password: String, // Consider if password should be stored/transmitted like this long-term
    var weight: Float? = null,
    var height: Int? = null,
    var gender: Gender? = null,
    var age: Int? = null,
    var goal: WeightGoal? = null
)

@Serializable
enum class Gender {
    MALE, FEMALE
}

@Serializable
enum class WeightGoal {
    LOSE_WEIGHT,
    GAIN_WEIGHT,
    MAINTAIN_WEIGHT
} 