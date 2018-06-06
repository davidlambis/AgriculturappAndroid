package com.interedes.agriculturappv3.modules.account

import com.google.firebase.auth.FirebaseUser
import com.interedes.agriculturappv3.modules.models.usuario.Usuario

class AccountInteractor :IMainViewAccount.Interactor {



    var repository: IMainViewAccount.Repository? = null

    init {
        repository = AccountRepository()
    }

    override  fun getUserLogued():Usuario? {
      return repository?.getUserLogued()
    }

    override fun  updateUserLogued(usuario:Usuario,checkConction:Boolean)
    {
        repository?.updateUserLogued(usuario,checkConction)
    }

    override fun getListas() {
        repository?.getListas()
    }

    override fun verificateUserLoguedFirebaseFirebase(): FirebaseUser?
    {
        return  repository?.verificateUserLoguedFirebaseFirebase()
    }

    override fun changeFotoUserAccount(checkConction: Boolean) {
        repository?.changeFotoUserAccount(checkConction)
    }

}