package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui.RegisterUserView
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.events.RegisterEvent
import com.interedes.agriculturappv3.events.RegisterEvent.Companion.onErrorRegistro
import com.interedes.agriculturappv3.events.RegisterEvent.Companion.onRegistroExitoso
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
        when (event?.eventType) {
            RegisterEvent.onRegistroExitoso -> {
                onRegistroExitoso()
            }
            RegisterEvent.onErrorRegistro -> {
                onErrorRegistro(event.mensajeError!!)
            }
        }
    }

    override fun validarCampos(): Boolean? {
        if (registerUserView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerUsuario(usuario: Usuario) {
        registerUserView?.disableInputs()
        registerUserView?.showProgress()
        registerUserInteractor?.registerUsuario(usuario)
    }

    //Acciones de Respuesta a Post de Eventos
    private fun onRegistroExitoso() {
        registerUserView?.enableInputs()
        registerUserView?.hideProgress()
        registerUserView?.registroExitoso()
    }

    private fun onErrorRegistro(error: String) {
        registerUserView?.enableInputs()
        registerUserView?.hideProgress()
        registerUserView?.registroError(error)
    }


}