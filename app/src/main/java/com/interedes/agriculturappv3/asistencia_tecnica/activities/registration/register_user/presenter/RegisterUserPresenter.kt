package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.events.RegisterEvent

interface RegisterUserPresenter {

    fun onCreate()
    fun onDestroy()
    fun onEventMainThread(event: RegisterEvent?)
    fun validarCampos(): Boolean?
    fun registerUsuario(usuario: Usuario)
    fun loadMetodosPago()
    fun loadDetalleMetodosPagoByMetodoPagoId(Id:Long?)
    fun getSqliteMetodosPago()
    //fun getMetodosPago()
}