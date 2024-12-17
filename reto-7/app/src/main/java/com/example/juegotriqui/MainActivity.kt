package com.example.juegotriqui

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.juegotriqui.ui.theme.BoardView
import com.example.juegotriqui.ui.theme.TicTacToeGame
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    private lateinit var gamesRef: DatabaseReference
    private lateinit var boardView: BoardView
    private lateinit var gameMessage: TextView
    private lateinit var statsTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    private val game = TicTacToeGame()
    private var userWins = 0
    private var aiWins = 0
    private var draws = 0
    private var gameOver = false
    private var isMuted = false
    private var playerMoveSound: MediaPlayer? = null
    private var winSound: MediaPlayer? = null
    private var loseSound: MediaPlayer? = null
    private var isMyTurn = false
    private var currentPlayer = "X"
    private var gameId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        gamesRef = database.getReference("games")

        // Initialize Views
        boardView = findViewById(R.id.board)
        boardView.setGame(game)
        boardView.initialize(R.drawable.x_image, R.drawable.o_image)

        gameMessage = findViewById(R.id.gameMessage)
        statsTextView = findViewById(R.id.stats)

        // Initialize Sounds
        playerMoveSound = MediaPlayer.create(this, R.raw.player_move)
        winSound = MediaPlayer.create(this, R.raw.winning)
        loseSound = MediaPlayer.create(this, R.raw.lose)

        // Persistent Data
        sharedPreferences = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE)
        userWins = sharedPreferences.getInt("userWins", 0)
        aiWins = sharedPreferences.getInt("aiWins", 0)
        draws = sharedPreferences.getInt("draws", 0)

        // Handle Intent Extras
        gameId = intent.getStringExtra("GAME_ID")
        currentPlayer = intent.getStringExtra("PLAYER") ?: "X"

        if (gameId != null) {
            joinGame(gameId!!)
        } else {
            createGame()
        }

        boardView.setOnTouchListener { _, event ->
            val col = (event.x / boardView.getCellWidth()).toInt()
            val row = (event.y / boardView.getCellHeight()).toInt()

            if (!gameOver && isMyTurn && game.getBoard()[row][col].isEmpty()) {
                handlePlayerMove(row, col)
            }
            true
        }
    }

    override fun onStop() {
        super.onStop()
        with(sharedPreferences.edit()) {
            putInt("userWins", userWins)
            putInt("aiWins", aiWins)
            putInt("draws", draws)
            apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        menu.add(0, Menu.FIRST, Menu.NONE, "Back to Lobby")
        menu.add(0, R.id.quit_game, Menu.NONE, "Quit Game")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_game -> {
                resetGame()
                true
            }
            R.id.mute_sounds -> {
                toggleMute(item)
                true
            }
            R.id.quit_game -> { // Quit Game and delete it
                if (gameId != null) {
                    gamesRef.child(gameId!!).removeValue()
                }
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun toggleMute(item: MenuItem) {
        isMuted = !isMuted
        item.title = if (isMuted) "Activate Sounds" else "Mute Sounds"
    }

    private fun handlePlayerMove(row: Int, col: Int) {
        if (game.makeMove(currentPlayer, row, col)) {
            if (!isMuted) playerMoveSound?.start()
            updateFirebaseGameState()
            isMyTurn = false
        }
    }

    private fun createGame() {
        val newGameRef = gamesRef.push()
        gameId = newGameRef.key
        val initialGameState = mapOf(
            "name" to "Unnamed Game",
            "playerX" to "user1",
            "board" to arrayListOf(
                arrayListOf("", "", ""),
                arrayListOf("", "", ""),
                arrayListOf("", "", "")
            ),
            "currentPlayer" to "X",
            "gameOver" to false,
            "isActive" to true
        )
        newGameRef.setValue(initialGameState)
        currentPlayer = "X"
        isMyTurn = true
        listenForGameUpdates()
    }

    private fun joinGame(gameId: String) {
        this.gameId = gameId
        gamesRef.child(gameId).child("playerO").setValue("user2")
        gamesRef.child(gameId).child("isActive").setValue(true)
        currentPlayer = "O"
        isMyTurn = false
        listenForGameUpdates()
    }

    private fun listenForGameUpdates() {
        val currentGameRef = gamesRef.child(gameId!!)
        currentGameRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val board = snapshot.child("board").getValue<ArrayList<ArrayList<String>>>()
                    ?.map { it.toTypedArray() }
                    ?.toTypedArray()
                val currentPlayer = snapshot.child("currentPlayer").getValue(String::class.java)
                val gameOver = snapshot.child("gameOver").getValue(Boolean::class.java) ?: false

                if (board != null) {
                    game.setBoard(board)
                    boardView.invalidate()
                }
                this@MainActivity.currentPlayer = currentPlayer ?: "X"
                this@MainActivity.gameOver = gameOver
                isMyTurn = (currentPlayer == this@MainActivity.currentPlayer)
            }

            override fun onCancelled(error: DatabaseError) {
                gameMessage.text = "Error: ${error.message}"
            }
        })
    }

    private fun updateFirebaseGameState() {
        val currentGameRef = gamesRef.child(gameId!!)
        val boardAsList = game.getBoard().map { it.toMutableList() }.toMutableList()
        currentGameRef.child("board").setValue(boardAsList)
        currentGameRef.child("currentPlayer").setValue(if (currentPlayer == "X") "O" else "X")
        checkForWinnerOrDraw()
    }

    private fun checkForWinnerOrDraw() {
        if (game.isWinner(currentPlayer)) {
            gamesRef.child(gameId!!).child("gameOver").setValue(true)
            gameMessage.text = "Player $currentPlayer Wins!"
            if (!isMuted) winSound?.start()
            gameOver = true
        } else if (game.getBoard().flatten().all { it.isNotEmpty() }) {
            gamesRef.child(gameId!!).child("gameOver").setValue(true)
            gameMessage.text = "It's a Draw!"
            if (!isMuted) loseSound?.start()
            gameOver = true
        }
    }

    private fun resetGame() {
        game.resetGame()
        gameOver = false
        gameMessage.text = ""
        boardView.invalidate()
    }
}
