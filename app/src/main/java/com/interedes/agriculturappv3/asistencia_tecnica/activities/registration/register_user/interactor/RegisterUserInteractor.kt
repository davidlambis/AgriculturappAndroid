package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago


interface RegisterUserInteractor {

    fun registerUsuario(usuario : Usuario)
    fun loadMetodosPago()
    fun loadDetalleMetodosPagoByMetodoPagoId(Id : Long?)
    fun loadSqliteDetalleMetodosPagoByMetodoPagoId(Id : Long?)
    fun getSqliteMetodosPago()
    //fun getMetodosPago()
}