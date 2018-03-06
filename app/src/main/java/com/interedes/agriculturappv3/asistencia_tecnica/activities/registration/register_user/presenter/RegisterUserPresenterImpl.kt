package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui.RegisterUserView
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.events.RegisterEvent


class RegisterUserPresenterImpl(val registerUserView: RegisterUserView) : RegisterUserPresenter {

    var registerUserInteractor: RegisterUserInteractor? = null

    override fun onCreate() {
        registerUserInteractor = RegisterUserInteractorImpl()
        //EventBus
    }

    override fun onDestroy() {

    }

    override fun onEventMainThread(event: RegisterEvent?) {

    }

    override fun registerUsuario(usuario: Usuario) {
        registerUserView.validarCampos()
        if (registerUserView.validarCampos() == true) {
            /*registerUserView.disableInputs()
            registerUserView.showProgress()*/
            registerUserInteractor?.registerUsuario(usuario)
        }
    }


}