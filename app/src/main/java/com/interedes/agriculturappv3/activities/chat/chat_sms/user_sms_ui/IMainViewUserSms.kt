package com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui.events.RequestEventUserSms
import com.interedes.agriculturappv3.modules.models.sms.Sms

interface IMainViewUserSms {

    interface MainView {
        //Progress and progress Hud
        fun showProgress()
        fun hideProgress()
        fun showProgressHud()
        fun hideProgressHud()

        //Validaciones
        fun validarSendSms(): Boolean
        fun limpiarCampos()

        //list
        fun setListSms(sms: List<Sms>)
        fun getListSms()

        //Navigate
        fun navigateDetailSms(sms:Sms)

        fun addContact(sms:Sms)

        //Response Notify
        fun onMessageToas(message:String,color:Int)
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
        fun onEventMainThread(requestEvent: RequestEventUserSms?)


        //Validacion
        fun validarSendSms(): Boolean


        //Methods
        fun getListSms(context:Activity,smsUser:Sms?)

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getListSms(context:Activity,smsUser:Sms?)


    }

    interface Repository {

        fun getListSms(context:Activity,smsUser:Sms?)
    }

}