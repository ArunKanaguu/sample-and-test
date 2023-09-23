package dev.testing.sampleandtest.ui

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.testing.sampleandtest.MainActivity
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.SampleAndTestApp
import dev.testing.sampleandtest.adapter.MainMenuAdapter
import dev.testing.sampleandtest.databinding.FragmentMainMenuBinding


class MainMenuFragment : Fragment() {
    private var _binding: FragmentMainMenuBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val menuList = arrayListOf<String>()
        menuList.add("TimePicker")
        menuList.add("Text to Speech (Audio File)")
        menuList.add("number to words")
        menuList.add("Image Convert")
        menuList.add("Set Auto Time Sync")
        menuList.add("CameraX UAB Cam")
        menuList.add("usb cam")
        menuList.add("CustomView")
        menuList.add("CustomSplitView")
        menuList.add("JsonCustomSplitView")
        menuList.add("canvas")
        menuList.add("shutdown")
        menuList.add("camera2")
        menuList.add("screen size")
        menuList.add("pdf")
        binding.menuRecyclerView?.adapter =
            MainMenuAdapter(menuList, object : MainMenuAdapter.OnClickListener {
                override fun onClick(menuStr: String) {
                    when (menuStr) {
                        "TimePicker" -> {
                            findNavController().navigate(R.id.timePickerFragment)
                        }

                        "camera2" -> {
                            findNavController().navigate(R.id.camara2Fragment)
                        }

                        "screen size" -> {
                            findNavController().navigate(R.id.screenSizeFragment)
                        }

                        "JsonCustomSplitView" -> {
                            findNavController().navigate(R.id.colorFragment)
                        }

                        "Text to Speech (Audio File)" -> {
                            findNavController().navigate(R.id.ttsToFileFragment)
                        }

                        "number to words" -> {
                            findNavController().navigate(R.id.numberToWordsFragment)
                        }

                        "Image Convert" -> {
                            findNavController().navigate(R.id.imageConverterFragment)
                        }

                        "Set Auto Time Sync" -> {
                            findNavController().navigate(R.id.autoTimeSyncFragment)
                        }

                        "usb cam" -> {
                            findNavController().navigate(R.id.cameraFragment)
                        }

                        "CameraX UAB Cam" -> {
                            findNavController().navigate(R.id.camaraXFragment)
                        }

                        "shutdown" -> {
                            (activity as MainActivity).findAndroidType()
                        }

                        "CustomView" -> {
                            findNavController().navigate(R.id.customViewFragment)
                        }

                        "canvas" -> {
                            findNavController().navigate(R.id.canvaShapeFragment)
                        }

                        "CustomSplitView" -> {
                            findNavController().navigate(R.id.customSplitViewFragment)
                        }

                        "pdf" -> {
                            findNavController().navigate(R.id.PDFFragment)
                        }

                        "recent app" -> {
                            val activityManager: ActivityManager =
                                requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

                            // Get a list of recently used tasks
                            val recentTasks =
                                activityManager.getRecentTasks(/* maxNum */ 10,/* flags */
                                    ActivityManager.RECENT_IGNORE_UNAVAILABLE
                                )

                            for (recentTask in recentTasks) {
                                val baseIntent = recentTask.baseIntent
                                val packageName = baseIntent.component?.packageName
                                val className = baseIntent.component?.className

                                if (packageName != null && className != null) {
                                    // Do something with packageName and className
                                    Log.d(TAG, "Package: $packageName, Class: $className")
                                }
                            }
                        }

                        else -> {
                            Toast.makeText(
                                SampleAndTestApp.instance, "menuStr : $menuStr", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MainMenuFragment"
    }

}