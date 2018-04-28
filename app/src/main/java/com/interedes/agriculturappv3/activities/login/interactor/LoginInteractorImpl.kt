package com.interedes.agriculturappv3.activities.login.interactor

import com.interedes.agriculturappv3.activities.login.repository.LoginRepository
import com.interedes.agriculturappv3.activities.login.repository.LoginRepositoryImpl
import com.interedes.agriculturappv3.productor.models.login.Login


class LoginInteractorImpl : LoginInteractor {

    var loginRepository: LoginRepository? = null

    init {
        loginRepository = LoginRepositoryImpl()
    }

    override fun ingresar(login: Login) {
        loginRepository?.ingresar(login)
    }

    override fun getSqliteUsuario(login: Login) {
        loginRepository?.getSqliteUsuario(login)
    }

    override fun resetPassword(correo: String) {
        loginRepository?.resetPassword(correo)
    }

}