package com.interedes.agriculturappv3.modules.credentials

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.interedes.agriculturappv3.modules.credentials.events.RequestCredentialsEvents
import com.interedes.agriculturappv3.modules.models.usuario.RequestCredentials
import com.interedes.agriculturappv3.util.ConexionInternet

interface IMainViewCredentials {

    interface MainView {
        //Progress and progress Hud
        fun showProgressHud()
        fun hideProgressHud()

        //Validaciones
        fun validarChangeCredentials(): Boolean
        fun limpiarCampos()

        //Response Notify
        fun requestResponseOK()
        fun requestResponseError(error: String?)
        fun onMessageOk(colorPrimary: Int, msg: String?)
        fun onMessageError(colorPrimary: Int, msg: String?)

        //Dialog
        fun verificateConnection(): AlertDialog?

        //Events
        fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun onResume(context: Context)
        fun onPause(context: Context)

        //Events
        fun onEventMainThread(requestEvent: RequestCredentialsEvents?)

        //Validacion
        fun validarChangeCredentials(): Boolean

        //Methods
        fun updateCredentialsUserLogued(credentials: RequestCredentials)

        //Conecttion
        fun checkConnection(): Boolean
    }

    interface Interactor {
        fun updateCredentialsUserLogued(checkConection: Boolean,credentials: RequestCredentials)
    }

    interface Repository {
        fun updateCredentialsUserLogued(checkConection: Boolean,credentials: RequestCredentials)
    }

}