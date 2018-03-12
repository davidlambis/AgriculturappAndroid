package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.events.RequestEvent

interface LoginPresenter {
    fun onCreate()
    fun onDestroy()
    fun onEventMainThread(event: RequestEvent?)
    fun validarCampos(): Boolean?
    fun ingresar(email: String, password: String)
}