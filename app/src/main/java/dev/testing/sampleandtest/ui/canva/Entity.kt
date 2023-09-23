package dev.dotworld.ceezet.dynamic_ui

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UiObject(
    val objects: ArrayList<UiElement> = arrayListOf()
)

@Keep
data class UiElement(
    @SerializedName("type")
    val elementType: ElementType,
    val left: Float,
    val top: Float,
    val height: Float,
    val width: Float,
    val fill: String,
    val stroke: String,
    @SerializedName("src")
    val imageSrc: String? = null,
    val text: String? = null,
    @SerializedName("underline")
    val underLine: Boolean = false,
    val fontSize: Float = 20f,
    val strokeWidth: Float = 0f,
    val strokeMiterLimit: Float = 4f,
    val textAlign: String = "left",
    val fontStyle: String = "normal",
    val scaleX: Float = 1f,
    val scaleY: Float = 1f,
    val rx: Float = 0f,
    val ry: Float = 0f

)

@Keep
enum class ElementType {
    image,
    rect,
    circle,
    textbox,
}