package com.interedes.agriculturappv3.modules.credentials

import com.interedes.agriculturappv3.modules.models.usuario.RequestCredentials

class CredentialsInteractor:IMainViewCredentials.Interactor {


    var repository: IMainViewCredentials.Repository? = null

    init {
        repository = CredentialsRepository()
    }

    override fun updateCredentialsUserLogued(checkConection: Boolean, credentials: RequestCredentials) {
       repository?.updateCredentialsUserLogued(checkConection,credentials)
    }

}