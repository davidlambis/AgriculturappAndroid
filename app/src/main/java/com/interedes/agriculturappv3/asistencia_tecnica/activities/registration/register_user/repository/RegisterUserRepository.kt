package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario

interface RegisterUserRepository {

    fun registerUsuario(usuario: Usuario)
}