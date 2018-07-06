package com.interedes.agriculturappv3.services.notifications.repository

import android.app.Notification
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal

interface IMainFirebaseInstance {

    interface Repository {
         fun syncToken(string:String?)

         fun getLastNotification(): NotificationLocal?

        fun saveNotification(notification:NotificationLocal)
    }
}