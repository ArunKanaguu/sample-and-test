package dev.dotworld.ceezet.dynamic_ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi

fun String.rgbToColor(): Int {
    try {
        val regex = Regex("""rgb\((\d+),\s*(\d+),\s*(\d+)\)""")
        val matchResult = regex.find(this.trim())
        if (matchResult != null) {
            val (red, green, blue) = matchResult.destructured
            val redValue = red.toInt()
            val greenValue = green.toInt()
            val blueValue = blue.toInt()
            Log.i(TAG, "rgbToColor: $redValue $greenValue $blueValue")
            return Color.rgb(redValue, greenValue, blueValue)
        }
    } catch (ex: Exception) {
        Log.e(TAG, "rgbToColor: ${ex.localizedMessage}")
    }
    return Color.rgb(0, 0, 0)
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.rgbaToColor(): Int {
    try {
        val values = trim().substring(5, this.length - 1).split(",")
        return Color.argb(
            (values[3].toFloat() * 255).toInt(),
            values[0].toInt(),
            values[1].toInt(),
            values[2].toInt()
        )
    } catch (ex: Exception) {
        Log.e(TAG, "rgbaToColor: ${ex.localizedMessage}")
    }
    return Color.argb(0.0f, 0f, 0f, 0f)
}

fun String.rgbToHexColor(): String {
    return String.format("#%08X", this.rgbToColor())
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.rgbaToHexColor(): String {
    return String.format("#%08X", this.rgbaToColor())
}

fun String.base64ToBitmap(): Bitmap? {
    return try {
        val base64Image = if (startsWith("data:image")) {
            substring(indexOf(",") + 1)
        } else {
            this
        }
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    } catch (e: Exception) {
        Log.e("Base64 to Bitmap", "Error converting Base64 to Bitmap: ${e.message}")
        null
    }
}

fun Context.pixToSp(pixels: Float): Float {
    return pixels / resources.displayMetrics.scaledDensity
}

fun Context.pixToDp(pixels: Float): Float {
    return pixels / (resources.displayMetrics.densityDpi / 160f)
}

private const val TAG = "Utils"