package dev.testing.sampleandtest.ui.multiscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentVideoBinding
import dev.testing.sampleandtest.viewmodel.AppViewModel
import java.util.Timer
import kotlin.concurrent.timerTask

class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!



    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timer().schedule(timerTask {
            Log.d(TAG, "onViewCreated: $TAG")
            requireActivity().runOnUiThread {
                findNavController().navigate(R.id.action_videoFragment_to_webPageFragment)
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
        private const val TAG = "VideoFragment"
    }
}