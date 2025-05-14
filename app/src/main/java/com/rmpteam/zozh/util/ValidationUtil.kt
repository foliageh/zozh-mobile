package com.rmpteam.zozh.util

import com.rmpteam.zozh.data.user.Gender
import com.rmpteam.zozh.data.user.WeightGoal

object ValidationUtil {

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val message: String) : ValidationResult()
    }

    fun validateProfileData(
        weightStr: String,
        heightStr: String,
        ageStr: String,
        gender: Gender?,
        goal: WeightGoal?
    ): ValidationResult {
        val weightFloat = weightStr.toFloatOrNull()
        val heightInt = heightStr.toIntOrNull()
        val ageInt = ageStr.toIntOrNull()

        if (weightFloat == null || weightFloat <= 0) {
            return ValidationResult.Error("Пожалуйста, введите корректный вес")
        }
        if (heightInt == null || heightInt <= 0) {
            return ValidationResult.Error("Пожалуйста, введите корректный рост")
        }
        if (ageInt == null || ageInt <= 0) {
             return ValidationResult.Error("Пожалуйста, введите корректный возраст")
        }
        if (gender == null) {
            return ValidationResult.Error("Пожалуйста, выберите пол")
        }
        if (goal == null) {
            return ValidationResult.Error("Пожалуйста, выберите цель")
        }

        // Add more specific checks if needed (e.g., reasonable ranges for weight/height/age)

        return ValidationResult.Success
    }

    fun validateLoginCredentials(username: String, password: String): ValidationResult {
        if (username.isBlank()) {
            return ValidationResult.Error("Логин не может быть пустым")
        }
        if (password.isBlank()) {
            return ValidationResult.Error("Пароль не может быть пустым")
        }
        return ValidationResult.Success
    }

    fun validateRegistrationCredentials(username: String, password: String, confirmPassword: String): ValidationResult {
        if (username.isBlank()) {
            return ValidationResult.Error("Логин не может быть пустым")
        }
        if (password.isBlank()) {
            return ValidationResult.Error("Пароль не может быть пустым")
        }
        if (confirmPassword.isBlank()) {
             return ValidationResult.Error("Подтверждение пароля не может быть пустым")
        }
        if (password != confirmPassword) {
            return ValidationResult.Error("Пароли не совпадают")
        }
        // Add password complexity checks if needed
        return ValidationResult.Success
    }
} 