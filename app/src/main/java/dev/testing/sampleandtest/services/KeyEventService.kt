package dev.testing.sampleandtest.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

class KeyEventService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Set up key event listeners here and send broadcasts or messages to your activities
        return START_STICKY
    }
}
