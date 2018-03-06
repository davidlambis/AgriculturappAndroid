package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui.RegisterUserView
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.events.RegisterEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import org.greenrobot.eventbus.Subscribe


class RegisterUserPresenterImpl(var registerUserView: RegisterUserView?) : RegisterUserPresenter {

    var registerUserInteractor: RegisterUserInteractor? = null
    var eventBus: EventBus? = null

    init {
        registerUserInteractor = RegisterUserInteractorImpl()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)

        //EventBus
    }

    override fun onDestroy() {
        registerUserView = null
        eventBus?.unregister(this)
    }

    @Subscribe
    override fun onEventMainThread(event: RegisterEvent?) {
        when(event?.eventType){

        }
    }

    override fun registerUsuario(usuario: Usuario) {
        registerUserView?.validarCampos()
        if (registerUserView?.validarCampos() == true) {
            /*registerUserView.disableInputs()
            registerUserView.showProgress()*/
            registerUserInteractor?.registerUsuario(usuario)
        }
    }


}