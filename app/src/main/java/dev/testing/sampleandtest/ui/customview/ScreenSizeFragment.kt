package dev.testing.sampleandtest.ui.customview

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.fragment.app.Fragment
import dev.testing.sampleandtest.databinding.FragmentScreenSizeBinding
import kotlin.math.roundToInt


class ScreenSizeFragment : Fragment() {

    private var _binding: FragmentScreenSizeBinding? = null
    private val binding get() = _binding!!

    private var pixelsRate = PixelsRate()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScreenSizeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFullWindowSize()
    }

    private fun getFullWindowSize() {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics =
                requireActivity().getSystemService(WindowManager::class.java).currentWindowMetrics
            val density = resources.displayMetrics.density
            Log.d(
                TAG,
                "getFullWindowSize: ${metrics.bounds.width()} / ${metrics.bounds.height()} / $density"
            )
            Log.d(
                TAG,
                "\n getFullWindowSize: ${metrics.bounds.width() / density} / ${metrics.bounds.height() / density}"
            )
            pixelsRate.sysWidth = metrics.bounds.width()
            pixelsRate.sysHeight = metrics.bounds.height()
            pixelsRate.density = density
        } else {
            val displayMetrics = requireActivity().resources.displayMetrics
            with(pixelsRate) {
                density = displayMetrics.density
                sysWidth = displayMetrics.widthPixels
                sysHeight = displayMetrics.heightPixels
                Log.d(TAG, "getFullWindowSize: $sysWidth / $sysHeight = $density")
            }
        }

        val w = pixelsRate.sysWidth / pixelsRate.density
        val h = pixelsRate.sysHeight / pixelsRate.density
        pixelsRate.pixel = "${w.roundToInt()} X ${h.roundToInt()}"

        binding.sizeText.text = pixelsRate.toString()
    }

    companion object {
        private const val TAG = "ScreenSizeFragment"
    }
}

data class PixelsRate(
    var sysWidth: Int = 0,
    var sysHeight: Int = 0,
    var density: Float = 0f,
    var pixel: String = ""
)