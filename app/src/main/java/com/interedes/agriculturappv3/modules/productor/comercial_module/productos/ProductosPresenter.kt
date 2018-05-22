package com.interedes.agriculturappv3.modules.productor.comercial_module.productos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.producto.CalidadProducto
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.productor.comercial_module.productos.events.ProductosEvent
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class ProductosPresenter(var view: IProductos.View?) : IProductos.Presenter {

    var interactor: IProductos.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<Unidad_Productiva>? = ArrayList<Unidad_Productiva>()
    var listLoteGlobal: List<Lote>? = ArrayList<Lote>()
    var listCultivosGlobal: List<Cultivo>? = ArrayList<Cultivo>()
    var listUnidadMedidaGlobalCantidades: List<Unidad_Medida>? = ArrayList<Unidad_Medida>()
    var listUnidadMedidaGlobalPrecios: List<Unidad_Medida>? = ArrayList<Unidad_Medida>()
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

            ProductosEvent.ERROR_EVENT -> {
                view?.hideProgressHud()
                view?.requestResponseError(event.mensajeError)
            }
        //LIST EVENTS
            ProductosEvent.LIST_EVENT_UNIDAD_MEDIDA_PRICE -> {
                listUnidadMedidaGlobalPrecios = event.mutableList as List<Unidad_Medida>
            }

            ProductosEvent.LIST_EVENT_UNIDAD_MEDIDA_CANTIDAD -> {
                listUnidadMedidaGlobalCantidades = event.mutableList as List<Unidad_Medida>
            }

            ProductosEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = event.mutableList as List<Unidad_Productiva>
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
                view?.hideProgressHud()
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


        //Error Conection
            ProductosEvent.ERROR_VERIFICATE_CONECTION -> {
                onMessageConnectionError()
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
        view?.showProgressHud()
        interactor?.registerProducto(producto, cultivo_id,checkConnection())
    }

    override fun updateProducto(mProducto: Producto, cultivo_id: Long) {
        view?.disableInputs()
        view?.showProgressHud()
        interactor?.updateProducto(mProducto, cultivo_id,checkConnection())

    }

    override fun deleteProducto(producto: Producto, cultivo_id: Long?) {
        view?.showProgressHud()
        interactor?.deleteProducto(producto, cultivo_id,checkConnection())

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
        view?.setListMoneda(listUnidadMedidaGlobalPrecios)
    }

    override fun setListSpinnerCantidades() {
        view?.setListUnidadCantidades(listUnidadMedidaGlobalCantidades)
    }

    override fun setListSpinnerCalidadProducto() {
        view?.setListCalidad(listCalidadesGlobal)
    }

    //Métodos
    private fun onSaveOk() {
        view?.enableInputs()
        view?.hideProgressHud()
        view?.limpiarCampos()
        view?.requestResponseOK()
    }

    private fun onMessageConnectionError() {
        view?.hideProgressHud()
        view?.verificateConnection()
    }
    //endregion
}