package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.login.Login
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario

interface LoginRepository {

    fun ingresar(login: Login)
    fun getSqliteUsuario(login: Login)
    //fun updateUsuario(usuario: Usuario)
    //fun getSqliteUsuario(usuario : Usuario)
}