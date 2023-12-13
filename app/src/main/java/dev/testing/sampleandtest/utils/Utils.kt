package dev.testing.sampleandtest.utils

import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.testing.sampleandtest.MainActivity
import dev.testing.sampleandtest.ui.time.TimePickerFragment
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.util.Date
import java.util.Locale
import java.util.TimeZone


fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

 var RECENT_ACTIVITY: String = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
    "com.android.systemui.recents.RecentsActivity"
} else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
    "com.android.systemui.recent.RecentsActivity"
} else {
    "com.android.internal.policy.impl.RecentApplicationDialog"
}

fun isDeviceRooted(): Boolean {
    // First, check the system build tags
    val buildTags = Build.TAGS
    if (buildTags != null && buildTags.contains("test-keys")) {
        return true
    }

    // Then, check for common root binaries
    val paths = arrayOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su"
    )

    for (path in paths) {
        if (File(path).exists()) {
            return true
        }
    }

    return false
}

private fun getCurrentDateTimeInRFC3339(): String? {
    try {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        sdf.timeZone = android.icu.util.TimeZone.getTimeZone("UTC")
        Log.i(TimePickerFragment.TAG, "getCurrentDateTimeInRFC3339: ${sdf.format(Date())}")
        return sdf.format(Date())
    } catch (ex: Exception) {
        Log.e(TimePickerFragment.TAG, "getCurrentDateTimeInRFC3339: ", ex)
    }
    return null
}

fun Window.hideSystemUI() {
    WindowCompat.setDecorFitsSystemWindows(this, true)
    WindowInsetsControllerCompat(
        this,
        decorView.findViewById(R.id.content)
    ).let { controller ->
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}

fun MainActivity.checkPermission(permission: String): Boolean {
    return packageManager.checkPermission(
        permission,
        packageName
    ) == PackageManager.PERMISSION_GRANTED
}


fun getEthMac(): String? {
    var macAddress: String? = null
    var br: BufferedReader? = null
    try {
        br = BufferedReader(FileReader("/sys/class/net/eth0/address"))
        macAddress = br.readLine().uppercase()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        if (br != null) {
            try {
                br.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    return macAddress
}