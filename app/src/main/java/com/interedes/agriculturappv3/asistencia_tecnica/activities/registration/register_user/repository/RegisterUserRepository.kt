package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago

interface RegisterUserRepository {

    fun registerUsuario(usuario: Usuario)
    fun loadInfo()
    fun getMetodosPago(): List<MetodoPago>
}