package com.interedes.agriculturappv3.activities.login.interactor

import com.interedes.agriculturappv3.modules.models.login.Login

interface LoginInteractor {

    fun ingresar(login: Login)
    fun getSqliteUsuario(login: Login)
    fun resetPassword(correo : String)
}