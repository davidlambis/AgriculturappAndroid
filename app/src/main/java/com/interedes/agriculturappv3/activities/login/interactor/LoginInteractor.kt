package com.interedes.agriculturappv3.activities.login.interactor

import com.interedes.agriculturappv3.productor.models.login.Login

interface LoginInteractor {

    fun ingresar(login: Login)
    fun getSqliteUsuario(login: Login)
}