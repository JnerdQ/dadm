package com.example.juegotriqui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu) // Asigna el diseño correcto

        val playWithAIButton: Button = findViewById(R.id.playWithAIButton)
        val playOnlineButton: Button = findViewById(R.id.playOnlineButton)

        // Navegación al modo de juego contra la máquina
        playWithAIButton.setOnClickListener {
            val intent = Intent(this, MainActivityAI::class.java)
            startActivity(intent)
        }

        // Navegación al lobby de juegos en línea
        playOnlineButton.setOnClickListener {
            val intent = Intent(this, GameLobbyActivity::class.java)
            startActivity(intent)
        }
    }
}
