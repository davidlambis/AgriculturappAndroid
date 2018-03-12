package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository.LoginRepository
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository.LoginRepositoryImpl


class LoginInteractorImpl : LoginInteractor{

    var loginRepository : LoginRepository ?= null

    init {
        loginRepository = LoginRepositoryImpl()
    }

    override fun ingresar(email: String, password: String) {

    }

}