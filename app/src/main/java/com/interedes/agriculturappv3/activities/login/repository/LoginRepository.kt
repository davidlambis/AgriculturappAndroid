package com.interedes.agriculturappv3.activities.login.repository

import com.interedes.agriculturappv3.productor.models.login.Login

interface LoginRepository {

    fun ingresar(login: Login)
    fun getSqliteUsuario(login: Login)
    fun resetPassword(correo : String)
    //fun updateUsuario(usuario: Usuario)
    //fun getSqliteUsuario(usuario : Usuario)
}