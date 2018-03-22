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

class CultivoPresenter(var view: ICultivo.View?) : ICultivo.Presenter {

    var interactor: ICultivo.Interactor? = null
    var eventBus: EventBus? = null

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
                view?.setListUnidadProductiva(list_unidad_productiva)
            }
            CultivoEvent.LIST_EVENT_TIPO_PRODUCTO -> {
                val list_tipo_producto = cultivoEvent.mutableList as List<TipoProducto>
                view?.setListTipoProducto(list_tipo_producto)
            }
            CultivoEvent.LIST_EVENT_DETALLE_TIPO_PRODUCTO -> {
                val list_detalle_tipo_producto = cultivoEvent.mutableList as List<DetalleTipoProducto>
                view?.setListDetalleTipoProducto(list_detalle_tipo_producto)
            }
            CultivoEvent.LIST_EVENT_UNIDAD_MEDIDA -> {
                val list_unidad_medida = cultivoEvent.mutableList as List<Unidad_Medida>
                view?.setListUnidadMedidas(list_unidad_medida)
            }
            CultivoEvent.LIST_EVENT_LOTES -> {
                view?.enableInputs()
                val list_lotes = cultivoEvent.mutableList as List<Lote>
                view?.setListLotes(list_lotes)
            }
            CultivoEvent.LIST_EVENT_LOTES_SEARCH -> {
                val list_lotes = cultivoEvent.mutableList as List<Lote>
                view?.setListLotesSearch(list_lotes)
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
                view?.requestResponseDialogError(cultivoEvent.mensajeError)
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
            CultivoEvent.SEARCH_EVENT -> {
                val list_cultivos = cultivoEvent.mutableList as List<Cultivo>
                view?.setListCultivos(list_cultivos)
            }

        //EVENTS ON ITEM CLICK
            CultivoEvent.ITEM_EVENT -> {
                val cultivo = cultivoEvent.objectMutable as Cultivo
                view?.onMessageOk(R.color.colorPrimary, "Item: " + cultivo.Nombre)
            }

            CultivoEvent.ITEM_EDIT_EVENT -> {
                val cultivo = cultivoEvent.objectMutable as Cultivo
                Cultivo_Fragment.instance?.cultivoGlobal = cultivo
                view?.showAlertDialogCultivo(Cultivo_Fragment.instance?.cultivoGlobal)
            }

            CultivoEvent.ITEM_DELETE_EVENT -> {
                val cultivo = cultivoEvent.objectMutable as Cultivo
                view?.deleteCultivo(cultivo)
            }

        }
    }

    //Search
    override fun validarCamposSearch(): Boolean {
        if (view?.validarCamposSearch() == true) {
            return true
        } else {
            return false
        }
    }

    override fun searchCultivos(loteId: Long?) {
        interactor?.searchCultivos(loteId)
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

    override fun getAllCultivos() {
        interactor?.getAllCultivos()
    }

    override fun getListas() {
        interactor?.getListas()
    }

    override fun loadLotesSpinner(unidadProductivaId: Long?) {
        view?.disableInputs()
        view?.hideLotes()
        interactor?.loadLotesSpinner(unidadProductivaId)
    }

    override fun loadLotesSpinnerSearch(unidadProductivaId: Long?) {
        interactor?.loadLotesSpinnerSearch(unidadProductivaId)
    }

    override fun loadDetalleTipoProducto(tipoProductoId: Long?) {
        interactor?.loadDetalleTipoProducto(tipoProductoId)
    }

    //endregion

    //region Acciones de Respuesta a Post de Eventos

    //endregion

    //region Messages/Notificaciones


    //endregion

}