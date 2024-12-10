package com.example.juegotriqui.ui.theme

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BoardView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    companion object {
        private const val GRID_WIDTH = 6 // Grosor de las líneas del tablero
    }

    private var mHumanBitmap: Bitmap? = null
    private var mComputerBitmap: Bitmap? = null
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mGame: TicTacToeGame? = null

    init {
        mPaint.apply {
            color = Color.LTGRAY // Color de las líneas del tablero
            strokeWidth = GRID_WIDTH.toFloat()
        }
    }

    fun initialize(humanResId: Int, computerResId: Int) {
        // Cargar las imágenes de las X y O
        mHumanBitmap = BitmapFactory.decodeResource(resources, humanResId)
        mComputerBitmap = BitmapFactory.decodeResource(resources, computerResId)

        // Manejo de errores si las imágenes no se cargan correctamente
        if (mHumanBitmap == null || mComputerBitmap == null) {
            throw IllegalArgumentException("No se pudieron cargar las imágenes de X y O.")
        }
    }

    fun setGame(game: TicTacToeGame) {
        mGame = game
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val boardWidth = width
        val boardHeight = height
        val cellWidth = boardWidth / 3
        val cellHeight = boardHeight / 3

        // Dibujar las líneas del tablero
        drawGrid(canvas, boardWidth, boardHeight, cellWidth, cellHeight)

        // Dibujar las X y O en las posiciones correctas
        drawMarkers(canvas, cellWidth, cellHeight)
    }

    private fun drawGrid(canvas: Canvas, boardWidth: Int, boardHeight: Int, cellWidth: Int, cellHeight: Int) {
        for (i in 1..2) {
            // Líneas verticales
            canvas.drawLine(
                (i * cellWidth).toFloat(), 0f,
                (i * cellWidth).toFloat(), boardHeight.toFloat(),
                mPaint
            )

            // Líneas horizontales
            canvas.drawLine(
                0f, (i * cellHeight).toFloat(),
                boardWidth.toFloat(), (i * cellHeight).toFloat(),
                mPaint
            )
        }
    }

    private fun drawMarkers(canvas: Canvas, cellWidth: Int, cellHeight: Int) {
        mGame?.getBoard()?.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                val left = colIndex * cellWidth
                val top = rowIndex * cellHeight
                val right = left + cellWidth
                val bottom = top + cellHeight
                val destRect = Rect(left, top, right, bottom)

                when (cell) {
                    "X" -> mHumanBitmap?.let { bitmap ->
                        canvas.drawBitmap(bitmap, null, destRect, null)
                    }
                    "O" -> mComputerBitmap?.let { bitmap ->
                        canvas.drawBitmap(bitmap, null, destRect, null)
                    }
                }
            }
        }
    }

    fun getCellWidth(): Int = width / 3
    fun getCellHeight(): Int = height / 3
}
