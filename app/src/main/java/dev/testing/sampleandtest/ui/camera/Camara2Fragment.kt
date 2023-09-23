package dev.testing.sampleandtest.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.common.util.concurrent.ListenableFuture
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.databinding.FragmentCamara2Binding
import dev.testing.sampleandtest.databinding.ImageViewBinding
import dev.testing.sampleandtest.utils.hideSystemUI
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class Camara2Fragment : Fragment() {
    private var _binding: FragmentCamara2Binding? = null
    private val binding get() = _binding!!

    private lateinit var builder: AlertDialog

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCamara2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        builder = AlertDialog.Builder(requireContext()).create()

        if (allPermissionsGranted()) {
            startCamera()

        } else {

        }
        binding.captureButton.setOnClickListener {
            val orientation: Int = resources.configuration.orientation
            capturePhoto()
        }
        binding.camSwitch.setOnClickListener {

        }
    }

    private fun createBitmapFromConstraintLayout(constraintLayout: ConstraintLayout): Bitmap? {
        Log.d(TAG, "createBitmapFromConstraintLayout: ")
        // Get the dimensions of the ConstraintLayout
        val width: Int = constraintLayout.width
        val height: Int = constraintLayout.height

        // Create a Bitmap with the same dimensions
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a Canvas with the Bitmap
        val canvas = Canvas(bitmap)

        // Draw the ConstraintLayout onto the Canvas
        constraintLayout.draw(canvas)
        return bitmap
    }

    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }


    fun showImage(img: Bitmap) {
        Log.d(TAG, "showImage: ")
        builder = AlertDialog.Builder(requireContext()).create()
        if (builder.isShowing) {
            builder.dismiss()
        }
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        builder.window?.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
            hideSystemUI()
            builder.setOnShowListener {
                clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            }
        }
        val bindingAlert = ImageViewBinding.inflate(layoutInflater)
        bindingAlert.dialogImageView.setImageBitmap(img)
        builder.setView(bindingAlert.root)
        builder.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::builder.isInitialized) {
            if (builder.isShowing) builder.dismiss()
        }
        cameraProviderFuture.cancel(true)
        _binding = null
    }

    private fun startCamera(cam: Int = CameraSelector.LENS_FACING_FRONT) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up the camera selector (use the back camera)
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(cam)
                .build()

            // Create a Preview use case
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.previewView.surfaceProvider)

            // Create an ImageCapture use case
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            // Bind the Preview and ImageCapture use cases to the camera
            val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturePhoto() {
        val outputDirectory = File(requireContext().externalCacheDir, "photos")

        val photoFile = File(
            outputDirectory,
            "${
                SimpleDateFormat(
                    "yyyyMMddHHmmss",
                    Locale.US
                ).format(System.currentTimeMillis())
            }.jpg"
        )

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
        val cameraExecutor: Executor = Executors.newSingleThreadExecutor()
        imageCapture.takePicture(cameraExecutor, object :
            ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                //get bitmap from image
                val bitmap = imageProxyToBitmap(image)
                Log.d(TAG, "bitmap = ${bitmap.height}")
                requireActivity().runOnUiThread {
                    val orientation: Int = resources.configuration.orientation
                    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                        rotateBitmap(bitmap, 270f)?.let { showImage(it) }
                    } else {
                        val iconBitmap =
                            BitmapFactory.decodeResource(resources, R.drawable.all_frame)
                        val img: Bitmap = bitmap
                        val flip = createFlippedBitmap(img, true, false)
                        if (flip != null) {
                            //val setFrameImg = fitDrawableInBitmap(flip, R.drawable.all_frame,0)
                            val crop = cropBitmapToHeight(flip, 1800)
                            val setFrameImg = matchDrawableSizeToBitmap(crop, R.drawable.all_frame)
                            val fit = matchDrawableSizeToBitmap(crop, R.drawable.all_frame)
                            val d =
                                ContextCompat.getDrawable(requireContext(), R.drawable.all_frame);
                            //val add = d?.let { addImageToBitmap(crop, it, 0, 0) }

                            if (fit != null) {
                                showImage(fit)
                            }
                        }
                    }
                }
                super.onCaptureSuccess(image)
                //Toast.makeText(requireContext(), "saved", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                //Toast.makeText(requireContext(), "un-saved", Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun cropBitmapToHeight(existingBitmap: Bitmap, targetHeight: Int): Bitmap {
        if (existingBitmap.height <= targetHeight) {
            // No cropping needed if the existingBitmap is already smaller or equal to the target height.
            return existingBitmap
        }

        // Calculate the cropping rectangle.
        val left = 0
        val top = (existingBitmap.height - targetHeight) / 2
        val right = existingBitmap.width
        val bottom = top + targetHeight

        // Crop the existingBitmap to the specified height.
        return Bitmap.createBitmap(existingBitmap, left, top, right - left, bottom - top)
    }


    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return createFlippedBitmap(
            Bitmap.createBitmap(
                source,
                0,
                0,
                source.width,
                source.height,
                matrix,
                true
            ), true, false
        )
    }

    fun fitDrawableInBitmap(existingBitmap: Bitmap, drawableResourceId: Int, padding: Int): Bitmap {
        // Load the drawable as a Bitmap
        val drawableBitmap = BitmapFactory.decodeResource(resources, drawableResourceId)

        // Create a Matrix to scale the drawable while maintaining its aspect ratio
        val matrix = Matrix()

        // Calculate the scale factors to fit the drawable within the existingBitmap
        val scaleWidth = (existingBitmap.width - 2 * padding).toFloat() / drawableBitmap.width
        val scaleHeight = (existingBitmap.height - 2 * padding).toFloat() / drawableBitmap.height
        val scaleFactor = scaleWidth.coerceAtMost(scaleHeight)

        // Apply the scaling to the Matrix
        matrix.postScale(scaleFactor, scaleFactor)

        // Create a new Bitmap with the dimensions of the existingBitmap
        val mergedBitmap = Bitmap.createBitmap(
            existingBitmap.width,
            existingBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        // Create a Canvas to draw both the existingBitmap and the scaled drawableBitmap
        val canvas = Canvas(mergedBitmap)

        // Draw the existingBitmap on the canvas
        canvas.drawBitmap(existingBitmap, 0f, 0f, null)

        // Calculate the position to center the drawable in the existingBitmap
        val left = (existingBitmap.width - drawableBitmap.width * scaleFactor) / 2 + padding
        val top = (existingBitmap.height - drawableBitmap.height * scaleFactor) / 2 + padding
        val rect = RectF(
            left,
            top,
            left + drawableBitmap.width * scaleFactor,
            top + drawableBitmap.height * scaleFactor
        )

        // Draw the scaled drawableBitmap on the canvas
        canvas.drawBitmap(drawableBitmap, null, rect, null)

        // Return the merged Bitmap with the drawable centered and scaled to fit
        return mergedBitmap
    }


    fun matchDrawableSizeToBitmap(existingBitmap: Bitmap, drawableResourceId: Int): Bitmap {
        // Load the drawable as a Bitmap
        val drawableBitmap = BitmapFactory.decodeResource(resources, drawableResourceId)

        // Create a new Bitmap with the same dimensions as the existingBitmap
        val mergedBitmap = Bitmap.createBitmap(
            existingBitmap.width,
            existingBitmap.height,
            Bitmap.Config.ARGB_8888
        )

        // Create a Canvas to draw both the existingBitmap and the drawableBitmap
        val canvas = Canvas(mergedBitmap)

        // Draw the existingBitmap on the canvas
        canvas.drawBitmap(existingBitmap, 0f, 0f, null)

        // Calculate the position to center the drawable in the existingBitmap
        val left = (existingBitmap.width - drawableBitmap.width) / 2
        val top = (existingBitmap.height - drawableBitmap.height) / 2

        // Draw the drawableBitmap on the canvas at the calculated position
        canvas.drawBitmap(drawableBitmap, left.toFloat(), top.toFloat(), null)

        // Return the merged Bitmap with the drawable centered and matching size
        return mergedBitmap
    }


    private fun createFlippedBitmap(source: Bitmap, xFlip: Boolean, yFlip: Boolean): Bitmap? {
        val matrix = Matrix()
        matrix.postScale(
            (if (xFlip) -1 else 1).toFloat(),
            (if (yFlip) -1 else 1).toFloat(),
            source.width / 2f,
            source.height / 2f
        )
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun getRoundedRectBitmap(bitmap: Bitmap, pixels: Int): Bitmap? {
        var result: Bitmap = bitmap
        try {
            result = Bitmap.createBitmap(
                bitmap.width, bitmap.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(result)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            paint.isAntiAlias = true
            canvas.drawARGB(0, 0, 0, 0)
            paint.color = color
            canvas.drawRoundRect(rectF, pixels.toFloat(), pixels.toFloat(), paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(bitmap, rect, rect, paint)
        } catch (e: NullPointerException) {
        } catch (o: OutOfMemoryError) {
        }
        return addFrameToBitmap(cropToMinimumSquare(result))
    }


    private fun addFrameToBitmap(
        originalBitmap: Bitmap,
        frameWidth: Int = 40,
        frameColor: Int = Color.MAGENTA,
        cornerRadius: Float = 30F
    ): Bitmap {
        // Calculate the new dimensions for the final bitmap
        val widthWithFrame = originalBitmap.width + (2 * frameWidth)
        val heightWithFrame = originalBitmap.height + (2 * frameWidth)

        // Create a new Bitmap with the calculated dimensions
        val framedBitmap =
            Bitmap.createBitmap(widthWithFrame, heightWithFrame, Bitmap.Config.ARGB_8888)

        // Create a Canvas to draw on the new Bitmap
        val canvas = Canvas(framedBitmap)

        // Draw the frame with rounded corners
        val framePaint = Paint()
        framePaint.color = frameColor
        val frameRect = RectF(0f, 0f, widthWithFrame.toFloat(), heightWithFrame.toFloat())
        canvas.drawRoundRect(frameRect, cornerRadius, cornerRadius, framePaint)

        // Calculate the destination rectangle for the original image
        val imageRect = RectF(
            frameWidth.toFloat(),
            frameWidth.toFloat(),
            (widthWithFrame - frameWidth).toFloat(),
            (heightWithFrame - frameWidth).toFloat()
        )

        // Draw the original image on top of the frame
        val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawBitmap(originalBitmap, null, imageRect, imagePaint)

        return framedBitmap
    }

    private fun cropToMinimumSquare(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val minSize = minOf(width, height)

        val xOffset = (width - minSize) / 2
        val yOffset = (height - minSize) / 2

        // Crop the Bitmap to a square of the minimum size
        return Bitmap.createBitmap(bitmap, xOffset, yOffset, minSize, minSize)
    }


    private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val planeProxy = image.planes[0]
        val buffer: ByteBuffer = planeProxy.buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun addImageToBitmap(originalBitmap: Bitmap, drawable: Drawable, x: Int, y: Int): Bitmap {
        val resultBitmap = Bitmap.createBitmap(
            originalBitmap.width,
            originalBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(resultBitmap)
        canvas.drawBitmap(originalBitmap, 0f, 0f, null)
        val left = x
        val top = y
        val right = left + drawable.intrinsicWidth
        val bottom = top + drawable.intrinsicHeight
        drawable.setBounds(left, top, right, bottom)
        drawable.draw(canvas)
        return resultBitmap
    }


        companion object {
        private val TAG: String = "CameraFragment"
    }
}