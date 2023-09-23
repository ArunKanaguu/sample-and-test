package dev.testing.sampleandtest.ui.tts

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dev.testing.sampleandtest.MainActivity
import dev.testing.sampleandtest.databinding.FragmentTtsToFileBinding
import dev.testing.sampleandtest.utils.hideKeyboard
import dev.testing.sampleandtest.viewmodel.AppViewModel
import java.io.File
import java.io.IOException


class TtsToFileFragment : Fragment() {
    private var _binding: FragmentTtsToFileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null
    private var selected = "audio"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTtsToFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.ttsState.observe(viewLifecycleOwner) {
            binding.fileConverState.text = it.toString()
        }
        val options = listOf("Speech", "Audio File")
        mediaPlayer = MediaPlayer()
        with(binding) {
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, options)
            selectedType.adapter = adapter
            selectedType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            selected = "audio"
                        }

                        1 -> {
                            selected = "file"
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }
            filePath.text = (activity as MainActivity).getAudioFilePath().absolutePath

            clear.setOnClickListener {
                val files = (activity as MainActivity).getAudioFilePath().deleteRecursively()

            }

            covert.setOnClickListener {
                view.let { activity?.hideKeyboard(it) }
                (activity as MainActivity).testToSpeech(text = text.text.toString(), selected)
            }
            play.setOnClickListener {
                playAudio()
            }
            convertToBytes.setOnClickListener {
                val filePath = if (binding.selectedFile.text.toString()
                        .isEmpty()
                ) (activity as MainActivity).getFileList()
                    ?.first()?.absolutePath else binding.selectedFile.text.toString().split(": \n")[1]
                if (filePath?.let { File(it).exists() } == true) {
                    fileSize.text =
                        (activity as MainActivity).convertWavToBinaryArray(filePath)?.size.toString()
                }
            }
            viewFile.setOnClickListener {
                val files = (activity as MainActivity).getFileList()
                Log.d(TAG, "onViewCreated: ${(activity as MainActivity).getFileList()?.size}")
                var fileList: Array<String> = Array(files!!.size) { "" }
                (activity as MainActivity).getFileList()?.forEachIndexed { i, d ->
                    Log.d(TAG, "onViewCreated: ${d.name}")
                    fileList[i] = d.absolutePath
                }
                alartList(fileList)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun alartList(fileList: Array<String>) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an Item")
        builder.setItems(fileList) { _, which ->
            val selectedItem = fileList[which]
            binding.selectedFile.text = "selected on : \n$selectedItem"
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun playAudio() {
        val filePath = if (binding.selectedFile.text.toString()
                .isEmpty()
        ) (activity as MainActivity).getFileList()
            ?.first()?.absolutePath else binding.selectedFile.text.toString().split(": \n")[1]
        if (filePath?.let { File(it).exists() } == true) {
            mediaPlayer = MediaPlayer()
            try {

                mediaPlayer!!.setDataSource(filePath)
                // if you want play in raw uri
                // mediaPlayer!!.setDataSource(this, uri)
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mediaPlayer?.release();
        } catch (e: IOException) {
            e.printStackTrace();
        }
    }

    companion object {
        private const val TAG = "TtsToFileFragment"
    }

}