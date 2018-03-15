package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.login.Login

interface LoginInteractor {

    fun ingresar(login: Login)
    fun getSqliteUsuario(login: Login)
}