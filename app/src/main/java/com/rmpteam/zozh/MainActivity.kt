package com.rmpteam.zozh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rmpteam.zozh.ui.ZOZHApp
import com.rmpteam.zozh.ui.theme.ZOZHTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZOZHTheme {
                ZOZHApp()
            }
        }
    }
}