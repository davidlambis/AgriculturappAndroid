package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario

interface LoginPresenter {
    fun onCreate()
    fun onDestroy()
    fun onEventMainThread()
    fun validarCampos(): Boolean?
    fun ingresar(email: String, password: String)
}