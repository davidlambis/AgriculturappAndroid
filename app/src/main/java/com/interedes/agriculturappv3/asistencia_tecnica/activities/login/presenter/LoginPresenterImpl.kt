package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.interactor.LoginInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.interactor.LoginInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.activities.login.ui.LoginView
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
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
    override fun onEventMainThread(event: RequestEvent?) {

    }

    override fun validarCampos(): Boolean? {
        if (loginView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun ingresar(email: String, password: String) {
        loginView?.disableInputs()
        loginView?.showProgress()
        loginInteractor?.ingresar(email, password)
    }
    //endregion

    //region Eventos


    //endregion
    /*
    //region Conexión a Internet
    override fun onNetworkConnectionChanged(isConnected: Boolean) {

    }
    //endregion*/

}