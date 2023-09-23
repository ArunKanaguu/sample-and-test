package dev.testing.sampleandtest.ui.canva

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.graphics.toRectF
import androidx.core.view.setPadding
import dev.dotworld.ceezet.dynamic_ui.ElementType
import dev.dotworld.ceezet.dynamic_ui.UiElement
import dev.dotworld.ceezet.dynamic_ui.UiObject
import dev.dotworld.ceezet.dynamic_ui.base64ToBitmap
import dev.dotworld.ceezet.dynamic_ui.pixToDp
import dev.dotworld.ceezet.dynamic_ui.rgbaToColor

class DynamicView(context: Context, attributeSet: AttributeSet?) : View(context, attributeSet) {

    private var uiObjects: ArrayList<UiElement> = arrayListOf()

    fun drawUiObjects(uiObject: UiObject) {
        uiObjects.clear()
        uiObjects.addAll(uiObject.objects)
        invalidate()
    }

    private var rectPaint = Paint()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (uiObjects.isNotEmpty() && uiObjects[0].elementType == ElementType.image) {

            layoutParams = layoutParams.apply {

            }

            if (uiObjects.size >= 2) {
                for (i in 1 until uiObjects.size) {
                    rectPaint = Paint()
                    val element = uiObjects[i]
                    val rect = Rect(
                        element.left.toInt(),
                        element.top.toInt(),
                        (element.left + element.width * element.scaleX).toInt(),
                        (element.top + element.height * element.scaleY).toInt()
                    )
                    Log.i(TAG, "onDraw: element ${element.elementType} rect $rect")
                    when (element.elementType) {
                        ElementType.circle -> {

                        }

                        ElementType.image -> {
                            element.imageSrc?.base64ToBitmap()?.let {
                                canvas.drawBitmap(it, null, rect, null)
                            }
                        }

                        ElementType.rect -> {
                            rectPaint.color = element.fill.rgbaToColor()
                            canvas.drawRoundRect(rect.toRectF(), element.rx * scaleX, element.ry * scaleY, rectPaint)
                        }

                        ElementType.textbox -> {
                            val text = element.text ?: "null"
                            rectPaint.color = Color.BLACK
                            //canvas.drawRect(rect, rectPaint)
                            canvas.save()
                            canvas.translate(rect.left.toFloat(), rect.top.toFloat())
                            val textPaint = TextPaint().apply {
                                minimumHeight = element.height.toInt()
                                color = element.fill.rgbaToColor()
                                isUnderlineText = element.underLine
                                strokeWidth = element.strokeWidth
                                strokeMiter = element.strokeMiterLimit
                                scaleX = element.scaleX
                                scaleY = element.scaleY
                                setPadding(0)
                                textSize = context.pixToDp(element.fontSize)
                                textAlign = try {
                                    Paint.Align.valueOf(element.textAlign.uppercase())
                                } catch (ex: Exception) {
                                    Paint.Align.LEFT
                                }
                            }
                            val sb = StaticLayout.Builder.obtain(
                                text,
                                0,
                                text.length,
                                textPaint,
                                element.width.toInt()
                            )
                            sb.build().draw(canvas)
                            canvas.restore()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "DynamicView"
    }
}