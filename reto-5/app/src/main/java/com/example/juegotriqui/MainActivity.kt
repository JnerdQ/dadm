package com.example.juegotriqui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.juegotriqui.ui.theme.BoardView
import com.example.juegotriqui.ui.theme.TicTacToeGame

class MainActivity : AppCompatActivity() {
    private lateinit var boardView: BoardView
    private var userWins = 0
    private var aiWins = 0
    private var draws = 0

    private val game = TicTacToeGame()
    private lateinit var gameMessage: TextView
    private lateinit var statsTextView: TextView

    private var currentPlayer = "X"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView = findViewById(R.id.board)
        boardView.setGame(game)
        boardView.initialize(R.drawable.x_image, R.drawable.o_image)

        gameMessage = findViewById(R.id.gameMessage)
        statsTextView = findViewById(R.id.stats)

        boardView.setOnTouchListener { _, event ->
            val col = (event.x / boardView.getCellWidth()).toInt()
            val row = (event.y / boardView.getCellHeight()).toInt()

            if (currentPlayer == "X" && game.getBoard()[row][col].isEmpty()) {
                handlePlayerMove(row, col)
                if (!game.isWinner("X") && !isBoardFull()) {
                    handleAIMove()
                }
            }
            true
        }
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

    private fun handlePlayerMove(row: Int, col: Int) {
        if (game.makeMove(currentPlayer, row, col)) {
            if (game.isWinner(currentPlayer)) {
                gameMessage.text = "¡Jugador $currentPlayer ganó!"
                updateStats(winner = currentPlayer)
            } else if (isBoardFull()) {
                gameMessage.text = "¡Empate!"
                updateStats(winner = null)
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
            }
            boardView.invalidate()
        }
    }

    private fun handleAIMove() {
        val (aiRow, aiCol) = game.getComputerMove()
        game.makeMove("O", aiRow, aiCol)

        if (game.isWinner("O")) {
            gameMessage.text = "¡La máquina ganó!"
            updateStats(winner = "O")
        }
        currentPlayer = "X"
        boardView.invalidate()
    }

    private fun resetGame() {
        game.resetGame()
        currentPlayer = "X"
        gameMessage.text = "Turno de $currentPlayer"
        boardView.invalidate()
    }

    private fun isBoardFull(): Boolean {
        return game.getBoard().all { row -> row.all { it.isNotEmpty() } }
    }

    private fun updateStats(winner: String?) {
        when (winner) {
            "X" -> userWins++
            "O" -> aiWins++
            else -> draws++
        }
        statsTextView.text = "Victorias: Usuario $userWins | Máquina $aiWins | Empates $draws"
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
