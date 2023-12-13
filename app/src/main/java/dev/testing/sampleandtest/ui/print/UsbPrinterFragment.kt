package dev.testing.sampleandtest.ui.print

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.print.PrintHelper
import androidx.print.PrintHelper.OnPrintFinishCallback
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentUsbPrinterBinding


class UsbPrinterFragment : Fragment() {

    private var _binding: FragmentUsbPrinterBinding? = null
    private val binding get() = _binding!!

    var mView: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUsbPrinterBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //get the layout of visiting card
        //get the layout of visiting card
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView: View = inflater.inflate(R.layout.identity_card, null)

        //get the main view which will be printed

        //get the main view which will be printed
        val view = contentView.findViewById<View>(R.id.relative) as RelativeLayout

        //get all the dynamic views which will be changed programmatically

        //get all the dynamic views which will be changed programmatically
        val tv = contentView.findViewById<View>(R.id.textView3) as TextView

        //change name

        //change name
        tv.text = "Titir Mukherjee"
        view.isDrawingCacheEnabled = true
        // this is the important code
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        //set view hight, width
        // this is the important code
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        //set view hight, width
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        //layout(left, top, size from left to right, size from top to bottom)
        //layout(left, top, size from left to right, size from top to bottom)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        //build the cache with autoscale enabled

        //build the cache with autoscale enabled
        view.buildDrawingCache(true)

        //store the view for further use

        //store the view for further use
        mView = view

        binding.button.setOnClickListener {
            doPhotoPrint();
        }

    }

    private fun doPhotoPrint() {
        val photoPrinter = PrintHelper(requireContext())
        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT

        //this is used for print drawable image
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                R.drawable.image);

        // get the layout in a bitmap
        val bitmap: Bitmap? = mView?.drawingCache

        //print
        if (bitmap != null) {
            photoPrinter.printBitmap("image.png_test_print", bitmap,
                OnPrintFinishCallback {
                    Toast.makeText(
                        requireContext(),
                        "Thank you!",
                        Toast.LENGTH_SHORT
                    ).show()
                })
        }
    }


    companion object {
        const val TAG = "UsbPrinterFragment"
    }
}