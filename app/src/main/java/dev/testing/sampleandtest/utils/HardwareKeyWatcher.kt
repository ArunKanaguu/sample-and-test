package dev.testing.sampleandtest.utils

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log


class HardwareKeyWatcher {

    companion object{
        const val TAG = "HardwareKeyWatcher"
        private var mContext: Context? = null
        private var mFilter: IntentFilter? = null
        private var mListener: OnHardwareKeysPressedListener? = null
        private var mReceiver: InnerReceiver? = null
    }


    interface OnHardwareKeysPressedListener {
        fun onHomePressed()
        fun onRecentAppsPressed()
    }

    @SuppressLint("NotConstructor")
    fun HardwareKeyWatcher(context: Context) {
        mContext = context
        mFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        mFilter!!.setPriority(1000)
    }

    fun setOnHardwareKeysPressedListenerListener(listener: OnHardwareKeysPressedListener) {
        mListener = listener
        mReceiver = InnerReceiver()
    }

    fun startWatch() {
        if (mReceiver != null) {
            mContext!!.registerReceiver(mReceiver, mFilter)
        }
    }

    fun stopWatch() {
        if (mReceiver != null) {
            mContext!!.unregisterReceiver(mReceiver)
        }
    }

    internal class InnerReceiver : BroadcastReceiver() {
        val SYSTEM_DIALOG_REASON_KEY = "reason"
        val SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps"
        val SYSTEM_DIALOG_REASON_HOME_KEY = "homekey"
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action == Intent.ACTION_CLOSE_SYSTEM_DIALOGS) {
                val reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY)
                if (reason != null) {
                    Log.e(TAG, "action:$action,reason:$reason")
                    if (mListener != null) {
                        if (reason == SYSTEM_DIALOG_REASON_HOME_KEY) {
                            mListener!!.onHomePressed()
                        } else if (reason == SYSTEM_DIALOG_REASON_RECENT_APPS) {
                            mListener!!.onRecentAppsPressed()
                        }
                    }
                }
            }
        }
    }
}

