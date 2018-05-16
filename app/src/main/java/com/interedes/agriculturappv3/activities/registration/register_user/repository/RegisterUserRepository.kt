package com.interedes.agriculturappv3.activities.registration.register_user.repository

import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.modules.models.usuario.User

interface RegisterUserRepository {

    fun registerUsuario(user: User)
    //Métodos Pago
    fun loadMetodosPago()
    fun getMetodosPago(): List<MetodoPago>
    fun getSqliteMetodosPago()

    //Detalle Métodos Pago
    fun getDetalleMetodosPagoByMetodoPagoId(Id: Long?): List<DetalleMetodoPago>
    fun loadDetalleMetodosPagoByMetodoPagoId(Id: Long?)
    fun loadSqliteDetalleMetodosPagoByMetodoPagoId(Id: Long?)
}