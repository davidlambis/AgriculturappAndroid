package com.interedes.agriculturappv3.activities.login.interactor

import android.content.Context
import com.interedes.agriculturappv3.modules.models.login.Login

interface LoginInteractor {

    fun ingresar(login: Login, context: Context)
    fun getSqliteUsuario(login: Login,context:Context)
    fun resetPassword(correo : String)
}