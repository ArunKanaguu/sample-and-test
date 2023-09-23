package dev.testing.sampleandtest.ui.customview

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.RelativeLayout.LayoutParams
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentCustomViewBinding
import es.voghdev.pdfviewpager.library.PDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import java.io.File


class CustomViewFragment : Fragment() {
    private var _binding: FragmentCustomViewBinding? = null
    private val binding get() = _binding!!

    private var sysWidth: Int = 0
    private var sysHeight: Int = 0

    private var selectedPdfUri = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomViewBinding.inflate(inflater, container, false)

        val rootView = binding.root

        sysWidth = Resources.getSystem().displayMetrics.widthPixels
        sysHeight = Resources.getSystem().displayMetrics.heightPixels

        val w = resources.displayMetrics

        Log.d(TAG, "onCreateView:(Pixels) ${w.widthPixels} x ${w.heightPixels}")
        Log.d(TAG, "onCreateView:(dpi) ${w.xdpi} x ${w.ydpi}")
        Log.d(TAG, "onCreateView:(Resources.getSystem()) $sysWidth x $sysHeight")

        val newTextView = TextView(requireContext())
        newTextView.text = "Dynamically added TextView"
        newTextView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val newImageView = ImageView(requireContext())
        newImageView.setImageResource(R.drawable.ic_launcher_foreground) // Replace with your image resource
        newImageView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )


        /*binding.container.addView(newTextView)
        binding.container.addView(newImageView)*/
        binding.container.addView(createImage())
        val v = createVideoView()
        binding.container.addView(v)
        binding.container.addView(createWebView())
        val pdf = createPDFView()
        binding.container.addView(pdf)
        // Set video source
        val videoUri =
            Uri.parse("android.resource://${requireActivity().packageName}/${R.raw.sapce}")
        v.setVideoURI(videoUri)
        // Set up media controller
        val mediaController = MediaController(requireContext())
        v.setMediaController(mediaController)
        v.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true
        }
        v.start()
        path
        val pdfAdapter = PDFPagerAdapter(
            requireContext(),
            path
        )
        pdf.adapter = pdfAdapter
        //binding.container.removeView(pdf) //remove one view
        return rootView
    }

    private fun createImage(): ImageView {
        // Create ImageView instance
        val imageView = ImageView(requireContext())
        val color = Color.parseColor("#FF5733") // Replace with your desired color
        imageView.setBackgroundColor(color)
        imageView.setImageResource((dev.testing.sampleandtest.R.drawable.img))
        // Set custom size
        val width = (sysWidth - percentageCalculate(sysWidth, 0.10f)) // in pixels
        val height = (sysHeight - percentageCalculate(sysHeight, 0.70f))  // in pixels
        val layoutParams = LayoutParams(width, height)
        // Set custom position
        layoutParams.leftMargin = percentageCalculate(sysWidth, 0.05f) // in pixels
        layoutParams.topMargin = percentageCalculate(sysHeight, 0.25f) // in pixels

        imageView.layoutParams = layoutParams
        return imageView
    }

    private fun createVideoView(): VideoView {
        // Create VideoView instance
        val videoView = VideoView(requireContext())
        // Set custom size
        val width = percentageCalculate(sysWidth, 0.68f) // in pixels
        val height = 800 // in pixels
        val layoutParams = LayoutParams(width, height)
        // Set custom position
        layoutParams.leftMargin = percentageCalculate(sysWidth, 0.11f) // in pixels
        layoutParams.topMargin = percentageCalculate(sysHeight, 0.05f) // in pixels
        videoView.layoutParams = layoutParams

        return videoView
    }

    private fun createWebView(): WebView {
        // Create WebView instance
        val webView = WebView(requireContext())
        webView.webViewClient = WebViewClient()

        // Enable JavaScript
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = true
        // Set custom size
        val width = RelativeLayout.LayoutParams.MATCH_PARENT // in pixels
        val height = sysHeight - percentageCalculate(sysHeight, 0.56f) // in pixels
        val layoutParams = LayoutParams(width, height)
        // Set custom position
        //layoutParams.leftMargin = percentageCalculate(sysWidth,0.30f) // in pixels
        layoutParams.topMargin = percentageCalculate(sysHeight, 0.56f) // in pixels
        webView.layoutParams = layoutParams
        // Load a web page
        webView.loadUrl("https://aniwatch.to/tv") // Replace with your URL
        return webView
    }

    private fun createPDFView(): PDFViewPager {

        Log.d(TAG, "createPDFView: $path")

        val pdfViewPager = PDFViewPager(requireContext(),path)
        // Set custom size
        val width = 400 // in pixels
        val height = 600  // in pixels
        val layoutParams = LayoutParams(width, height)
        // Set custom position
        val color = Color.parseColor("#FF5733") // Replace with your desired color
        pdfViewPager.setBackgroundColor(color)
        layoutParams.leftMargin = percentageCalculate(sysWidth, 0.65f) // in pixels
        layoutParams.topMargin = percentageCalculate(sysHeight, 0.05f)
        pdfViewPager.layoutParams = layoutParams
        return pdfViewPager
    }

    private fun percentageCalculate(number: Int = 1500, percentage: Float = 0.30f): Int {
        val result = number * percentage
        return result.toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        private const val path = "/storage/emulated/0/Download/sample.pdf"
        private const val REQUEST_CODE_PICK_PDF = 101
        private const val TAG = "CustomViewFragment"
    }
}