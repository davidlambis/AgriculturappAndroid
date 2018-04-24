package com.interedes.agriculturappv3.productor.modules.comercial_module.productos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.producto.CalidadProducto
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.events.ProductosEvent
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class ProductosPresenter(var view: IProductos.View?) : IProductos.Presenter {

    var interactor: IProductos.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<UnidadProductiva>? = ArrayList<UnidadProductiva>()
    var listLoteGlobal: List<Lote>? = ArrayList<Lote>()
    var listCultivosGlobal: List<Cultivo>? = ArrayList<Cultivo>()
    var listUnidadMedidaGlobal: List<Unidad_Medida>? = ArrayList<Unidad_Medida>()
    var listCalidadesGlobal: List<CalidadProducto>? = ArrayList<CalidadProducto>()


    companion object {
        var instance: ProductosPresenter? = null
    }

    init {
        instance = this
        interactor = ProductosInteractor()
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
    override fun onEventMainThread(event: ProductosEvent?) {
        when (event?.eventType) {
            ProductosEvent.READ_EVENT -> {
                val list = event.mutableList as List<Producto>
                view?.setListProductos(list)
            }
        //LIST EVENTS
            ProductosEvent.LIST_EVENT_UNIDAD_MEDIDA -> {
                listUnidadMedidaGlobal = event.mutableList as List<Unidad_Medida>
            }

            ProductosEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = event.mutableList as List<UnidadProductiva>
            }

            ProductosEvent.LIST_EVENT_LOTE -> {
                listLoteGlobal = event.mutableList as List<Lote>
            }

            ProductosEvent.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal = event.mutableList as List<Cultivo>
            }

            ProductosEvent.LIST_EVENT_CALIDADES -> {
                listCalidadesGlobal = event.mutableList as List<CalidadProducto>
            }

        //Get Single
            ProductosEvent.GET_EVENT_CULTIVO -> {
                val cultivo = event.objectMutable as Cultivo
                view?.setCultivo(cultivo)
            }
            ProductosEvent.SAVE_EVENT -> {
                val list = event.mutableList as List<Producto>
                view?.setListProductos(list)
                onSaveOk()
            }
            ProductosEvent.DELETE_EVENT -> {
                val list = event.mutableList as List<Producto>
                view?.setListProductos(list)
                view?.requestResponseOK()
            }

        //ITEMS
            ProductosEvent.ITEM_EDIT_EVENT -> {
                val producto = event.objectMutable as Producto
                view?.showAlertDialogAddProducto(producto)
            }

            ProductosEvent.ITEM_DELETE_EVENT -> {
                val producto = event.objectMutable as Producto
                view?.confirmDelete(producto)
            }
        }
    }

    override fun validarCampos(): Boolean? {
        if (view?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun validarListasAddProducto(): Boolean? {
        if (view?.validarListasAddProducto() == true) {
            return true
        }
        return false
    }


    override fun registerProducto(producto: Producto, cultivo_id: Long) {
        view?.disableInputs()
        view?.showDialogProgress()
        interactor?.registerProducto(producto, cultivo_id)
    }

    override fun updateProducto(producto: Producto, cultivo_id: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteProducto(producto: Producto, cultivo_id: Long?) {
        interactor?.deleteProducto(producto, cultivo_id)
    }

    override fun getListProductos(cultivo_id: Long?) {
        interactor?.getListProductos(cultivo_id)
    }


    override fun getCultivo(cultivo_id: Long?) {
        interactor?.getCultivo(cultivo_id)
    }


    //Spinners
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

    override fun setListSpinnerMoneda() {
        view?.setListMoneda(listUnidadMedidaGlobal)
    }

    override fun setListSpinnerCalidadProducto() {
        view?.setListCalidad(listCalidadesGlobal)
    }

    //Métodos
    private fun onSaveOk() {
        view?.enableInputs()
        view?.hideDialogProgress()
        view?.limpiarCampos()
        view?.requestResponseOK()
    }
    //endregion
}