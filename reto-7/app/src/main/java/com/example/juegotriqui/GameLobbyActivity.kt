package com.example.juegotriqui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class GameLobbyActivity : AppCompatActivity() {

    private lateinit var gamesListView: ListView
    private lateinit var createGameButton: Button
    private lateinit var refreshButton: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var gamesRef: DatabaseReference

    private val availableGames = mutableListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_lobby)

        database = FirebaseDatabase.getInstance()
        gamesRef = database.getReference("games")

        gamesListView = findViewById(R.id.gamesListView)
        createGameButton = findViewById(R.id.createGameButton)
        refreshButton = findViewById(R.id.refreshButton)

        loadAvailableGames()

        createGameButton.setOnClickListener {
            showCreateGameDialog()
        }

        refreshButton.setOnClickListener {
            loadAvailableGames()
        }

        gamesListView.setOnItemClickListener { _, _, position, _ ->
            val (_, gameId) = availableGames[position]
            joinGame(gameId)
        }
    }

    private fun loadAvailableGames() {
        gamesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                availableGames.clear()
                for (gameSnapshot in snapshot.children) {
                    val gameId = gameSnapshot.key
                    val isActive = gameSnapshot.child("isActive").getValue(Boolean::class.java) ?: false

                    // Safely parse playerO as a Map or null
                    val playerOType = object : GenericTypeIndicator<Map<String, String>>() {}
                    val playerO = try {
                        gameSnapshot.child("playerO").getValue(playerOType)
                    } catch (e: Exception) {
                        null // Handle invalid data gracefully
                    }

                    // Only include games where playerO is null and the game is active
                    if (gameId != null && isActive && playerO == null) {
                        val gameName = gameSnapshot.child("name").getValue(String::class.java) ?: "Unnamed Game"
                        availableGames.add(Pair(gameName, gameId))
                    }
                }
                updateGameList()
            }

            override fun onCancelled(error: DatabaseError) {
                showError("Failed to load games: ${error.message}")
            }
        })
    }




    private fun updateGameList() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            availableGames.map { "${it.first} (ID: ${it.second})" }
        )
        gamesListView.adapter = adapter
    }

    private fun showCreateGameDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Create New Game")

        val input = EditText(this)
        input.hint = "Enter Game Name"
        builder.setView(input)

        builder.setPositiveButton("Create") { _, _ ->
            val gameName = input.text.toString().ifEmpty { "Unnamed Game" }
            createNewGame(gameName)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    private fun navigateToGame(gameId: String, player: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("GAME_ID", gameId)
        intent.putExtra("PLAYER", player)
        startActivity(intent)
    }


    private fun createNewGame(gameName: String) {
        val newGameRef = gamesRef.push()
        val gameId = newGameRef.key
        val initialGameState = mapOf(
            "name" to gameName,
            "playerX" to "user1",
            "board" to listOf(listOf("", "", ""), listOf("", "", ""), listOf("", "", "")),
            "currentPlayer" to "X",
            "gameOver" to false,
            "isActive" to true
        )
        newGameRef.setValue(initialGameState)
            .addOnSuccessListener {
                navigateToGame(gameId!!, "X")
            }
            .addOnFailureListener {
                showError("Failed to create a new game.")
            }
    }

    private fun joinGame(gameId: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("GAME_ID", gameId)
        intent.putExtra("PLAYER", "O")
        startActivity(intent)
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
