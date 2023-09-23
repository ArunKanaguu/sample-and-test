package dev.testing.sampleandtest.ui.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dev.testing.sampleandtest.databinding.FragmentColorBinding
import dev.testing.sampleandtest.viewmodel.AppViewModel


class ColorFragment : Fragment() {
    private var _binding: FragmentColorBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentColorBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
            val controller = requireActivity().window.insetsController
            controller?.hide(WindowInsets.Type.systemBars())
        }
        binding.container.addView(createCardView())
        binding.container.addView(createImage())
        binding.container.addView(createTextview())
        binding.container.addView(createTextview(
                leftMargin = 465f,
                topMargin = 592f,
                text = "smaple this\n",
                width = 376f,
                height = 50f,
                textSize = 20f
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.sample.setBackgroundColor(rgbaToHex(220,123,180,1))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.sample.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun rgbaToHex(red: Int, green: Int, blue: Int, alpha: Int = 0): Int {
        val color = (red shl 24) or (green shl 16) or (blue shl 8) or alpha
        return Color.parseColor(String.format("#%08X", color))
    }

    val base64Img =
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEEAAABBCAYAAACO98lFAAAHSklEQVR4AeWbeWwUVRzHv29mV2k5xVviCd7IFUEuOQIJ9FoRAoKAJmjabhFEbgwqIhFFCWgtuxzhH4hHRbHdtgKCULFEImgCaIgKBCKGQ7lBWnbm+X3lKrDdzuzV3fXTzPTNzHu7O79573e9NwLRZtzKZjjjbA9NdoREB2iiFc/eBMgmgEhlWYMUBoT8l9eP8NwB/j8M3VzH8k84fGYbPh9yClFEIBrkrb4Tsup5lrryK/pB3WjoVFJg5RC6D/5Ty7BoyHFEmMgJYcb6BjhwuiekHM+n3YdndEQayT+BlTDFYizMWIUIEb4QpBRw+wZDc4yDNLsgdnxPkcyCN3M1wiQ8IeSV9OYPmcdSW9QXEhUwqkZh8cDfECKhCUEpu7POOWz9HI+uR70jlN7Ix4KMyRBCwib2hZDz1SPQHSV8Avci/tiBc5VpWDLoTzuN7Gnt3OIhHPub4lQAitZwXl+BvK872mlkXQi5JRMgtM9Yaor45i5IYwPyfM9YbWBNCG7fdA6c95E4pLK3foxc32QrlevWCTm+abT7byMxMWDKoViYtSJYpeBCyC0Zxf0S1oqOZxkbTnB4pMP7VEVtFWq/OXfRg3T6NrHUHInPfjjNrvjQtS/QxcA6YeTqhpD01ZNDAIoWqNKWYXDhdYEuOgI2aeSfyv39CM4rjPyOINJIwd5pOvm/AcdzIzi0FjDxIPvsPVR2rUIemgI90DzFzdIH1166mtG+LjBEhYUvawlP5m7EkhnrHTh4shtNdRYF4kLdD+pKBE7DkO2pKH+vefranmCIuXGrCGf09nNffmGbSBPYiyE2n6zZxlJ7iYa0dK+zNLLm6St1Ql5ZFm8/lpFgeHizNmBBWjuWxkLdojVGwF3WueaJGkJgSCyNWUg0VMDkycxnGD/Yehs5s+bhZSHkFPflVWvdKh7xur7g7cy0VFeiF3JWXQr/LwtB119EotPg1DvcH627onRC+HMuHp1XgGMKb4Y/9RDsEdw65Pjuh6k1RCRxHNsLz/DgN+kuKeTewtAQh+DYdRfyx1aetw7nUgZE3B5ozAVq8lFEEtF0EPdfBq8kv2VFC0KQt6CqZQ8Wvrk4HCyHnXGPxEHLdYWsFpaGMWVNaDvbIVkQ0np6TVBBZm9xakxHdaD0bkSyILXuNmrfB2P/3Zz9cTyEZELIvjZq63A6OmjQtMeRLLjL0igFu+n/FrQO8mHEGokVdFDzYZeUqm21Xnup6A4Yphd20WRrZSJvQ8wx92Kh6ztEipeLbkWVvhEqyWoXKR5TJvIGJDK5Jf0ogK1QSi4kZFPVE1KQiFRPAcrRLA1COEikKiE4kGi4S0dTAAyfxQMIFyFT1HAwkGh4Mgo47/gQn+IwbrsQDkKc0Og1RXUVSNRQeQRv5qfc1MoXFUKbCAUTp1RPOIFEx5P5BpMqwxAKQh53sDFDaC22E6xC68SQ91XrDRgPeLJmB63idRUy53gPe8i7sIPEHgpB+5FD4gnElicvbFZRXX12nbWcuhd+cxpLzWCdHWrl2C9IFvLTOeWGUltthL5dYx4+eYRQjbnRclW1EEzz79PQqOF2Hv2DpEHYSRNuQ4HrVw3zex9jwwr8P/mZm7zoLa7l5kKsEGILTKPcen3YWYxlfWpOGmrlzSWXeTm3udyciAVKAF7XREQDiT6WksZqCfFCV/UayPOJVk8m09jSnlaNR4YvbwJddLNUV4hPlKekipcnXzR8hESnSbP3qidd68aEYVy638tCKMhax7118xJv5BarSdkXrFUWxVjk2nnx6OqVKtbm8uINd+lbdMXV4gtri8o1TLnysCaezLVUKuuRCKiF5Tm+PoxBaN7ldBvtlqEg44p10NcmVExtAndb43KhhlpL1djoRi+3H/JK+3PS6BHY42/o2mtXnwx8o6NL58KU4xEMKacEX7Okc2jJ2wNeEmINM8Mra/lgNQ2g8/NT+OsaU9Hdza7OuRGzBRuqpHDoL5AIbQoWpM+55nTAympqzi838Ae1R7IgxCpmo9ICXQosVRWNGVVqsjLyq9PqBbEbonJEbVdr71qLnt4FUwxnX/EjkRE4Ck13oWBgrUFi8PGl3jOSmISERjyLgv5B0wV1KxlP5nwqwDeReJyjIsymHqjzhTHrZtBdlk1hFFBrJ8I8hUnzOZD+QJGVytbNjSd9EUPPwRSb9ZUg9cN+CLOzVQEo7DtEeataQfpVF2uJuEP+QB9jKAWw104r+47Hgv5/wLjtYdrdedU5uvhAZccmw6H3sisARZjvRZa247zFUn5MPTpV9D6hTYInbRtCJDLxgbt4AH+Iyvd3Qiyo7oGynPZ/Nt3gNQiTSAZJAtkl6Rxg2SxnRSkAO3P+XWm5hKa7PJQXQQMRnUhx6sYbcPy4clMzuPXk1gChc4bPvYhPfR3ONi7E0u4nEWGiHy5nF6fyibWlj9GGX9eX38hIUDSvXjYokMoyk7smHRuc5DneoPiL5T00x5sZ1m/GdfpOxjKViCL/AdDMIcqWtbOOAAAAAElFTkSuQmCC"

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createImage(
        width: Float = 245f,
        height: Float = 245f,
        topMargin: Float = 400f,
        leftMargin: Float = 178f,
        src: String = base64Img,
        padding: List<Float> = listOf<Float>(1f, 1f, 1f, 1f),
        setBackgroundColor: Int = "rgba(0,0,0,0)".rgbaToColor()
    ): ImageView {
        // Create ImageView instance
        val imageView = ImageView(requireContext())
        val color = Color.parseColor("#FF5733") // Replace with your desired color
        imageView.setBackgroundColor(setBackgroundColor)
        val bitmap = base64ToBitmap(src)
        if (bitmap != null) {
            imageView.setImageBitmap((bitmap))
        } else {

        }
        val width = width.toInt()
        val height = height.toInt()
        val layoutParams = RelativeLayout.LayoutParams(width, height)
        layoutParams.leftMargin = leftMargin.toInt()
        layoutParams.topMargin = topMargin.toInt()

        imageView.layoutParams = layoutParams
        return imageView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createTextview(
        width: Float = 523f,
        height: Float = 93f,
        topMargin: Float = 469f,
        leftMargin: Float = 471f,
        padding: List<Int> = listOf<Int>(1, 1, 1, 1),
        text: String = "helo",
        textSize: Float = 70f,
        textAlignment: Int = TextView.TEXT_ALIGNMENT_TEXT_START,
        textColor: Int = "rgba(0,0,0,1)".rgbaToColor(),
        setBackgroundColor: Int = "rgba(0, 0, 0, 0)".rgbaToColor()
    ): TextView {
        val textView = TextView(requireContext())
        textView.text = text
        textView.textSize = pixToSp(textSize)
        textView.setTextColor(textColor)
        textView.textAlignment = textAlignment
        textView.setBackgroundColor(setBackgroundColor)
        textView.setPadding(padding[0], padding[1], padding[2], padding[3])
        val width = width.toInt()
        val height = height.toInt()
        val layoutParams = RelativeLayout.LayoutParams(width, height)
        layoutParams.leftMargin = leftMargin.toInt()
        layoutParams.topMargin = topMargin.toInt()
        textView.layoutParams = layoutParams
        return textView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createCardView(
        width: Float = 817f,
        height: Float = 299f,
        topMargin: Float = 372f,
        leftMargin: Float = 106f,
        radius: Float = 42f,
        elevation: Float = 0f,
        backgroundColor: Int = "rgba(217,217,217,1)".rgbaToColor()
    ): CardView {
        val cardView = CardView(requireContext())
        cardView.radius = radius
        cardView.cardElevation = elevation
        cardView.setCardBackgroundColor(backgroundColor)
        val width = width.toInt()
        val height = height.toInt()
        val layoutParams = RelativeLayout.LayoutParams(width, height)
        layoutParams.leftMargin = leftMargin.toInt()
        layoutParams.topMargin = topMargin.toInt()
        cardView.layoutParams = layoutParams
        //cardView.addView(content)
        return cardView
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun String.rgbaToColor(): Int {
        try {
            val values = trim().substring(5, this.length - 1).split(",")
            return Color.argb(
                values[3].toFloat(),
                values[0].toFloat(),
                values[1].toFloat(),
                values[2].toFloat()
            )
        } catch (ex: Exception) {
            Log.e(TAG, ": ${ex.localizedMessage}")
        }
        return Color.argb(0.0f, 0f, 0f, 0f)
    }

    private fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            val base64Image = if (base64String.startsWith("data:image")) {
                base64String.substring(base64String.indexOf(",") + 1)
            } else {
                base64String
            }
            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            Log.e("Base64 to Bitmap", "Error converting Base64 to Bitmap: ${e.message}")
            null
        }
    }

    private fun pixToSp(pixels: Float): Float {
        return pixels / resources.displayMetrics.scaledDensity
    }

    private fun pixToDp(pixels: Float): Float {
        return pixels / (resources.displayMetrics.densityDpi / 160f)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "ColorFragment"
    }
}