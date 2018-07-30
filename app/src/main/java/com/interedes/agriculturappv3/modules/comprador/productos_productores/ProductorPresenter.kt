package com.interedes.agriculturappv3.modules.comprador.productos_productores

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.productos_productores.events.RequestEventProductor
import com.interedes.agriculturappv3.modules.comprador.productos_productores.resources.RequestFilter
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList


class ProductorPresenter(var mainView: IMainViewProductor.MainView?):IMainViewProductor.Presenter {

    var interactor: IMainViewProductor.Interactor? = null
    var eventBus: EventBus? = null

    //List
    var listDepartamentoGlobal: List<Departamento>? = ArrayList<Departamento>()
    var listMunicipiosGlobal: List<Ciudad>? = ArrayList<Ciudad>()

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
    override fun onEventMainThread(event: RequestEventProductor?) {
        when (event?.eventType) {
            RequestEventProductor.READ_EVENT -> {
                val list = event.mutableList as List<Producto>
                listTipoProducto=list
                mainView?.setListProducto(list)
                mainView?.hideProgress()

                mainView?.hideProgressHud()

            }

            RequestEventProductor.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventProductor.ITEM_EVENT -> {
                val producto = event.objectMutable as Producto
                mainView?.navigateDetalleTipoProductoUser(producto)
            }

            RequestEventProductor.ITEM_NEW_EVENT -> {
                val producto = event.objectMutable as Producto
                mainView?.addNewItem(producto)
                mainView?.hideProgressHud()
            }

            RequestEventProductor.LIST_EVENT -> {
                val listProducto = event.mutableList as List<Producto>
                mainView?.setListProducto(listProducto)
                mainView?.hideProgressHud()
                mainView?.hideProgress()
            }

            RequestEventProductor.LOAD_DATA_FIRTS -> {
                val list = event.mutableList as List<Producto>
                listTipoProducto=list
                mainView?.setListProductoFirts(list)
                mainView?.hideProgress()
                mainView?.hideProgressHud()
            }
        }
    }
    //endregion

    //region Request Repository
    override fun  getListProducto(filter: RequestFilter) {
        filter.checkConection=checkConnection()
        interactor?.execute(filter)
    }

    override fun getTipoProducto(tipoProducto: Long): TipoProducto? {
        return interactor?.getTipoProducto(tipoProducto)
    }

    override fun getListDepartmentCities() {
        interactor?.getListDepartmentCities()
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
        mainView?.hideProgressHud()
        mainView?.verificateConnection()
    }
    //endregion


}