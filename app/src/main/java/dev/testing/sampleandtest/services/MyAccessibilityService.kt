package dev.testing.sampleandtest.services

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        // Set up key event listeners here
        Log.i(TAG, "onServiceConnected: ")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        Log.d(TAG, "Key pressed via accessibility is: " + event.keyCode)
        //This allows the key pressed to function normally after it has been used by your app.
        return super.onKeyEvent(event)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Handle key events here
        Log.i(TAG, "onAccessibilityEvent: event $event")
    }

    override fun onInterrupt() {
        // Handle interruption or cleanup
        Log.i(TAG, "onInterrupt: ")
    }

    companion object{
        private const val TAG = "MyAccessibilityService"
    }
}
