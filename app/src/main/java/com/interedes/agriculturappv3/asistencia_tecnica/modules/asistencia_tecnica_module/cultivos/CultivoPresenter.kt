package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.*
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.events.CultivoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class CultivoPresenter(var view: ICultivo.View?) : ICultivo.Presenter {



    var interactor: ICultivo.Interactor? = null
    var eventBus: EventBus? = null
    var listUnidadProductivaGlobal:List<UnidadProductiva>?= ArrayList<UnidadProductiva>()
    var listLoteGlobal:List<Lote>?= ArrayList<Lote>()

    var listTipoProductosGlobal:List<TipoProducto>?= ArrayList<TipoProducto>()
    var listDetalleTipoProductosGlobal:List<DetalleTipoProducto>?= ArrayList<DetalleTipoProducto>()
    var listUnidadMedidasGlobal:List<Unidad_Medida>?= ArrayList<Unidad_Medida>()

    init {
        interactor = CultivoInteractor()
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
            var extras = intent.extras
            view?.onEventBroadcastReceiver(extras, intent);
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
        //showSnack(isConnected);
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiver, IntentFilter("CONECTIVIDAD"))
        context.registerReceiver(mNotificationReceiver, IntentFilter("LOCATION"))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }
    //endregion

    @Subscribe
    override fun onEventMainThread(cultivoEvent: CultivoEvent?) {
        when (cultivoEvent?.eventType) {
            CultivoEvent.LIST_EVENT_UNIDAD_PRODUCTIVA -> {
                val list_unidad_productiva = cultivoEvent.mutableList as List<UnidadProductiva>
                listUnidadProductivaGlobal=list_unidad_productiva
            }

            CultivoEvent.LIST_EVENT_TIPO_PRODUCTO -> {
                val list_tipo_producto = cultivoEvent.mutableList as List<TipoProducto>
                listTipoProductosGlobal=list_tipo_producto
            }

            CultivoEvent.LIST_EVENT_DETALLE_TIPO_PRODUCTO -> {
                val list_detalle_tipo_producto = cultivoEvent.mutableList as List<DetalleTipoProducto>
                listDetalleTipoProductosGlobal=list_detalle_tipo_producto
            }
            CultivoEvent.LIST_EVENT_UNIDAD_MEDIDA -> {
                val list_unidad_medida = cultivoEvent.mutableList as List<Unidad_Medida>
                listUnidadMedidasGlobal=list_unidad_medida
            }
            CultivoEvent.LIST_EVENT_LOTES -> {
                val list_lotes = cultivoEvent.mutableList as List<Lote>
                listLoteGlobal= list_lotes
            }

            CultivoEvent.SAVE_EVENT -> {
                val list_cultivos = cultivoEvent.mutableList as List<Cultivo>
                view?.setListCultivos(list_cultivos)
                view?.requestResponseOk()
            }
            CultivoEvent.ERROR_EVENT -> {
                view?.requestResponseError(cultivoEvent.mensajeError)
            }
            CultivoEvent.ERROR_DIALOG_EVENT -> {
                view?.messageErrorDialog(cultivoEvent.mensajeError)
            }
            CultivoEvent.READ_EVENT -> {
                val list_cultivos = cultivoEvent.mutableList as List<Cultivo>
                view?.setListCultivos(list_cultivos)
            }
            CultivoEvent.UPDATE_EVENT -> {
                val list_cultivos = cultivoEvent.mutableList as List<Cultivo>
                view?.setListCultivos(list_cultivos)
                view?.requestResponseOk()
            }
            CultivoEvent.DELETE_EVENT -> {
                val list_cultivos = cultivoEvent.mutableList as List<Cultivo>
                view?.setListCultivos(list_cultivos)
                view?.requestResponseOk()
            }

            //EVENTS ON ITEM CLICK
            CultivoEvent.ITEM_EVENT -> {
                val cultivo = cultivoEvent.objectMutable as Cultivo
                view?.onMessageOk(R.color.colorPrimary, "Item: " + cultivo.Nombre)
            }

            CultivoEvent.ITEM_EDIT_EVENT -> {
                val cultivo = cultivoEvent.objectMutable as Cultivo
                view?.showAlertDialogCultivo(cultivo)
            }

            CultivoEvent.ITEM_DELETE_EVENT -> {
                val cultivo = cultivoEvent.objectMutable as Cultivo
                view?.deleteCultivo(cultivo)
            }
        }
    }

    //Search
    override fun validarListasFilterLote(): Boolean {
        if (view?.validarListasFilterLote() == true) {
            return true
        } else {
            return false
        }
    }

    override fun validarCampos(): Boolean {
        if (view?.validarCampos() == true) {
            return true
        } else {
            return false
        }
    }

    override fun registerCultivo(cultivo: Cultivo?) {
        interactor?.registerCultivo(cultivo)
    }

    override fun updateCultivo(cultivo: Cultivo?) {
        interactor?.updateCultivo(cultivo)
    }

    override fun deleteCultivo(cultivo: Cultivo?) {
        interactor?.deleteCultivo(cultivo)
    }


    override fun getListas() {
        interactor?.getListas()
    }

    override fun getListCultivos(lote_id: Long?) {
        interactor?.execute(null)
    }

    override fun setListSpinnerUnidadProductiva() {
        view?.setListUnidadProductiva(listUnidadProductivaGlobal)
    }

    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
        var list= listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id==unidad_productiva_id }
        view?.setListLotes(list)
    }

    override fun setListSpinnerTipoProducto() {
        view?.setListTipoProducto(listTipoProductosGlobal)
    }

    override fun setListSpinnerDetalleTipoProducto(detalle_tipo_produto_id: Long?) {
        var list= listDetalleTipoProductosGlobal?.filter { detalleTipoProducto: DetalleTipoProducto -> detalleTipoProducto.TipoProductoId==detalle_tipo_produto_id }
        view?.setListDetalleTipoProducto(list)
    }

    override fun setListSpinnerUnidadMedida() {
        view?.setListUnidadMedidas(listUnidadMedidasGlobal)
    }

    //endregion

    //region Acciones de Respuesta a Post de Eventos

    //endregion

    //region Messages/Notificaciones


    //endregion

}