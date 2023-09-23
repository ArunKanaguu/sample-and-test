package dev.testing.sampleandtest.ui.image

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dev.testing.sampleandtest.databinding.FragmentImageConvertionBinding
import dev.testing.sampleandtest.viewmodel.AppViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ImageConverterFragment : Fragment() {

    private var _binding: FragmentImageConvertionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppViewModel by activityViewModels()

    private var imagePath = ""
    private var pngFilePath = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageConvertionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val file = createSampleImageFolder()
        if (file != null) {
            if (file.exists()){
                Log.w(TAG, "onViewCreated: file ${file.absoluteFile}", )
                pngFilePath = file.absolutePath

            }else{
                Log.w(TAG, "onViewCreated: not file exist", )
            }
        }
        with(binding){
            uploadImg.setOnClickListener {
                selectedImg.text = "selected img : null"
                pngImage.text = "Png image : null"
                selectImage()
            }
        }

       /* // Replace "pngFilePath" with the desired output PNG file path
        val pngFilePath: String = Environment.getExternalStorageDirectory() + "/output/image.png"

        // Convert WebP to PNG and display the converted PNG image in the ImageView
        val pngBitmap = convertWebPToPNG(webpFilePath, pngFilePath)*/
    }



    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private val imagePickerLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val uri: Uri? = result.data!!.data
            val imagePath: String? = uri?.let { getRealPathFromURI(requireContext(), it) };
            Log.d(TAG, "selectedImageUri : $imagePath")
            if (imagePath != null) {
                binding.selectedImg.text = binding.selectedImg.text.toString().replace("null",imagePath)
                convertToPNG(imagePath)
            }else{
                binding.selectedImg.text = binding.selectedImg.text.toString().replace("null", "imagePath null")
            }
        }
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val contentResolver: ContentResolver = context.contentResolver
            cursor = contentResolver.query(uri, projection, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }


    // Method to create the "SampleImage" folder in the app-specific directory
    private fun createSampleImageFolder(context: Context = requireContext()): File? {
        /*val appDir: File = context.filesDir // Gets the app-specific directory
        val sampleImageFolder = File(appDir, "SampleImage")*/

        val folderName = "SampleApp"
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val sampleImageFolder = File(downloadsDir, folderName)

        return if (!sampleImageFolder.exists()) {
            if (sampleImageFolder.mkdirs()) {
                sampleImageFolder
            } else {
                null
            }
        } else {
            sampleImageFolder
        }
    }


    private fun convertToPNG(imagePath: String, pngPath: String = pngFilePath): Bitmap? {
        var pngBitmap: Bitmap? = null
        try {
            val webpBitmap: Bitmap? = BitmapFactory.decodeFile(imagePath)
            if (webpBitmap != null) {
                saveBitmapAsPNG(webpBitmap, pngPath)
                pngBitmap = webpBitmap
//                val ob = BitmapDrawable(resources, pngBitmap)
//                binding.pngImageLoad.setImageDrawable(ob)
                binding.pngImageLoad.setImageBitmap(pngBitmap)
            }
        } catch (e: Exception) {

            Log.d(TAG, "convertToPNG: webpBitmap = ${e.message} ")
        }
        return pngBitmap
    }

    private fun saveBitmapAsPNG(bitmap: Bitmap, filePath: String) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "png-$timeStamp.png"
        val pngFile = File(filePath, fileName)

        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(pngFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            binding.pngImage.text = binding.pngImage.text.toString().replace("null",pngFile.path)
        } catch (e: IOException) {
            e.printStackTrace()
            binding.pngImage.text = binding.pngImage.text.toString().replace("null","saveBitmapAsPNG ${e.message}")
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
     private const val TAG = "ImageConverterFragment"
     private const val PICK_IMAGE_REQUEST_CODE = 1;
    }
}