package com.mrsgx.campustalk.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ConnService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }
}
