package com.interedes.agriculturappv3.activities.login.repository

import android.content.Context
import com.interedes.agriculturappv3.modules.models.login.Login

interface LoginRepository {

    fun ingresar(login: Login, context: Context)
    fun getSqliteUsuario(login: Login,context:Context)
    fun resetPassword(correo : String)
    //fun updateUsuario(usuario: Usuario)
    //fun getSqliteUsuario(usuario : Usuario)
}