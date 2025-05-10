package com.rmpteam.zozh.ui.settings

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
fun SettingsScreen(
    onBackClick: () -> Unit,
    userRepository: UserRepository,
    onLogout: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf<UserProfile?>(null) }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<Gender?>(null) }
    var selectedGoal by remember { mutableStateOf<WeightGoal?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        userRepository.getCurrentUser().collect { user ->
            if (user != null) {
                currentUser = user
                weight = user.weight?.toString() ?: ""
                height = user.height?.toString() ?: ""
                age = user.age?.toString() ?: ""
                selectedGender = user.gender
                selectedGoal = user.goal
            }
        }
    }
    
    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Настройки",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
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
                    label = { Text(when (gender) {
                        Gender.MALE -> "Мужской"
                        Gender.FEMALE -> "Женский"
                    }) }
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
                            WeightGoal.MAINTAIN_WEIGHT -> "Поддерживать вес"
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Назад")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    try {
                        val user = currentUser ?: return@Button
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
                            onBackClick()
                        }
                    } catch (e: Exception) {
                        errorMessage = "Пожалуйста, проверьте правильность введенных данных"
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Сохранить")
            }
        }
        
        // Logout button
        OutlinedButton(
            onClick = { 
                scope.launch {
                    userRepository.logout()
                    onLogout()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Выйти")
        }
    }
} 