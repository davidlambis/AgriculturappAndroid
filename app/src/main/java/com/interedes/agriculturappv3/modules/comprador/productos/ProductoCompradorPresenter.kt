package com.interedes.agriculturappv3.modules.comprador.productos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productos.events.RequestEventProductosComprador
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class ProductoCompradorPresenter(var mainView: IMainViewProductoComprador.MainView?):IMainViewProductoComprador.Presenter {

    var interactor: IMainViewProductoComprador.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listTipoProducto:List<TipoProducto>?= ArrayList<TipoProducto>()

    companion object {
        var instance: ProductoCompradorPresenter? = null
    }

    init {
        instance = this
        interactor = ProductoCompradorInteractor()
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventProductosComprador?) {
        when (event?.eventType) {
            RequestEventProductosComprador.LIST_EVENT_TIPO_PRODUCTO -> {
                val list = event.mutableList as List<TipoProducto>
                listTipoProducto=list
                mainView?.setListTipoProducto(list)
                mainView?.hideProgress()
                mainView?.hideProgressHud()

            }

            RequestEventProductosComprador.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventProductosComprador.ITEM_EVENT -> {
                val tipoProducto = event.objectMutable as TipoProducto
                mainView?.navigateDetalleTipoProducto(tipoProducto.Id!!)
            }
        }
    }
    //endregion

    //region Request Repository
    override fun getListTipoProducto() {
        mainView?.showProgress()
        mainView?.showProgressHud()
       interactor?.execute(checkConnection())
    }


    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.hideProgress()
        mainView?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        mainView?.hideProgress()
        mainView?.hideProgressHud()
        mainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        mainView?.hideProgress()
        mainView?.showProgressHud()
        mainView?.verificateConnection()
    }
    //endregion

}