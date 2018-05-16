package com.interedes.agriculturappv3.activities.registration.register_user.presenter

import com.interedes.agriculturappv3.activities.registration.register_user.events.RegisterEvent
import com.interedes.agriculturappv3.modules.models.usuario.User

interface RegisterUserPresenter {

    fun onCreate()
    fun onDestroy()
    fun onEventMainThread(event: RegisterEvent?)
    fun validarCampos(): Boolean?
    fun registerUsuario(user: User)
    fun loadMetodosPago()
    fun loadDetalleMetodosPagoByMetodoPagoId(Id:Long?)
    fun getSqliteMetodosPago()
    //fun getMetodosPago()
}