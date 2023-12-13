package dev.testing.sampleandtest

import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.view.WindowMetrics
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.testing.sampleandtest.commons.PlayList
import dev.testing.sampleandtest.databinding.ActivityMain2Binding
import dev.testing.sampleandtest.databinding.NavViewLayoutBinding
import dev.testing.sampleandtest.viewmodel.AppViewModel
import java.util.Random


@AndroidEntryPoint
class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    private val viewModel: AppViewModel by viewModels()

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        hideSystemBar()
        setWakeLock()
        getFullWindowSize()
        setContentView(binding.root)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //createSplitScreen( h = true) //horizontal
        createSplitScreen(v = true) //vertical
        //createSplitScreen( 4,e = true) //equal
    }

    private fun setWakeLock() {
        // Keep the screen on while this activity is visible
        window.addFlags(LayoutParams.FLAG_KEEP_SCREEN_ON)
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "YourApp:WakeLockTag")

    }

    private fun hideSystemBar() {
        kotlin.runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Android 12 and newer
                window.setDecorFitsSystemWindows(false)
                val controller = window.insetsController
                controller?.hide(WindowInsets.Type.systemBars())
            } else {
                // For older Android versions, use deprecated method
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }
        }
    }

    private fun getFullWindowSize() {
        val displayMetrics = DisplayMetrics()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics: WindowMetrics =
                this.getSystemService(WindowManager::class.java).currentWindowMetrics
            val density = resources.displayMetrics.density
            Log.d(
                TAG,
                "getFullWindowSize: ${metrics.bounds.width()} / ${metrics.bounds.height()} / $density"
            )
            Log.d(
                TAG,
                "getFullWindowSize: ${metrics.bounds.width() / density} / ${metrics.bounds.height() / density}"
            )
            sysWidth = metrics.bounds.width()
            sysHeight = metrics.bounds.height()
        } else {
            val displayMetrics = this.resources.displayMetrics
            val density = resources.displayMetrics.density
            sysWidth = displayMetrics.widthPixels
            sysHeight = displayMetrics.heightPixels
            Log.d(TAG, "getFullWindowSize: $sysWidth / $sysHeight = $density")
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
        } else if (e) {
            val orientation = this.resources.configuration.orientation
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(TAG, "createSplitScreen: PORTRAIT")
                widthSplit = 2
                heightSplit = splitCount / 2
            } else {
                Log.d(TAG, "createSplitScreen: LANDSCAPE")
                widthSplit = splitCount / 2
                heightSplit = 2
            }
        }
        Log.d(
            TAG,
            "createSplitScreen: width = $width / height = $height / widthSplit = $widthSplit / heightSplit = $heightSplit"
        )
        width = sysWidth / widthSplit
        height = sysHeight / heightSplit
        Log.d(TAG, "createSplitScreen: splitCount = $splitCount / h = $h / v = $v")
        for (i in 0 until splitCount) {
            createLayoutView(width - 1, height - 1, top + 1, left + 1)
            if (h) {
                left += width
            } else if (v) {
                top += height
            } else if (e) {
                val orientation = this.resources.configuration.orientation
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    Log.d(TAG, "createSplitScreen: PORTRAIT")
                    if (i % 2 == 0) {
                        left += width
                    } else {
                        left = 0
                        top += height
                    }
                } else {
                    if ((i + 1) % (splitCount / 2) != 0) {
                        left += width
                    } else {
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
//        bgColor: Int = randomColor(),
        bgColor: Int =Color.parseColor("#000000"),
        text: String = ""
    ): LinearLayout {
        Log.d(TAG, "createLayoutView: w=$width h=$height t=$top l=$left")
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(width, height,1.0f)
        linearLayout.id = View.generateViewId()
        layoutParams.leftMargin = left // in pixels
        layoutParams.topMargin = top // in pixels
        linearLayout.layoutParams = layoutParams
        linearLayout.setBackgroundColor(bgColor)
        setTextView(linearLayout, "${childView.size + 1} Layout")
        addNavHostFragmentToLinearLayout(linearLayout, R.navigation.multi_nav_graph)
        childView.add(linearLayout)
        binding.mainView.addView(linearLayout)
        return linearLayout
    }

    private fun randomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        /*view.setBackgroundColor(color);*/
    }

    private fun setTextView(linearLayout: LinearLayout, text: String = "") {
        viewModel.playerList.add(PlayList(id = "${childView.size + 1} playlist"))
        val textView = TextView(this)
        textView.text = viewModel.playerList[childView.size].id
        linearLayout.addView(textView)
    }

    private fun setFragmentContainerView(
        linearLayout: LinearLayout,
        width: Int, height: Int,
        margin: Int = 12,
        backgroundColor: Long = 0xFFFFFFFF
    ) {
        val childLayout1: View =
            NavViewLayoutBinding.inflate(LayoutInflater.from(this)).root
        val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT,
            1.0F
        )
        /*LinearLayout.LayoutParams(width, height)*/
        childLayout1.background = ColorDrawable(backgroundColor.toInt())
        layoutParams.setMargins(margin, margin, margin, margin)
        linearLayout.layoutParams = layoutParams
        linearLayout.addView(childLayout1)
    }

    val navList: MutableList<NavController> = mutableListOf()

    private fun addNavHostFragmentToLinearLayout(
        linearLayout: LinearLayout,
        navGraphId: Int
    ) {
        val navHostFragment = NavHostFragment.create(navGraphId)

        // Set layout parameters for the NavHostFragment to match parent
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        navHostFragment.view?.layoutParams = layoutParams

        // Set gravity of the LinearLayout to center
        linearLayout.gravity = Gravity.CENTER

        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(linearLayout.id, navHostFragment).commit()
        navHost.add(navHostFragment)
    }

    companion object {
        private const val TAG = "MainActivity2"
        private var sysWidth: Int = 0
        private var sysHeight: Int = 0
        private var childView: ArrayList<LinearLayout> = arrayListOf()
        private var navHost: ArrayList<NavHost> = arrayListOf()
    }
}