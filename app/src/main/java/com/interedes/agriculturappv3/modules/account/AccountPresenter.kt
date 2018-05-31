package com.interedes.agriculturappv3.modules.account

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.account.events.RequestEventAccount
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class AccountPresenter (var mainView: IMainViewAccount.MainView?):IMainViewAccount.Presenter {


    var interactor: IMainViewAccount.Interactor? = null
    var eventBus: EventBus? = null
    var listMetodoPago: List<MetodoPago>? = ArrayList<MetodoPago>()
    var listDetalleMetodo: List<DetalleMetodoPago>? = ArrayList<DetalleMetodoPago>()

    companion object {
        var instance: AccountPresenter? = null
    }

    init {
        instance = this
        interactor = AccountInteractor()
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
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if (extras != null) {
                mainView?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventAccount?) {
        when (event?.eventType) {

            RequestEventAccount.UPDATE_EVENT -> {
                onMessageOk()
            }

            RequestEventAccount.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventAccount.LIST_EVENT_DETALLE_METODO_PAGO -> {
                listDetalleMetodo = event.mutableList as List<DetalleMetodoPago>
            }

            RequestEventAccount.LIST_EVENT_METODO_PAGO -> {
                listMetodoPago = event.mutableList as List<MetodoPago>
            }

        //Error Conection
            RequestEventAccount.ERROR_VERIFICATE_CONECTION -> {
                onMessageConectionError()
            }
        }
    }
    //endregion

    //region Validations
    override fun validarUpdateUser(): Boolean {
        if (mainView?.validarUpdateUser() == true) {
            return true
        }
        return false
    }
    //endregion

    //region METHODS
    override fun setListSpinnerMetodoPago() {
        mainView?.setListMetodoPago(listMetodoPago)
    }

    override fun setListSpinnerDetalleMetodoPago(metodopagoId: Long?) {
        var list = listDetalleMetodo?.filter { detalleMetodoPago: DetalleMetodoPago -> detalleMetodoPago.MetodoPagoId == metodopagoId }
        mainView?.setListDetalleMetodoPago(list)
    }



    override fun getUserLogued(): Usuario? {
       return  interactor?.getUserLogued()
    }

    override fun updateUserLogued(usuario: Usuario?) {
        mainView?.showProgressHud()
        interactor?.updateUserLogued(usuario,checkConnection())

    }

    override fun getListas() {
       interactor?.getListas()
    }


    //endregion

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.hideProgressHud()
        mainView?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        mainView?.hideProgress()
        mainView?.showProgressHud()
        mainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        mainView?.hideProgress()
        mainView?.showProgressHud()
        mainView?.verificateConnection()
    }


    //endregion
}