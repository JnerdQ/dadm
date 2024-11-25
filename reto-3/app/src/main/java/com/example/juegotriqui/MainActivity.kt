package com.example.juegotriqui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var currentPlayer = "X" // El jugador siempre será "X", la máquina será "O"
    private val board = Array(3) { Array(3) { "" } }
    private lateinit var buttons: Array<Button>
    private lateinit var restartButton: Button
    private lateinit var gameMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttons = arrayOf(
            findViewById(R.id.button1), findViewById(R.id.button2), findViewById(R.id.button3),
            findViewById(R.id.button4), findViewById(R.id.button5), findViewById(R.id.button6),
            findViewById(R.id.button7), findViewById(R.id.button8), findViewById(R.id.button9)
        )

        restartButton = findViewById(R.id.restartButton)
        gameMessage = findViewById(R.id.gameMessage)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                handlePlayerMove(button, index)
            }
        }

        restartButton.setOnClickListener {
            resetGame()
        }
    }

    private fun handlePlayerMove(button: Button, index: Int) {
        val row = index / 3
        val col = index % 3

        if (board[row][col].isNotEmpty()) {
            gameMessage.text = "Casilla ocupada, elige otra"
            return
        }

        board[row][col] = currentPlayer
        button.text = currentPlayer
        button.setTextColor(resources.getColor(R.color.colorX, theme))

        if (checkWinner()) {
            gameMessage.text = "¡Jugador ganó!"
            showRestartButton()
        } else if (isBoardFull()) {
            gameMessage.text = "¡Empate!"
            showRestartButton()
        } else {
            currentPlayer = "O"
            gameMessage.text = "Turno de la máquina"
            handleMachineMove() // Llamada al movimiento de la máquina
        }
    }

    private fun handleMachineMove() {
        val emptySpaces = mutableListOf<Pair<Int, Int>>()

        // Encontrar todas las casillas vacías
        for (i in 0..2) {
            for (j in 0..2) {
                if (board[i][j].isEmpty()) {
                    emptySpaces.add(Pair(i, j))
                }
            }
        }

        if (emptySpaces.isNotEmpty()) {
            val (row, col) = emptySpaces[Random.nextInt(emptySpaces.size)] // Elegir una casilla aleatoria
            board[row][col] = currentPlayer
            val buttonIndex = row * 3 + col
            val button = buttons[buttonIndex]

            button.text = currentPlayer
            button.setTextColor(resources.getColor(R.color.colorO, theme))

            if (checkWinner()) {
                gameMessage.text = "¡La máquina ganó!"
                showRestartButton()
            } else if (isBoardFull()) {
                gameMessage.text = "¡Empate!"
                showRestartButton()
            } else {
                currentPlayer = "X"
                gameMessage.text = "Turno del jugador"
            }
        }
    }

    private fun checkWinner(): Boolean {
        for (i in 0..2) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) return true
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) return true
        }
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) return true
        if (board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer) return true

        return false
    }

    private fun isBoardFull(): Boolean {
        return board.all { row -> row.all { it.isNotEmpty() } }
    }

    private fun showRestartButton() {
        restartButton.visibility = Button.VISIBLE
        buttons.forEach { it.isEnabled = false }
    }

    private fun resetGame() {
        board.forEach { row -> row.fill("") }
        buttons.forEach { button ->
            button.text = ""
            button.isEnabled = true
        }
        currentPlayer = "X"
        gameMessage.text = "Turno del jugador"
        restartButton.visibility = Button.GONE
    }
}
