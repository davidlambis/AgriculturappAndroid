package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository.LoginRepository
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository.LoginRepositoryImpl
import com.interedes.agriculturappv3.asistencia_tecnica.models.login.Login


class LoginInteractorImpl : LoginInteractor{

    var loginRepository : LoginRepository ?= null

    init {
        loginRepository = LoginRepositoryImpl()
    }

    override fun ingresar(login : Login) {
        loginRepository?.ingresar(login)
    }

    override fun getSqliteUsuario(login: Login) {
        loginRepository?.getSqliteUsuario(login)
    }

}