package dev.testing.sampleandtest.ui.camera

import android.graphics.Bitmap
import android.hardware.camera2.CameraDevice
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.dotworld.test.usbcamera.UsbCamera
import dev.dotworld.test.usbcamera.UsbCameraListener
import dev.testing.sampleandtest.databinding.FragmentCamaraXBinding
import kotlin.jvm.internal.Intrinsics.Kotlin


class CamaraXFragment : Fragment(), UsbCameraListener {

    private var _binding: FragmentCamaraXBinding? = null
    private val binding get() = _binding!!
    private lateinit var usbCamere:UsbCamera

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
        ): View? {
            _binding = FragmentCamaraXBinding.inflate(inflater, container, false)
            return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init class
        usbCamere = UsbCamera(requireActivity())
        // init Listener
        usbCamere.setListener(this)
        // check permission check
        if (usbCamere.checkAllCameraPermissions()) {
            //open camera (single camera only)
            usbCamere.openUsbCamera(requireContext(), binding.textureView)
        } else {

        }
        binding.takeImage.setOnClickListener {
            //if un-want to save local auto save set to false
            usbCamere.takePicture(autoSave = true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object {
        private val TAG = "CamaraXFragment"

    }

    override fun currentCamera(camera: CameraDevice) {
        //selected camera
        Log.d(TAG, "currentCamera: camera - $camera")
    }

    override fun takePicture(bitmap: Bitmap) {
        // if want to save in local file next line save bitmap to image
        //usbCamere.saveBitmapToFile(bitmap, "SampleAndTest_${usbCamere.getTime()}.jpg")
        binding.preview.setImageBitmap(bitmap)
    }
}