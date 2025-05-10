package com.rmpteam.zozh.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.data.user.Gender
import com.rmpteam.zozh.data.user.WeightGoal
import com.rmpteam.zozh.di.AppViewModelProvider

@Composable
fun ProfileSetupScreen(
    modifier: Modifier = Modifier,
    onSetupComplete: () -> Unit
) {
    val viewModel: ProfileSetupViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = modifier
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
            value = uiState.weight,
            onValueChange = { viewModel.updateWeight(it) },
            label = { Text("Вес (кг)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.height,
            onValueChange = { viewModel.updateHeight(it) },
            label = { Text("Рост (см)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.age,
            onValueChange = { viewModel.updateAge(it) },
            label = { Text("Возраст") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

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
                        selected = uiState.selectedGender == gender,
                        onClick = { viewModel.updateSelectedGender(gender) }
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
                        selected = uiState.selectedGoal == goal,
                        onClick = { viewModel.updateSelectedGoal(goal) }
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

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { viewModel.saveProfile(onSuccess = onSetupComplete) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Продолжить")
        }
    }
} 