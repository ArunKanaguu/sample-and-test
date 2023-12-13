package dev.testing.sampleandtest.ui.pdf

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.barteksc.pdfviewer.source.AssetSource
import com.github.barteksc.pdfviewer.util.FitPolicy
import dev.testing.sampleandtest.databinding.FragmentPdfBinding
import java.io.File
import java.io.InputStream


class PdfFragment : Fragment() {
    private var _binding: FragmentPdfBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: ${AssetSource("kkkkkk.pdf")}")

        requireActivity().runOnUiThread {
            //binding.pdfView.fromStream(getAssetFileInputStream(fileName = "kkkkkk.pdf"))
            binding.pdfView.fromAsset("kkkkkk.pdf")
                .defaultPage(0)
                .enableSwipe(true)
                .autoSpacing(true)
                .pageFitPolicy(FitPolicy.BOTH)
                .load()
        }
    }

    private fun getAssetFileInputStream(context: Context = requireContext(), fileName: String): InputStream? {
        return try {
            val assetManager = context.assets
            assetManager.open(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        private const val TAG = "PdfFragment"
    }
}