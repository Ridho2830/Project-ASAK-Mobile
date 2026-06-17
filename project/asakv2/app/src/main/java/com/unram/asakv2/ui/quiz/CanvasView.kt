package com.unram.asakv2.ui.quiz

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * CanvasView — Custom View canvas tulis tangan untuk Quiz Tipe 3.
 * Memungkinkan user menulis aksara di canvas, hasilnya dikirim ke CNN untuk recognition.
 * [RENDI]
 */
class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val path = Path()
    private val paths = mutableListOf<Path>()
    private var canvasBitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
        drawCanvas?.drawColor(Color.WHITE)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                drawCanvas?.drawPath(path, paint)
                paths.add(Path(path))
                path.reset()
            }
        }
        invalidate()
        return true
    }

    /**
     * Mengambil bitmap dari canvas untuk dikirim ke recognizer.
     */
    fun getBitmap(): Bitmap? = canvasBitmap

    /**
     * Menghapus semua coretan di canvas.
     */
    fun clearCanvas() {
        paths.clear()
        path.reset()
        drawCanvas?.drawColor(Color.WHITE)
        invalidate()
    }
}
