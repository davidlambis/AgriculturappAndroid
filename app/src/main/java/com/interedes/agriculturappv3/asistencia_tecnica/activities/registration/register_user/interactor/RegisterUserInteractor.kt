package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario


interface RegisterUserInteractor {

    fun registerUsuario(usuario : Usuario)
}