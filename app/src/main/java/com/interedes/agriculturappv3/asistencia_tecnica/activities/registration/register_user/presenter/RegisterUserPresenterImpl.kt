package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.presenter

import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractor
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor.RegisterUserInteractorImpl
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.ui.RegisterUserView
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.events.RegisterEvent
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import org.greenrobot.eventbus.Subscribe


class RegisterUserPresenterImpl(var registerUserView: RegisterUserView?) : RegisterUserPresenter, ConnectivityReceiver.connectivityReceiverListener {


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
                onErrorRegistro(event.mensajeError)
            }
            RegisterEvent.onMetodoPagoExitoso -> {
                //onMetodoPagoExitoso()
                val metodosPagoList = event.mutableList as List<MetodoPago>
                registerUserView?.setMetodosPago(metodosPagoList)

            }
            RegisterEvent.onDetalleMetodosPagoExitoso -> {
                val detalleMetodosPagoList = event.mutableList as List<DetalleMetodoPago>
                registerUserView?.setDetalleMetodosPago(detalleMetodosPagoList)
            }
            RegisterEvent.onLoadInfoError -> {
                onLoadInfoError(event.mensajeError)
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
        if (checkConnection()) {
            registerUserView?.disableInputs()
            registerUserView?.showProgress()
            registerUserInteractor?.registerUsuario(usuario)
        } else {
            registerUserView?.hasNotConnectivity()
        }
    }

    override fun loadMetodosPago() {
        if (checkConnection()) {
            registerUserInteractor?.loadMetodosPago()
        } else {
            registerUserView?.getSqliteMetodosPago()
        }
    }

    override fun loadDetalleMetodosPagoByMetodoPagoId(Id: Long?) {
        if (checkConnection()) {
            registerUserInteractor?.loadDetalleMetodosPagoByMetodoPagoId(Id)
        } else {
            registerUserInteractor?.loadSqliteDetalleMetodosPagoByMetodoPagoId(Id)
        }
    }

    override fun getSqliteMetodosPago() {
        registerUserInteractor?.getSqliteMetodosPago()
    }

/*
    override fun getMetodosPago() {
        if (checkConnection()) {
            registerUserInteractor?.getMetodosPago()
        } else {
            registerUserView?.hasNotConnectivity()
        }
    } */

    //Acciones de Respuesta a Post de Eventos
    private fun onRegistroExitoso() {
        registerUserView?.enableInputs()
        registerUserView?.hideProgress()
        registerUserView?.registroExitoso()
    }

    private fun onErrorRegistro(error: String?) {
        registerUserView?.enableInputs()
        registerUserView?.hideProgress()
        registerUserView?.registroError(error)
    }


    /*
    private fun onMetodoPagoExitoso() {
        registerUserView?.loadMetodosPago()
    }*/

    private fun onLoadInfoError(error: String?) {
        registerUserView?.loadMetodosPagoError(error)
        registerUserView?.hideMetodosPago()
    }

    //region Conectividad a Internet
    //Conexión a Internet

    //Revisar manualmente
    private fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            //Snackbar.make(container, getString(R.string.internet_connected), Snackbar.LENGTH_SHORT).show()
        } else {
            //Snackbar.make(container, getString(R.string.not_internet_connected), Snackbar.LENGTH_SHORT).show()
        }
    }

    //endregion


}