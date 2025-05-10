package com.rmpteam.zozh.data

data class UserProfile(
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