package com.interedes.agriculturappv3.modules.productor.comercial_module.ventas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.compras.Compras
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.productor.comercial_module.ventas.events.VentasEvent
import com.interedes.agriculturappv3.services.resources.Const_Resources
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class VentasPresenter(var view: IVentas.View?) : IVentas.Presenter {

    var interactor: IVentas.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<Unidad_Productiva>? = ArrayList<Unidad_Productiva>()
    var listLoteGlobal: List<Lote>? = ArrayList<Lote>()
    var listCultivosGlobal: List<Cultivo>? = ArrayList<Cultivo>()
    var listProductosGlobal: List<Producto>? = ArrayList<Producto>()

    companion object {
        var instance: VentasPresenter? = null
    }

    init {
        instance = this
        interactor = VentasInteractor()
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)
        getListas()
    }

    override fun onDestroy() {
        view = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val extras = intent.extras
            if (extras != null) {
                view?.onEventBroadcastReceiver(extras, intent)
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
        context.unregisterReceiver(this.mNotificationReceiver)
    }

    override fun getListas() {
        interactor?.getListas()
    }

    @Subscribe
    override fun onEventMainThread(event: VentasEvent?) {
        when (event?.eventType) {
        //Listas
            VentasEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = event.mutableList as List<Unidad_Productiva>
            }
            VentasEvent.LIST_EVENT_LOTE -> {
                listLoteGlobal = event.mutableList as List<Lote>
            }

            VentasEvent.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal = event.mutableList as List<Cultivo>
            }
            VentasEvent.LIST_EVENT_PRODUCTO -> {
                listProductosGlobal = event.mutableList as List<Producto>
            }
            VentasEvent.READ_EVENT -> {
                val list = event.mutableList as List<Compras>
                view?.setListVentas(list)
            }
            VentasEvent.GET_EVENT_PRODUCTO -> {
                val producto = event.objectMutable as Producto
                view?.setProducto(producto)
            }
        }
    }

    override fun validarListas(): Boolean? {
        if (view?.validarListas() == true) {
            return true
        }
        return false
    }

    override fun getListVentas(productoId: Long?) {
        interactor?.getListVentas(productoId)
    }

    override fun getProducto(productoId: Long?) {
        interactor?.getProducto(productoId)
    }

    //SET Listas
    override fun setListSpinnerUnidadProductiva() {
        view?.setListUnidadProductiva(listUnidadProductivaGlobal)
    }

    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
        val list = listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id == unidad_productiva_id }
        view?.setListLotes(list)
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        val list = listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId == lote_id }
        view?.setListCultivos(list)
    }

    override fun setListSpinnerProducto(cultivo_id: Long?) {
        val list = listProductosGlobal?.filter { producto: Producto -> producto.cultivoId == cultivo_id }
        view?.setListProductos(list)
    }


    //endregion
}