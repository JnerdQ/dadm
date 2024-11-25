package com.example.juegotriqui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.juegotriqui.ui.theme.TicTacToeGame

class MainActivity : AppCompatActivity() {

    private val game = TicTacToeGame()
    private lateinit var buttons: Array<Button>
    private lateinit var gameMessage: TextView

    private var currentPlayer = "X"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = arrayOf(
            findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3),
            findViewById(R.id.button4), findViewById(R.id.button5), findViewById(R.id.button6),
            findViewById(R.id.button7), findViewById(R.id.button8), findViewById(R.id.button9)
        )

        gameMessage = findViewById(R.id.gameMessage)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handlePlayerMove(button, index)
            }
        }

        resetGame()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_game -> {
                resetGame()
                true
            }
            R.id.ai_difficulty -> {
                showDifficultyDialog()
                true
            }
            R.id.quit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handlePlayerMove(button: Button, index: Int) {
        val row = index / 3
        val col = index % 3

        if (game.makeMove(currentPlayer, row, col)) {
            // Establecer el texto y el color basado en el jugador actual
            button.text = currentPlayer
            button.setTextColor(
                if (currentPlayer == "X")
                    resources.getColor(R.color.colorX, theme)
                else
                    resources.getColor(R.color.colorO, theme)
            )

            if (game.isWinner(currentPlayer)) {
                gameMessage.text = "¡Jugador $currentPlayer ganó!"
                disableButtons()
            } else if (isBoardFull()) {
                gameMessage.text = "¡Empate!"
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                if (currentPlayer == "O") {
                    // Movimiento de la máquina
                    val (aiRow, aiCol) = game.getComputerMove()
                    val aiButton = buttons[aiRow * 3 + aiCol]
                    game.makeMove(currentPlayer, aiRow, aiCol)
                    aiButton.text = currentPlayer
                    aiButton.setTextColor(resources.getColor(R.color.colorO, theme))

                    if (game.isWinner("O")) {
                        gameMessage.text = "¡La máquina ganó!"
                        disableButtons()
                    }
                    currentPlayer = "X"
                }
            }
        }
    }


    private fun resetGame() {
        game.resetGame()
        buttons.forEach { button ->
            button.text = ""
            button.isEnabled = true
        }
        currentPlayer = "X"
        gameMessage.text = "Turno de $currentPlayer"
    }

    private fun isBoardFull(): Boolean {
        return game.getBoard().all { row -> row.all { it.isNotEmpty() } }
    }

    private fun disableButtons() {
        buttons.forEach { it.isEnabled = false }
    }

    private fun showDifficultyDialog() {
        val levels = TicTacToeGame.DifficultyLevel.values()
        val selected = levels.indexOf(game.getDifficultyLevel())

        AlertDialog.Builder(this)
            .setTitle("Selecciona la dificultad")
            .setSingleChoiceItems(levels.map { it.name }.toTypedArray(), selected) { dialog, which ->
                game.setDifficultyLevel(levels[which])
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
