package dev.testing.sampleandtest.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.app.NotificationCompat
import dev.testing.sampleandtest.R
import dev.testing.sampleandtest.SampleAndTestApp

class OverlayService : Service(), View.OnTouchListener, View.OnClickListener {
    private val TAG = "OverlayService"
    private lateinit var wm: WindowManager
    private lateinit var button: Button

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "channel1"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Overlay notification",
                NotificationManager.IMPORTANCE_LOW
            )

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("adsf")
                .setContentText("asdf1")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

            startForeground(1, notification)
        }

        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        button = Button(this)
        button.setBackgroundResource(R.drawable.ic_launcher_foreground)
        button.text = "Button"
        button.alpha = 1f
        button.setBackgroundColor(Color.BLUE)
        button.setOnClickListener(this)

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.START or Gravity.TOP
        params.x = 560
        params.y = 0
        wm.addView(button, params)

        return START_NOT_STICKY
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        android.util.Log.d(TAG, " ++++ On touch")
        return false
    }

    override fun onClick(v: View?) {
        android.util.Log.d(TAG, " ++++ On click")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::button.isInitialized) {
            wm.removeView(button)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}