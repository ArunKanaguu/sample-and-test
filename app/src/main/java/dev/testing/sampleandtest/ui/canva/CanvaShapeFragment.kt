package dev.testing.sampleandtest.ui.canva

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import dev.dotworld.ceezet.dynamic_ui.UiObject
import dev.testing.sampleandtest.databinding.FragmentCanvaShapeBinding


class CanvaShapeFragment : Fragment() {

    private var _binding: FragmentCanvaShapeBinding? = null
    private val binding get() = _binding!!

    private var sysWidth: Int = 0
    private var sysHeight: Int = 0
    private lateinit var shapeDrawable: ShapeDrawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCanvaShapeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loadAsset = resources.assets.open("my.json").bufferedReader().use {
            it.readText()
        }

        val json = Gson().fromJson(loadAsset, UiObject::class.java)
        binding.dynamicView.drawUiObjects(json)

    }



    companion object {
        const val TAG = "CanvaShapeFragment"
    }
}