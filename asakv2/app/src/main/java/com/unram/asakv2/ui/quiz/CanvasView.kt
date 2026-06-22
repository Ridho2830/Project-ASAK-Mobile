package com.unram.asakv2.ui.quiz

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class CanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    interface OnStrokeEndListener {
        fun onStrokeEnd(bitmap: Bitmap)
    }

    var onStrokeEndListener: OnStrokeEndListener? = null

    // Mode: false = pensil (tulis), true = penghapus (eraser)
    var isEraserMode: Boolean = false
        set(value) {
            field = value
            paint.color = if (value) Color.WHITE else Color.BLACK
            paint.strokeWidth = if (value) currentWidth * 2.5f else currentWidth
        }

    private var currentWidth = 24f

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = currentWidth
    }

    private var path = Path()
    private var bitmap: Bitmap? = null
    private var canvasBitmap: Canvas? = null

    private var lastX = 0f
    private var lastY = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        currentWidth = w * 0.035f
        paint.strokeWidth = if (isEraserMode) currentWidth * 2.5f else currentWidth
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565)
        canvasBitmap = Canvas(bitmap!!).apply {
            drawColor(Color.WHITE)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        bitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
                lastX = x
                lastY = y
            }
            MotionEvent.ACTION_MOVE -> {
                path.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2)
                lastX = x
                lastY = y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                path.lineTo(x, y)
                canvasBitmap?.drawPath(path, paint)
                path.reset()
                invalidate()
                // Trigger submit ke AI setiap angkat (baik pensil maupun penghapus)
                bitmap?.let { bmp ->
                    onStrokeEndListener?.onStrokeEnd(
                        bmp.copy(bmp.config ?: Bitmap.Config.ARGB_8888, false)
                    )
                }
            }
        }
        return true
    }

    fun clearCanvas() {
        path.reset()
        canvasBitmap?.drawColor(Color.WHITE)
        invalidate()
    }

    fun getBitmap(): Bitmap? =
        bitmap?.copy(bitmap!!.config ?: Bitmap.Config.ARGB_8888, false)
}