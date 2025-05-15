package com.rmpteam.zozh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rmpteam.zozh.ui.ZOZHApp
import com.rmpteam.zozh.ui.theme.ZOZHTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash screen visible while MainViewModel determines the start route.
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value.isLoading
        }

        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()

            // Only compose the main app UI once the start route is determined
            if (!uiState.isLoading && uiState.startScreen != null) {
                ZOZHTheme {
                    ZOZHApp(startScreen = uiState.startScreen!!)
                }
            }
        }
    }
}