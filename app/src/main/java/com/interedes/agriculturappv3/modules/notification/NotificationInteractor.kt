package com.interedes.agriculturappv3.modules.notification

import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal

class NotificationInteractor:IMainViewNotification.Interactor {
    var repository: IMainViewNotification.Repository? = null
    init {
        repository = NotificationRepository()
    }

    override fun getListNotification() {
       repository?.getListNotification()
    }

    override fun updateNotifications(notification: NotificationLocal) {
        repository?.updateNotifications(notification)
    }
}