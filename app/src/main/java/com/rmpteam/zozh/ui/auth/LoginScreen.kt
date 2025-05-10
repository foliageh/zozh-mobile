package com.rmpteam.zozh.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rmpteam.zozh.di.AppViewModelProvider

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginSuccessNavigation: (requiresProfileSetup: Boolean) -> Unit,
    onRegisterClick: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate on login success
    LaunchedEffect(uiState.loginSucceeded, uiState.requiresProfileSetup) {
        if (uiState.loginSucceeded && uiState.requiresProfileSetup != null) {
            onLoginSuccessNavigation(uiState.requiresProfileSetup!!)
            viewModel.onLoginNavigationHandled() // Reset the event
        }
    }
    
    // Check if user is already logged in (e.g. from previous session via DataStore)
    // This initial check should ideally happen before navigating to LoginScreen,
    // typically in a higher-level navigator or splash screen.
    // For now, if UserRepository indicates a user is already logged in when LoginScreen is shown,
    // it might imply an issue with navigation logic or that this check is redundant here
    // if AppNavGraph already handles it.
    // If we keep a check here, it should use the ViewModel that observes UserRepository.
    // However, LoginViewModel itself doesn't have a direct currentUser state, it acts on login attempts.
    // This was previously: userRepository.getCurrentUser().collect { if (it != null) onLoginSuccess() }
    // This kind of check is now implicitly handled by the navigation graph listening to userRepository.getCurrentUser()

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
            isError = uiState.errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            isError = uiState.errorMessage != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 16.dp))
        } else {
            Button(
                onClick = { viewModel.loginUser() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Войти")
            }
        }

        TextButton(
            onClick = onRegisterClick,
            enabled = !uiState.isLoading
        ) {
            Text("Зарегистрироваться")
        }
    }
} 