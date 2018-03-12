package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago

interface RegisterUserRepository {

    fun registerUsuario(usuario: Usuario)
    //Métodos Pago
    fun loadMetodosPago()
    fun getMetodosPago(): List<MetodoPago>
    fun getSqliteMetodosPago()

    //Detalle Métodos Pago
    fun getDetalleMetodosPagoByMetodoPagoId(Id: Long?): List<DetalleMetodoPago>
    fun loadDetalleMetodosPagoByMetodoPagoId(Id: Long?)
    fun loadSqliteDetalleMetodosPagoByMetodoPagoId(Id: Long?)
}