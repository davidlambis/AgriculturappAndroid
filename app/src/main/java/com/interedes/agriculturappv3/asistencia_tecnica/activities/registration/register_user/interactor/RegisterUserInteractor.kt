package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.User


interface RegisterUserInteractor {

    fun registerUsuario(user: User)
    fun loadMetodosPago()
    fun loadDetalleMetodosPagoByMetodoPagoId(Id : Long?)
    fun loadSqliteDetalleMetodosPagoByMetodoPagoId(Id : Long?)
    fun getSqliteMetodosPago()
    //fun getMetodosPago()
}