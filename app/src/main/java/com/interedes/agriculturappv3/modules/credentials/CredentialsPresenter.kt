package com.interedes.agriculturappv3.modules.credentials

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.credentials.events.RequestCredentialsEvents
import com.interedes.agriculturappv3.modules.models.usuario.RequestCredentials
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.interedes.agriculturappv3.services.resources.Const_Resources
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class CredentialsPresenter (var mainView: IMainViewCredentials.MainView?):IMainViewCredentials.Presenter {



    var interactor: IMainViewCredentials.Interactor? = null
    var eventBus: EventBus? = null

    init {
        interactor = CredentialsInteractor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)

    }

    override fun onDestroy() {
        mainView = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiverApp = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                mainView?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiverApp);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestCredentialsEvents?) {
        when (event?.eventType) {

            RequestCredentialsEvents.UPDATE_EVENT -> {
                onMessageOk()

            }

            RequestCredentialsEvents.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestCredentialsEvents.ERROR_PASSWORD_EVENT -> {
                onMessageErrorUpdatePassword(event.mensajeError)
            }

            //Error Conection
            RequestCredentialsEvents.ERROR_VERIFICATE_CONECTION -> {
                onMessageConectionError()
            }
        }
    }
    //endregion

    //region methods
    override fun updateCredentialsUserLogued(credentials: RequestCredentials) {
        mainView?.showProgressHud()
       interactor?.updateCredentialsUserLogued(checkConnection(),credentials)
    }
    //endregion
    //region Validations
    override fun validarChangeCredentials(): Boolean {
        if (mainView?.validarChangeCredentials() == true) {
            return true
        }
        return false
    }
    //endregion

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.limpiarCampos()
        mainView?.hideProgressHud()
        mainView?.requestResponseOK()
    }

    private fun onMessageErrorUpdatePassword(error: String?) {
        mainView?.hideProgressHud()
        mainView?.requestResponseError(error)
    }

    private fun onMessageError(error: String?) {
        mainView?.hideProgressHud()
        mainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        mainView?.hideProgressHud()
        mainView?.verificateConnection()
    }


    //endregion




}