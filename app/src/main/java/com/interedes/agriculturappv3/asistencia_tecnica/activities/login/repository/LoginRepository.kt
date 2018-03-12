package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario

interface LoginRepository {

    fun ingresar(email: String, password: String)
    //fun updateUsuario(usuario: Usuario)
    //fun getUsuario(usuario : Usuario)
}