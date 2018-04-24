package com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.ofertas.Oferta
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.modules.comercial_module.ofe.OfertasInteractor
import com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas.events.OfertasEvent
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList


class OfertasPresenter(var view: IOfertas.View?) : IOfertas.Presenter {

    var interactor: IOfertas.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<UnidadProductiva>? = ArrayList<UnidadProductiva>()
    var listLoteGlobal: List<Lote>? = ArrayList<Lote>()
    var listCultivosGlobal: List<Cultivo>? = ArrayList<Cultivo>()
    var listProductosGlobal: List<Producto>? = ArrayList<Producto>()

    companion object {
        var instance: OfertasPresenter? = null
    }

    init {
        instance = this
        interactor = OfertasInteractor()
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver)
    }

    override fun getListas() {
        interactor?.getListas()
    }

    @Subscribe
    override fun onEventMainThread(event: OfertasEvent?) {
        when (event?.eventType) {
        //Listas
            OfertasEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = event.mutableList as List<UnidadProductiva>
            }
            OfertasEvent.LIST_EVENT_LOTE -> {
                listLoteGlobal = event.mutableList as List<Lote>
            }

            OfertasEvent.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal = event.mutableList as List<Cultivo>
            }
            OfertasEvent.LIST_EVENT_PRODUCTO -> {
                listProductosGlobal = event.mutableList as List<Producto>
            }
            OfertasEvent.READ_EVENT -> {
                val list = event.mutableList as List<Oferta>
                view?.setListOfertas(list)
            }
            OfertasEvent.GET_EVENT_PRODUCTO -> {
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

    override fun getListOfertas(productoId: Long?) {
        interactor?.getListOfertas(productoId)
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
        val list = listProductosGlobal?.filter { producto: Producto -> producto.CultivoId == cultivo_id }
        view?.setListProductos(list)
    }


    //endregion

}