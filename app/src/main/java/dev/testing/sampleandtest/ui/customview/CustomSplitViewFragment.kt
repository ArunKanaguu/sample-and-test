package dev.testing.sampleandtest.ui.customview

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentCustomSplitViewBinding
import java.util.Random


class CustomSplitViewFragment : Fragment() {

    private var _binding: FragmentCustomSplitViewBinding? = null
    private val binding get() = _binding!!

    private var sysWidth: Int = 0
    private var sysHeight: Int = 0

    private lateinit var button: Button

    private val childView: ArrayList<LinearLayout> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCustomSplitViewBinding.inflate(inflater, container, false)
        button = addButton()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.setDecorFitsSystemWindows(false)
            val controller = requireActivity().window.insetsController
            controller?.hide(WindowInsets.Type.systemBars())
        }
        sysWidth = Resources.getSystem().displayMetrics.widthPixels
        sysHeight = Resources.getSystem().displayMetrics.heightPixels
        getFullWindowSize()
        return binding.root
    }

    private fun getFullWindowSize() {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics =
                requireActivity().getSystemService(WindowManager::class.java).currentWindowMetrics
            val density = resources.displayMetrics.density
            Log.d(TAG, "getFullWindowSize: ${metrics.bounds.width()} / ${metrics.bounds.height()} / $density")
            Log.d(TAG, "getFullWindowSize: ${metrics.bounds.width()/density} / ${metrics.bounds.height()/density}")
            sysWidth = metrics.bounds.width()
            sysHeight = metrics.bounds.height()
        } else {
            val displayMetrics = requireActivity().resources.displayMetrics

            val density = resources.displayMetrics.density
            sysWidth = displayMetrics.widthPixels
            sysHeight = displayMetrics.heightPixels
            Log.d(TAG, "getFullWindowSize: ${sysWidth} / ${sysHeight} = ${density}")
        }

    }

    private fun addButton(): Button {
        val topRightButton = Button(requireContext())
        topRightButton.text = "split"
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END)
        layoutParams.marginStart = sysWidth / 3
        layoutParams.topMargin = 20
        topRightButton.gravity = Gravity.CENTER_HORIZONTAL
        topRightButton.layoutParams = layoutParams
        button = topRightButton
        binding.mainView.addView(button)
        //childView.add(button)
        return topRightButton
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button.setOnClickListener {
            showEditTextAlertDialog(
                requireContext(),
                "split count",
                "split",
                "clear"
            ) /*{ inputText ->
                Toast.makeText(requireContext(), "Entered text: ${inputText.toInt()}", Toast.LENGTH_SHORT)
                    .show()
                createSplitScreen(inputText.toInt())
            }*/
        }
    }

    private fun createSplitScreen(
        splitCount: Int = 2,
        h: Boolean = false,
        v: Boolean = false,
        e: Boolean = false
    ) {
        var width = 0 // in pixels
        var height = 0 // in pixels
        var top = 0
        var left = 0
        var widthSplit = 1
        var heightSplit = 1
        if (h) {
            widthSplit = splitCount
        } else if (v) {
            heightSplit = splitCount
        } else if (e){
            val orientation = this.resources.configuration.orientation

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(TAG, "createSplitScreen: PORTRAIT")
                widthSplit = 2
                heightSplit = splitCount/2
            } else {
                Log.d(TAG, "createSplitScreen: LANDSCAPE")
                widthSplit = splitCount/2
                heightSplit = 2
            }

        }

        Log.d(TAG, "createSplitScreen: width = $width / height = $height / widthSplit = $widthSplit / heightSplit = $heightSplit")
        width = sysWidth / widthSplit
        height = sysHeight / heightSplit
        Log.d(TAG, "createSplitScreen: splitCount = $splitCount / h = $h / v = $v")
        for (i in 0 until splitCount) {
            createLayoutView(width-1, height-1, top+1, left+1)
            if (h) {
                left += width
            } else if (v) {
                top += height
            } else if(e){
                val orientation = this.resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Log.d(TAG, "createSplitScreen: PORTRAIT")
                    if (i%2 == 0){
                        left +=width
                    }else{
                        left = 0
                        top += height
                    }
                } else {
                    if ((i+1)%(splitCount/2) != 0){
                        left +=width
                    }else{
                        left = 0
                        top += height
                    }
                }


            }
        }
    }


    private fun createLayoutView(
        width: Int,
        height: Int,
        top: Int,
        left: Int,
        bgColor: Int = randomColor(),
        text: String = ""
    ): LinearLayout {
        Log.d(TAG, "createLayoutView: w=$width h=$height t=$top l=$left")
        val linearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(width, height)
        layoutParams.leftMargin = left // in pixels
        layoutParams.topMargin = top // in pixels
        linearLayout.layoutParams = layoutParams
        linearLayout.setBackgroundColor(bgColor)
        childView.add(linearLayout)
        binding.mainView.addView(linearLayout)
        return linearLayout
    }

    private fun randomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        /*view.setBackgroundColor(color);*/
    }

    private fun showEditTextAlertDialog(
        context: Context,
        title: String,
        positiveButtonText: String,
        negativeButtonText: String,
        /*onPositiveClick: (String) -> Unit*/
    ) {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        val layoutParams1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams1.setMargins(32, 16, 32, 16)
        layout.layoutParams = layoutParams1
        val editText = EditText(context)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        editText.layoutParams = layoutParams
        editText.inputType = InputType.TYPE_CLASS_NUMBER
        val spinner = Spinner(context)
        spinner.layoutParams = layoutParams
        val spinnerAdapter = ArrayAdapter.createFromResource(
            context,
            R.array.spinner_values,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        layout.addView(editText)
        layout.addView(spinner)
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton(positiveButtonText) { dialog, _ ->
                val inputText = editText.text.toString()
                childView.forEach {
                    binding.mainView.removeView(it)
                }
                Toast.makeText(
                    requireContext(),
                    "Entered text: ${inputText.toInt()}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(
                    TAG,
                    "showEditTextAlertDialog: selectedItemPosition ${spinner.selectedItemPosition}"
                )
                when (spinner.selectedItemPosition) {
                    1 -> {
                        createSplitScreen(inputText.toInt(), h = true)
                    }

                    2 -> {
                        createSplitScreen(inputText.toInt(), v = true)
                    }

                    0 -> {
                        if (inputText.toInt() >= 4) {
                            createSplitScreen(inputText.toInt(), e = true)
                        } else {
                            val orientation = this.resources.configuration.orientation
                            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                                createSplitScreen(inputText.toInt(), h = true)
                            } else {
                                createSplitScreen(inputText.toInt(), v = true)
                            }
                        }
                    }
                }

                dialog.dismiss()
            }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val TAG = "CustomSplitViewFragment"
    }
}