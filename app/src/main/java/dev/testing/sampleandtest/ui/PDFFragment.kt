package dev.testing.sampleandtest.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentPDFBinding
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter


class PDFFragment : Fragment() {


    private var _binding: FragmentPDFBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPDFBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pdf = PDFPagerAdapter(
            requireContext(),
            path
        )
        binding.pdfViewPager.adapter = pdf
        binding.pdfViewPager.currentItem = 1

    }

    companion object {
        private const val path = "/storage/emulated/0/Download/sample.pdf"
    }
}