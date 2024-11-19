    package com.example.juegotriqui

    import android.os.Bundle
    import android.widget.Button
    import android.widget.TextView
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity

    class MainActivity : AppCompatActivity() {

        private var currentPlayer = "X"
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
                    handleMove(button, index)
                }
            }

            restartButton.setOnClickListener {
                resetGame()
            }
        }

        private fun handleMove(button: Button, index: Int) {
            val row = index / 3
            val col = index % 3

            if (board[row][col].isNotEmpty()) {
                gameMessage.text = "Casilla ocupada, elige otra"
                return
            }

            board[row][col] = currentPlayer
            button.text = currentPlayer

            // Cambiar color según el jugador
            button.setTextColor(
                if (currentPlayer == "X") resources.getColor(R.color.colorX, theme)
                else resources.getColor(R.color.colorO, theme)
            )

            if (checkWinner()) {
                gameMessage.text = "¡Jugador $currentPlayer ganó!"
                fillRemainingSpaces()
                showRestartButton()
            } else if (isBoardFull()) {
                gameMessage.text = "¡Empate!"
                showRestartButton()
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
                gameMessage.text = "Turno de $currentPlayer"
            }
        }

        private fun fillRemainingSpaces() {
            // Llenar las casillas vacías
            board.forEachIndexed { rowIndex, row ->
                row.forEachIndexed { colIndex, cell ->
                    if (cell.isEmpty()) {
                        board[rowIndex][colIndex] = currentPlayer
                        val button = buttons[rowIndex * 3 + colIndex]
                        button.text = currentPlayer
                        button.setTextColor(resources.getColor(android.R.color.darker_gray, theme))
                    }
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
            gameMessage.text = "Turno de $currentPlayer"
            restartButton.visibility = Button.GONE
        }
    }

