package com.example.juegotriqui.ui.theme

import kotlin.random.Random

class TicTacToeGame {

    enum class DifficultyLevel { Easy, Harder, Expert }

    private val board = Array(3) { Array(3) { "" } }
    private var difficultyLevel: DifficultyLevel = DifficultyLevel.Expert

    fun getBoard(): Array<Array<String>> = board

    fun setDifficultyLevel(level: DifficultyLevel) {
        difficultyLevel = level
    }

    fun getDifficultyLevel(): DifficultyLevel = difficultyLevel

    fun resetGame() {
        for (i in board.indices) {
            for (j in board[i].indices) {
                board[i][j] = ""
            }
        }
    }

    fun isWinner(player: String): Boolean {
        for (i in 0..2) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) return true
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) return true
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) return true
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) return true
        return false
    }

    fun getRandomMove(): Pair<Int, Int> {
        val emptySpaces = mutableListOf<Pair<Int, Int>>()
        for (i in board.indices) {
            for (j in board[i].indices) {
                if (board[i][j].isEmpty()) {
                    emptySpaces.add(Pair(i, j))
                }
            }
        }
        return emptySpaces.random()
    }

    fun getComputerMove(): Pair<Int, Int> {
        return when (difficultyLevel) {
            DifficultyLevel.Easy -> getRandomMove()
            DifficultyLevel.Harder -> {
                getWinningMove("O") ?: getRandomMove()
            }
            DifficultyLevel.Expert -> {
                getWinningMove("O") ?: getWinningMove("X") ?: getRandomMove()
            }
        }
    }

    private fun getWinningMove(player: String): Pair<Int, Int>? {
        for (i in board.indices) {
            for (j in board[i].indices) {
                if (board[i][j].isEmpty()) {
                    board[i][j] = player
                    val isWinner = isWinner(player)
                    board[i][j] = ""
                    if (isWinner) return Pair(i, j)
                }
            }
        }
        return null
    }

    fun makeMove(player: String, row: Int, col: Int): Boolean {
        if (board[row][col].isEmpty()) {
            board[row][col] = player
            return true
        }
        return false
    }
}
