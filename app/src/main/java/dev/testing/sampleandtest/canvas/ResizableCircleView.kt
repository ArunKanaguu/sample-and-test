package dev.testing.sampleandtest.canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import dev.testing.sampleandtest.R

class ResizableCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = Color.BLUE
        isAntiAlias = true
    }

    private val radius = 100f
    private val imageView: ImageView

    init {
        // Initialize ImageView
        imageView = ImageView(context)
        imageView.setImageResource(R.drawable.ic_launcher_foreground) // Replace with your image resource
        imageView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        addView(imageView)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f

        canvas.drawCircle(centerX, centerY, radius, paint)

        // Position the ImageView at the center of the circle
        val imgWidth = imageView.measuredWidth
        val imgHeight = imageView.measuredHeight
        val imgLeft = (centerX - imgWidth / 2).toInt()
        val imgTop = (centerY - imgHeight / 2).toInt()
        imageView.layout(imgLeft, imgTop, imgLeft + imgWidth, imgTop + imgHeight)
    }
}