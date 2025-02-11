package com.dadm.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.dadm.ai.ui.theme.AiTheme


import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dadm.ai.ui.theme.AiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiTheme {
                // Creamos el controlador de navegación
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Configuramos la navegación
                    NavHost(
                        navController = navController,
                        startDestination = "welcome",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Definimos la ruta para la pantalla de bienvenida
                        composable("welcome") {
                            WelcomeScreen(
                                onNavigateToGemini = {
                                    navController.navigate("gemini")
                                }
                            )
                        }

                        // Definimos la ruta para GeminiScreen
                        composable("gemini") {
                            GeminiScreen()
                        }
                    }
                }
            }
        }
    }
}