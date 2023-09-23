package dev.testing.sampleandtest.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLUE // Set the color of the circle
        isAntiAlias = true // Enable anti-aliasing for smoother edges
    }

    private val radius = 100f // Set the radius of the circle

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f

        canvas.drawCircle(centerX, centerY, radius, paint) // Draw the circle at the specified center and radius
    }
}
