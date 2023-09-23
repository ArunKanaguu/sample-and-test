package dev.testing.sampleandtest.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class MovableCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLUE
        isAntiAlias = true
    }

    private val radius = 100f
    private var centerX = 0f
    private var centerY = 0f
    private var offsetX = 0f
    private var offsetY = 0f
    private var isDragging = false

    init {
        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    val distanceX = event.x - centerX
                    val distanceY = event.y - centerY
                    if (distanceX * distanceX + distanceY * distanceY <= radius * radius) {
                        offsetX = centerX - event.x
                        offsetY = centerY - event.y
                        isDragging = true
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        centerX = event.x + offsetX
                        centerY = event.y + offsetY
                        invalidate()
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isDragging = false
                }
            }
            true
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!isDragging) {
            centerX = width / 2f
            centerY = height / 2f
        }
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}
