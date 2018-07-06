package com.interedes.agriculturappv3.modules.notification

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.notification.events.RequestEventsNotification

interface IMainViewNotification {
    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()


        fun setResults(notifications: Int)


        //list
        fun setListNotification(listNotification:List<NotificationLocal>?)
        fun setNewNotification(notification: NotificationLocal)

        fun onNavigationdetailNotification(notification: NotificationLocal)


        //Response Notify
        fun onMessageToas(message:String,color:Int)
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        fun checkConectionInternet()


        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventsNotification?)

        //Methods
        fun getListNotification()
        fun updateNotifications(notification:NotificationLocal)

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getListNotification()
        fun updateNotifications(notification:NotificationLocal)

    }

    interface Repository {
        fun getListNotification()
        fun updateNotifications(notification:NotificationLocal)
    }
}