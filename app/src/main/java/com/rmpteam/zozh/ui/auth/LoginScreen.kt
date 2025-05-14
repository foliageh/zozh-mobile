package com.rmpteam.zozh.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.di.AppViewModelProvider

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccessNavigation: (requiresProfileSetup: Boolean) -> Unit,
    onRegisterClick: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState = viewModel.uiState
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вход",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = uiState.username,
            onValueChange = { viewModel.updateUsername(it) },
            label = { Text("Логин") },
            isError = !uiState.validationResult.isValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            isError = !uiState.validationResult.isValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (!uiState.validationResult.isValid) {
            uiState.validationResult.errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        } else {
            Button(
                onClick = {
                    viewModel.loginUser()
                    if (uiState.loginSucceeded)
                        onLoginSuccessNavigation(uiState.requiresProfileSetup)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Войти")
            }
            TextButton(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Зарегистрироваться")
            }
        }
    }
}