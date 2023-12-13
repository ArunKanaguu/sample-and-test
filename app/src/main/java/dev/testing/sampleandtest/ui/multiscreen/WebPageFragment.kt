package dev.testing.sampleandtest.ui.multiscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentWebPageBinding
import dev.testing.sampleandtest.viewmodel.AppViewModel
import java.util.Timer
import kotlin.concurrent.timerTask

class WebPageFragment : Fragment() {

    private var _binding: FragmentWebPageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWebPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webView.settings.javaScriptEnabled = true
        // Set a WebViewClient to track page loading
        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: android.webkit.WebResourceError?
            ) {
                // Page loading has failed
                super.onReceivedError(view, request, error)

            }
        }

        binding.webView.loadUrl("https://optical.toys/")
        nextLoad()
    }

    private fun nextLoad() {
        Timer().schedule(timerTask {
            Log.d(TAG, "onViewCreated: $TAG")
            requireActivity().runOnUiThread {
                findNavController().navigate(R.id.action_webPageFragment_to_defaultFragment)
            }
        }, viewModel.delay)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "onDestroyView: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
        _binding = null
    }

    companion object {
        private const val TAG = "WebPageFragment"
    }
}