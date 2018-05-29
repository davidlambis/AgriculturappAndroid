package com.interedes.agriculturappv3.modules.comprador.detail_producto

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.google.firebase.database.DatabaseReference
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.comprador.detail_producto.events.RequestEventDetalleProducto
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList


class DetailProductoPresenter(var mainView: IMainViewDetailProducto.MainView?):IMainViewDetailProducto.Presenter {

    var interactor: IMainViewDetailProducto.Interactor? = null
    var eventBus: EventBus? = null


    //GLOBALS
    var listTipoProducto:List<Producto>?= ArrayList<Producto>()

    companion object {
        var instance: DetailProductoPresenter? = null
    }

    init {
        instance = this
        interactor = DetailProductoInteractor()
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
    override fun onEventMainThread(event: RequestEventDetalleProducto?) {
        when (event?.eventType) {


            RequestEventDetalleProducto.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventDetalleProducto.PRODUCTO_EVENT -> {
                var producto = event.objectMutable as Producto
            }



        }
    }
    //endregion

    //region VALIDACIONES
    //region Methods
    override fun validarCamposAddOferta(): Boolean? {
        if (mainView?.validarListasAddOferta() == true) {
            return true
        }
        return false
    }

    //endregion

    //region METHODS
    override fun getProducto(producto_id: Long): Producto? {
       return interactor?.getProducto(producto_id)
    }

    override fun getTipoProducto(tipo_producto_id: Long): TipoProducto? {
        return  interactor?.getTipoProducto(tipo_producto_id)
    }
    override fun verificateCantProducto(producto_id: Long?, catnidad: Double?): Boolean? {
       return interactor?.verificateCantProducto(producto_id,catnidad)
    }



    //endregion

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