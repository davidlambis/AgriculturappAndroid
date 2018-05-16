package com.interedes.agriculturappv3.activities.registration.register_user.interactor

import com.interedes.agriculturappv3.modules.models.usuario.User


interface RegisterUserInteractor {

    fun registerUsuario(user: User)
    fun loadMetodosPago()
    fun loadDetalleMetodosPagoByMetodoPagoId(Id : Long?)
    fun loadSqliteDetalleMetodosPagoByMetodoPagoId(Id : Long?)
    fun getSqliteMetodosPago()
    //fun getMetodosPago()
}