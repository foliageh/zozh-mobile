package com.rmpteam.zozh.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rmpteam.zozh.data.user.Gender
import com.rmpteam.zozh.data.user.UserProfile
import com.rmpteam.zozh.data.user.UserRepository
import com.rmpteam.zozh.data.user.WeightGoal
import kotlinx.coroutines.launch

@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    userRepository: UserRepository
) {
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var selectedGoal by remember { mutableStateOf<WeightGoal?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(Unit) {
        userRepository.getCurrentUser().collect {
            currentUser = it
            // Pre-fill form with existing data if available
            it?.let { user ->
                user.weight?.let { weight = it.toString() }
                user.height?.let { height = it.toString() }
                user.age?.let { age = it.toString() }
                selectedGender = user.gender
                selectedGoal = user.goal
            }
        }
    }

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

        // Gender selection
        Text(
            text = "Пол",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Gender.values().forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    RadioButton(
                        selected = selectedGender == gender,
                        onClick = { selectedGender = gender }
                    )
                    Text(
                        text = when (gender) {
                            Gender.MALE -> "Мужской"
                            Gender.FEMALE -> "Женский"
                        }
                    )
                }
            }
        }

        // Weight goal selection
        Text(
            text = "Цель",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            WeightGoal.values().forEach { goal ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    RadioButton(
                        selected = selectedGoal == goal,
                        onClick = { selectedGoal = goal }
                    )
                    Text(
                        text = when (goal) {
                            WeightGoal.LOSE_WEIGHT -> "Похудеть"
                            WeightGoal.MAINTAIN_WEIGHT -> "Поддерживать вес"
                            WeightGoal.GAIN_WEIGHT -> "Набрать вес"
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
                val user = currentUser ?: return@Button
                
                try {
                    val updatedProfile = user.copy(
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
                    
                    scope.launch {
                        userRepository.updateUser(updatedProfile)
                        userRepository.setCurrentUser(updatedProfile)
                        onSetupComplete()
                    }
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