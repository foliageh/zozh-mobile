package com.rmpteam.zozh.data.user

import com.rmpteam.zozh.util.ValidationResult
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
    var goal: WeightGoal? = null,
    var calories: Int? = null
) {
    fun isProfileComplete(): Boolean {
        val weight = weight; val height = height; val age = age
        return weight != null && weight > 0
                && height != null && height > 0
                && gender != null
                && age != null && age > 0
                && goal != null
    }

    fun validate(): ValidationResult {
        val weight = weight; val height = height; val age = age
        if (username.isBlank())
            return ValidationResult(false, "Логин не может быть пустым")
        if (password.isBlank())
            return ValidationResult(false, "Пароль не может быть пустым")
        if (weight == null || weight <= 0)
            return ValidationResult(false, "Неккорректное значение веса")
        if (height == null || height <= 0)
            return ValidationResult(false, "Неккорректное значение роста")
        if (gender == null)
            return ValidationResult(false, "Пожалуйста, выберите пол")
        if (age == null || age <= 0)
            return ValidationResult(false, "Неккорректное значение возраста")
        if (goal == null)
            return ValidationResult(false, "Пожалуйста, выберите цель")
        return ValidationResult(true)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as UserProfile
        return username == other.username
    }

    override fun hashCode(): Int {
        return username.hashCode()
    }
}

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
