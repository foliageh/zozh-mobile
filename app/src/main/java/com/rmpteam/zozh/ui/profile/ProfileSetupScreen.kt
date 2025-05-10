package com.rmpteam.zozh.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rmpteam.zozh.data.Gender
import com.rmpteam.zozh.data.MockUserRepository
import com.rmpteam.zozh.data.UserProfile
import com.rmpteam.zozh.data.WeightGoal

@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    userRepository: MockUserRepository
) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var selectedGoal by remember { mutableStateOf<WeightGoal?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Настройка профиля",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Для начала работы с приложением необходимо заполнить данные профиля",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            label = { Text("Вес (кг)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Рост (см)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Возраст") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Пол",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Gender.values().forEach { gender ->
                FilterChip(
                    selected = selectedGender == gender,
                    onClick = { selectedGender = gender },
                    label = { 
                        Text(
                            when (gender) {
                                Gender.MALE -> "Мужской"
                                Gender.FEMALE -> "Женский"
                            }
                        ) 
                    }
                )
            }
        }

        Text(
            text = "Цель",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            WeightGoal.values().forEach { goal ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedGoal == goal,
                        onClick = { selectedGoal = goal }
                    )
                    Text(
                        text = when (goal) {
                            WeightGoal.LOSE_WEIGHT -> "Похудеть"
                            WeightGoal.GAIN_WEIGHT -> "Набрать вес"
                            WeightGoal.MAINTAIN_WEIGHT -> "Сохранить вес"
                        }
                    )
                }
            }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                val currentUser = userRepository.getCurrentUser() ?: return@Button
                
                try {
                    val updatedProfile = currentUser.copy(
                        weight = weight.toFloatOrNull(),
                        height = height.toIntOrNull(),
                        age = age.toIntOrNull(),
                        gender = selectedGender,
                        goal = selectedGoal
                    )
                    
                    if (updatedProfile.weight == null || updatedProfile.height == null ||
                        updatedProfile.age == null || updatedProfile.gender == null ||
                        updatedProfile.goal == null) {
                        errorMessage = "Пожалуйста, заполните все поля"
                        return@Button
                    }
                    
                    userRepository.updateProfile(updatedProfile)
                    onSetupComplete()
                } catch (e: Exception) {
                    errorMessage = "Пожалуйста, проверьте правильность введенных данных"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Продолжить")
        }
    }
} 