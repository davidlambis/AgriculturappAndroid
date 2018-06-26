package com.interedes.agriculturappv3.services.notifications

import android.app.Service
import android.content.Intent
import android.os.IBinder

class LikeService: Service() {

    private val NOTIFICATION_ID_EXTRA = "notificationId"
    private val IMAGE_URL_EXTRA = "imageUrl"

    override fun onBind(p0: Intent?): IBinder? {
        //Saving action implementation
        return null;
    }
}