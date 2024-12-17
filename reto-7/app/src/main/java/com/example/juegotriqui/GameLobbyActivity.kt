package com.example.juegotriqui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class GameLobbyActivity : AppCompatActivity() {

    private lateinit var gamesListView: ListView
    private lateinit var createGameButton: Button
    private lateinit var refreshButton: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var gamesRef: DatabaseReference

    private val availableGames = mutableListOf<Pair<String, String>>() // Game name and ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_lobby)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance()
        gamesRef = database.getReference("games")

        // Initialize UI elements
        gamesListView = findViewById(R.id.gamesListView)
        createGameButton = findViewById(R.id.createGameButton)
        refreshButton = findViewById(R.id.refreshButton)

        // Load available games
        loadAvailableGames()

        // Create a new game when the button is clicked
        createGameButton.setOnClickListener {
            showCreateGameDialog()
        }

        // Refresh the game list
        refreshButton.setOnClickListener {
            loadAvailableGames()
        }

        // Join a selected game
        gamesListView.setOnItemClickListener { _, _, position, _ ->
            val (_, gameId) = availableGames[position]
            joinGame(gameId)
        }
    }

    /**
     * Load the list of available games from Firebase.
     */
    private fun loadAvailableGames() {
        gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                availableGames.clear()
                for (gameSnapshot in snapshot.children) {
                    val gameId = gameSnapshot.key
                    val isActive = gameSnapshot.child("isActive").getValue(Boolean::class.java) ?: false

                    // Use GenericTypeIndicator to handle nested Map types for playerO
                    val playerOType = object : GenericTypeIndicator<Map<String, String>>() {}
                    val playerO = gameSnapshot.child("playerO").getValue(playerOType)

                    // Only include games where playerO is null and the game is active
                    if (gameId != null && isActive && playerO == null) {
                        val gameName = gameSnapshot.child("name").getValue(String::class.java) ?: "Unnamed Game"
                        availableGames.add(Pair(gameName, gameId))
                    }
                }
                updateGameList()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@GameLobbyActivity, "Error loading games: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    /**
     * Update the ListView with the available games.
     */
    private fun updateGameList() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            availableGames.map { "${it.first} (ID: ${it.second})" }
        )
        gamesListView.adapter = adapter
    }

    /**
     * Show a dialog to create a new game.
     */
    private fun showCreateGameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Create New Game")

        val input = android.widget.EditText(this)
        input.hint = "Enter Game Name"
        builder.setView(input)

        builder.setPositiveButton("Create") { _, _ ->
            val gameName = input.text.toString().ifEmpty { "Unnamed Game" }
            createNewGame(gameName)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    /**
     * Create a new game in Firebase.
     */
    private fun createNewGame(gameName: String) {
        val newGameRef = gamesRef.push()
        val gameId = newGameRef.key
        val initialGameState = mapOf(
            "name" to gameName,
            "playerX" to mapOf("id" to "user1", "name" to "Player1"), // Placeholder for Player X
            "playerO" to null,
            "board" to listOf(listOf("", "", ""), listOf("", "", ""), listOf("", "", "")),
            "currentPlayer" to "X",
            "gameOver" to false,
            "winner" to null,
            "isActive" to true,
            "createdAt" to System.currentTimeMillis(),
            "moves" to emptyList<Map<String, Any>>() // Empty move list
        )
        newGameRef.setValue(initialGameState)
            .addOnSuccessListener {
                navigateToGame(gameId!!, "X") // Navigate as Player X
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to create a new game.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Join an existing game as Player O.
     */
    private fun joinGame(gameId: String) {
        val gameRef = gamesRef.child(gameId)
        val playerOData = mapOf("id" to "user2", "name" to "Player2") // Placeholder for Player O

        gameRef.child("playerO").setValue(playerOData)
            .addOnSuccessListener {
                navigateToGame(gameId, "O") // Navigate as Player O
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to join the game.", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Navigate to the game screen.
     */
    private fun navigateToGame(gameId: String, player: String) {
        val intent = Intent(this, MainActivityOnline::class.java)
        intent.putExtra("GAME_ID", gameId)
        intent.putExtra("PLAYER", player)
        startActivity(intent)
    }
}
