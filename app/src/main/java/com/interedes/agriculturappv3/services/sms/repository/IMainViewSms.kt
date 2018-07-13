package com.interedes.agriculturappv3.services.sms.repository

import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal

interface IMainViewSms {
    interface Repository {
        fun getLastNotification(): NotificationLocal?
        fun saveNotification(notification:NotificationLocal)
    }
}