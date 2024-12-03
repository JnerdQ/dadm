package com.example.juegotriqui

import android.annotation.SuppressLint
import android.media.MediaPlayer
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
    private var gameOver = false // Variable para bloquear jugadas después de terminar el juego

    // Variables para los sonidos
    private var playerMoveSound: MediaPlayer? = null
    private var aiMoveSound: MediaPlayer? = null
    private var winSound: MediaPlayer? = null
    private var loseSound: MediaPlayer? = null

    // Variable para manejar el mute
    private var isMuted = false // Indica si los sonidos están silenciados

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView = findViewById(R.id.board)
        boardView.setGame(game)
        boardView.initialize(R.drawable.x_image, R.drawable.o_image)

        gameMessage = findViewById(R.id.gameMessage)
        statsTextView = findViewById(R.id.stats)

        // Inicializar sonidos
        playerMoveSound = MediaPlayer.create(this, R.raw.player_move)
        aiMoveSound = MediaPlayer.create(this, R.raw.ai_move)
        winSound = MediaPlayer.create(this, R.raw.winning)
        loseSound = MediaPlayer.create(this, R.raw.lose)

        boardView.setOnTouchListener { _, event ->
            val col = (event.x / boardView.getCellWidth()).toInt()
            val row = (event.y / boardView.getCellHeight()).toInt()

            if (currentPlayer == "X" && game.getBoard()[row][col].isEmpty()) {
                handlePlayerMove(row, col)
                if (!gameOver && !game.isWinner("X") && !isBoardFull()) {
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
            R.id.mute_sounds -> {
                toggleMute(item)
                true
            }
            R.id.quit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleMute(item: MenuItem) {
        isMuted = !isMuted
        item.title = if (isMuted) "Activar Sonidos" else "Silenciar Sonidos"
    }

    private fun handlePlayerMove(row: Int, col: Int) {
        if (gameOver) return // No permitir movimientos si el juego ha terminado

        if (game.makeMove(currentPlayer, row, col)) {
            if (!isMuted) playerMoveSound?.start() // Reproducir sonido de movimiento del jugador
            if (game.isWinner(currentPlayer)) {
                gameMessage.text = "¡Jugador $currentPlayer ganó!"
                gameOver = true // Marcar el juego como terminado
                val handler = android.os.Handler()
                handler.postDelayed({
                    if (!isMuted) winSound?.start() // Reproducir sonido de victoria después de un delay
                }, 700)
                updateStats(winner = currentPlayer)
            } else if (isBoardFull()) {
                gameMessage.text = "¡Empate!"
                gameOver = true // Marcar el juego como terminado
                updateStats(winner = null)
            } else {
                currentPlayer = if (currentPlayer == "X") "O" else "X"
            }
            boardView.invalidate()
        }
    }

    private fun handleAIMove() {
        if (gameOver) return // No permitir movimientos si el juego ha terminado

        gameMessage.text = "La máquina está pensando..."
        val handler = android.os.Handler()
        handler.postDelayed({
            val (aiRow, aiCol) = game.getComputerMove()
            game.makeMove("O", aiRow, aiCol)
            if (!isMuted) aiMoveSound?.start() // Reproducir sonido de movimiento de la IA

            if (game.isWinner("O")) {
                gameMessage.text = "¡La máquina ganó!"
                gameOver = true // Marcar el juego como terminado
                handler.postDelayed({
                    if (!isMuted) loseSound?.start() // Reproducir sonido de derrota después de un delay
                }, 700)
                updateStats(winner = "O")
            } else if (isBoardFull()) {
                gameMessage.text = "¡Empate!"
                gameOver = true // Marcar el juego como terminado
                updateStats(winner = null)
            } else {
                currentPlayer = "X"
                gameMessage.text = "Turno del jugador $currentPlayer"
            }
            boardView.invalidate() // Actualizar el tablero
        }, 2000) // 2000 ms = 2 segundos
    }

    private fun resetGame() {
        game.resetGame()
        currentPlayer = "X"
        gameOver = false // Reiniciar el estado del juego
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

    override fun onDestroy() {
        super.onDestroy()
        // Liberar recursos de MediaPlayer
        playerMoveSound?.release()
        aiMoveSound?.release()
        winSound?.release()
        loseSound?.release()
    }
}
