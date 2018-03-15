package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.models.login.Login
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver

interface LoginPresenter {
    fun onCreate()
    fun onDestroy()
    fun onEventMainThread(event: RequestEvent?)
    fun validarCampos(): Boolean?

    fun ingresar(login: Login)
}