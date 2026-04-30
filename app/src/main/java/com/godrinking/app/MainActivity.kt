package com.godrinking.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.godrinking.app.ui.screens.GodrinkingApp
import com.godrinking.app.ui.theme.GoDrinkingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // O tema pode ser alternado dinamicamente pela SettingsScreen
            var isDarkTheme by remember { mutableStateOf(true) }

            GoDrinkingTheme(darkTheme = isDarkTheme) {
                GodrinkingApp(
                    onThemeChange = { theme -> isDarkTheme = (theme == "dark") }
                )
            }
        }
    }
}
