package com.rmpteam.zozh.util

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
