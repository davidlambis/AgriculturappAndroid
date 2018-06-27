package com.interedes.agriculturappv3.activities.login.presenter

import android.content.Context
import com.interedes.agriculturappv3.activities.login.events.LoginEvent
import com.interedes.agriculturappv3.modules.models.login.Login
import com.interedes.agriculturappv3.events.RequestEvent

interface LoginPresenter {
    fun onCreate()
    fun onDestroy()
    fun onEventMainThread(event: LoginEvent?)
    fun validarCampos(): Boolean?

    fun ingresar(login: Login, context: Context)
    fun resetPassword(correo : String)
}