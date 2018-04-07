package com.interedes.agriculturappv3.activities.login.presenter

import com.interedes.agriculturappv3.productor.models.login.Login
import com.interedes.agriculturappv3.events.RequestEvent

interface LoginPresenter {
    fun onCreate()
    fun onDestroy()
    fun onEventMainThread(event: RequestEvent?)
    fun validarCampos(): Boolean?

    fun ingresar(login: Login)
}