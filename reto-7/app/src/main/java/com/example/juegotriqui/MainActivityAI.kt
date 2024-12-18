package com.example.juegotriqui

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.juegotriqui.ui.theme.BoardView
import com.example.juegotriqui.ui.theme.TicTacToeGame

class MainActivityAI : AppCompatActivity() {
    private lateinit var boardView: BoardView
    private var userWins = 0
    private var aiWins = 0
    private var draws = 0
    private val handler = android.os.Handler()

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
    private lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)

        // Load persistent data
        userWins = sharedPreferences.getInt("userWins", 0)
        aiWins = sharedPreferences.getInt("aiWins", 0)
        draws = sharedPreferences.getInt("draws", 0)
        game.setDifficultyLevel(
            TicTacToeGame.DifficultyLevel.values()[sharedPreferences.getInt("difficultyLevel", 2)]
        )

        statsTextView.text = "Wins: User $userWins  |  Gipeto AI $aiWins  |  Draws $draws"

    }

    override fun onStop() {
        super.onStop()
        with(sharedPreferences.edit()) {
            putInt("userWins", userWins)
            putInt("aiWins", aiWins)
            putInt("draws", draws)
            putInt("difficultyLevel", game.getDifficultyLevel().ordinal)
            apply()
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
            R.id.quit_game -> { // Aquí cambias R.id.quit por R.id.quit_game
                finish() // Finaliza la actividad
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun toggleMute(item: MenuItem) {
        isMuted = !isMuted
        item.title = if (isMuted) "Enable sounds" else "Mute sounds"
    }

    private fun handlePlayerMove(row: Int, col: Int) {
        if (gameOver) return // No permitir movimientos si el juego ha terminado

        if (game.makeMove(currentPlayer, row, col)) {
            if (!isMuted) playerMoveSound?.start() // Reproducir sonido de movimiento del jugador
            if (game.isWinner(currentPlayer)) {
                gameMessage.text = "¡Player $currentPlayer wins!"
                gameOver = true // Marcar el juego como terminado
                val handler = android.os.Handler()
                handler.postDelayed({
                    if (!isMuted) winSound?.start() // Reproducir sonido de victoria después de un delay
                }, 700)
                updateStats(winner = currentPlayer)
            } else if (isBoardFull()) {
                gameMessage.text = "It's a Draw!"
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

        gameMessage.text = "Gipeto AI is thinking..."
        val handler = android.os.Handler()
        handler.postDelayed({
            val (aiRow, aiCol) = game.getComputerMove()
            game.makeMove("O", aiRow, aiCol)
            if (!isMuted) aiMoveSound?.start() // Reproducir sonido de movimiento de la IA

            if (game.isWinner("O")) {
                gameMessage.text = "¡Gipeto AI wins!"
                gameOver = true // Marcar el juego como terminado
                handler.postDelayed({
                    if (!isMuted) loseSound?.start() // Reproducir sonido de derrota después de un delay
                }, 700)
                updateStats(winner = "O")
            } else if (isBoardFull()) {
                gameMessage.text = "It's a Draw!"
                gameOver = true // Marcar el juego como terminado
                updateStats(winner = null)
            } else {
                currentPlayer = "X"
                gameMessage.text = "Turn player $currentPlayer"
            }
            boardView.invalidate() // Actualizar el tablero
        }, 2000) // 2000 ms = 2 segundos
    }

    private fun resetGame() {
        game.resetGame()
        currentPlayer = "X"
        gameOver = false // Reiniciar el estado del juego
        gameMessage.text = "Turn player $currentPlayer"
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
        statsTextView.text = "Wins: User $userWins  |  Gipeto AI $aiWins  |  Draws $draws"
    }

    private fun showDifficultyDialog() {
        val levels = TicTacToeGame.DifficultyLevel.values()
        val selected = levels.indexOf(game.getDifficultyLevel())

        AlertDialog.Builder(this)
            .setTitle("Select Difficulty")
            .setSingleChoiceItems(levels.map { it.name }.toTypedArray(), selected) { dialog, which ->
                game.setDifficultyLevel(levels[which])
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerMoveSound?.release()
        aiMoveSound?.release()
        winSound?.release()
        loseSound?.release()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("board", game.getBoard())
        outState.putString("currentPlayer", currentPlayer)
        outState.putBoolean("gameOver", gameOver)
        outState.putInt("userWins", userWins)
        outState.putInt("aiWins", aiWins)
        outState.putInt("draws", draws)
        outState.putString("gameMessage", gameMessage.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val board = savedInstanceState.getSerializable("board") as Array<Array<String>>
        game.setBoard(board)
        currentPlayer = savedInstanceState.getString("currentPlayer") ?: "X"
        gameOver = savedInstanceState.getBoolean("gameOver")
        userWins = savedInstanceState.getInt("userWins")
        aiWins = savedInstanceState.getInt("aiWins")
        draws = savedInstanceState.getInt("draws")
        gameMessage.text = savedInstanceState.getString("gameMessage") ?: ""
        statsTextView.text = "Wins: User $userWins  |  Gipeto AI $aiWins  |  Draws $draws"
        boardView.invalidate()
    }

}