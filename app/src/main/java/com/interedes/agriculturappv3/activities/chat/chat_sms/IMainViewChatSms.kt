package com.interedes.agriculturappv3.activities.chat.chat_sms

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.activities.chat.chat_sms.events.RequestEventChatSms

interface IMainViewChatSms {

    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //Validaciones
        fun validarSendSms(): Boolean

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)


        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestEventChatSms?)


        //Validacion
        fun validarSendSms(): Boolean


        //Methods
        fun getListSms(context:Activity)

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getListSms(context:Activity)


    }

    interface Repository {

        fun getListSms(context:Activity)
    }

}