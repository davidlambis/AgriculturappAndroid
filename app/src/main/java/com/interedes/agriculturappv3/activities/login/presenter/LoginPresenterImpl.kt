package com.interedes.agriculturappv3.activities.login.presenter

import android.support.v4.content.ContextCompat
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.login.events.LoginEvent
import com.interedes.agriculturappv3.activities.login.interactor.LoginInteractor
import com.interedes.agriculturappv3.activities.login.interactor.LoginInteractorImpl
import com.interedes.agriculturappv3.activities.login.ui.LoginView
import com.interedes.agriculturappv3.productor.models.login.Login
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import org.greenrobot.eventbus.Subscribe

class LoginPresenterImpl(var loginView: LoginView?) : LoginPresenter {

    var loginInteractor: LoginInteractor? = null
    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        loginInteractor = LoginInteractorImpl()
    }

    //region Interfaz
    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        loginView = null
        eventBus?.unregister(this)
    }

    @Subscribe
    override fun onEventMainThread(event: LoginEvent?) {
        when (event?.eventType) {
            LoginEvent.ERROR_EVENT -> {
                onErrorIngresar(event.mensajeError)
            }
            LoginEvent.SAVE_EVENT -> {
                loginView?.navigateToMainActivity()
            }
            LoginEvent.RESET_PASSWORD_EVENT -> {
                loginView?.hideProgress()
                loginView?.onMessageOk(R.color.colorPrimary, "Contraseña Actualizada.Revisa tu correo")
            }
        }
    }

    override fun validarCampos(): Boolean? {
        if (loginView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun ingresar(login: Login) {
        loginView?.disableInputs()
        loginView?.showProgress()
        if (loginView?.checkConnection()!!) {
            loginInteractor?.ingresar(login)
        } else {
            loginInteractor?.getSqliteUsuario(login)
        }
    }

    override fun resetPassword(correo: String) {
        if (loginView?.checkConnection()!!) {
            loginView?.disableInputs()
            loginView?.showProgress()
            loginInteractor?.resetPassword(correo)
        } else {
            loginView?.onMessageError(R.color.grey_luiyi, "No Conectado a Internet")
        }
    }
    //endregion

    //region Métodos de Respuesta de Eventos
    fun onErrorIngresar(error: String?) {
        loginView?.enableInputs()
        loginView?.hideProgress()
        loginView?.errorIngresar(error)
    }

    //endregion
}