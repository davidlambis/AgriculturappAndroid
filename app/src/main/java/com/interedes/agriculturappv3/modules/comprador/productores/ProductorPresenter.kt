package com.interedes.agriculturappv3.modules.comprador.productores

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList


class ProductorPresenter(var mainView: IMainViewProductor.MainView?):IMainViewProductor.Presenter {

    var interactor: IMainViewProductor.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listTipoProducto:List<Producto>?= ArrayList<Producto>()

    companion object {
        var instance: ProductorPresenter? = null
    }

    init {
        instance = this
        interactor = ProductorInteractor()
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventProductor?) {
        when (event?.eventType) {
            RequestEventProductor.READ_EVENT -> {
                var list = event.mutableList as List<Producto>
                listTipoProducto=list
                mainView?.setListProducto(list)
                mainView?.hideProgress()
               // mainView?.hideProgressHud()

            }

            RequestEventProductor.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventProductor.ITEM_EVENT -> {
                var producto = event.objectMutable as Producto
                mainView?.navigateDetalleTipoProductoUser(producto)
            }


            RequestEventProductor.ITEM_NEW_EVENT -> {
                var producto = event.objectMutable as Producto
                mainView?.addNewItem(producto)
                mainView?.hideProgressHud()
            }

            RequestEventProductor.LIST_EVENT -> {
                var listProducto = event.mutableList as List<Producto>
                mainView?.setListProducto(listProducto)
                mainView?.hideProgressHud()
            }

            RequestEventProductor.LOAD_DATA_FIRTS -> {
                var list = event.mutableList as List<Producto>
                listTipoProducto=list
                mainView?.setListProductoFirts(list)
                mainView?.hideProgress()
                mainView?.hideProgressHud()


            }

        }
    }
    //endregion

    //region Request Repository
    override fun getListProducto(tipoProducto:Long,top:Int,skip:Int,isFirst:Boolean) {
        mainView?.showProgress()
        //mainView?.showProgressHud()
        interactor?.execute(checkConnection(),tipoProducto,top,skip,isFirst)
    }

    override fun getTipoProducto(tipoProducto: Long): TipoProducto? {
        return interactor?.getTipoProducto(tipoProducto)
    }

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.hideProgress()
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