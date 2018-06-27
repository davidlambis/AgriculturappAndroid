package com.interedes.agriculturappv3.activities.login.interactor

import android.content.Context
import com.interedes.agriculturappv3.activities.login.repository.LoginRepository
import com.interedes.agriculturappv3.activities.login.repository.LoginRepositoryImpl
import com.interedes.agriculturappv3.modules.models.login.Login


class LoginInteractorImpl : LoginInteractor {

    var loginRepository: LoginRepository? = null

    init {
        loginRepository = LoginRepositoryImpl()
    }

    override fun ingresar(login: Login, context: Context) {
        loginRepository?.ingresar(login,context)
    }

    override fun getSqliteUsuario(login: Login,context:Context) {
        loginRepository?.getSqliteUsuario(login,context)
    }

    override fun resetPassword(correo: String) {
        loginRepository?.resetPassword(correo)
    }

}