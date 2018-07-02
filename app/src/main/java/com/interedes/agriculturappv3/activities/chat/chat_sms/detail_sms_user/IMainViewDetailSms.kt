package com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user.events.RequestEventSmsDetail
import com.interedes.agriculturappv3.modules.models.sms.Sms

interface IMainViewDetailSms {

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
        fun setListSmsDetaiil(sms: List<Sms>)


        fun getListSms(eventClickNotification: Boolean)



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
        fun onEventMainThread(requestEvent: RequestEventSmsDetail?)


        //Validacion
        fun validarSendSms(): Boolean


        //Methods
        fun getListSms(context: Activity, sms: Sms?,eventNotification:Boolean)

        fun saveSms(context:Context,sms: Sms?)

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun getListSms(context: Activity, smsUser: Sms?,eventNotification:Boolean)


    }

    interface Repository {

        fun getListSms(context: Activity, smsUser: Sms?,eventNotification:Boolean)
    }
}