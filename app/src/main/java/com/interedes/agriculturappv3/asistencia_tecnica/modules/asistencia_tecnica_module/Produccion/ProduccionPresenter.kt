package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

/**
 * Created by usuario on 21/03/2018.
 */


class ProduccionPresenter(var mainView: IMainProduccion.MainView?) : IMainProduccion.Presenter {

    var interactor: IMainProduccion.Interactor? = null
    var eventBus: EventBus? = null


    companion object {
        var instance: ProduccionPresenter? = null
    }

    init {
        instance = this
        interactor = ProduccionInteractor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)
        getListas()
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


    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventProduccion?) {
        when (event?.eventType) {
            RequestEventProduccion.READ_EVENT -> {
                var produccionList = event.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
            }
            RequestEventProduccion.SAVE_EVENT -> {
                var produccionList = event.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
                onSaveOk()
            }
            RequestEventProduccion.UPDATE_EVENT -> {
                var produccionList = event.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
                onUpdateOk()
            }
            RequestEventProduccion.DELETE_EVENT -> {
                var produccionList = event.mutableList as List<Produccion>
                //loteMainView?.setListLotes(loteList)
                onDeleteOk()
            }
            RequestEventProduccion.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

           //EVENTS ONITEM CLICK
            RequestEventProduccion.ITEM_EVENT -> {
                var proiduccion = event.objectMutable as Produccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Item: "+lote.Nombre)
            }
            RequestEventProduccion.ITEM_READ_EVENT -> {
                var lote = event.objectMutable as Produccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Leer: "+lote.Nombre)
                ///  Toast.makeText(activity,"Leer: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
            RequestEventProduccion.ITEM_EDIT_EVENT -> {
                var lote = event.objectMutable as Produccion
                //Lote_Fragment.instance?.loteGlobal=lote
                //loteMainView?.showAlertDialogAddLote(Lote_Fragment.instance?.loteGlobal)
            }
            RequestEventProduccion.ITEM_DELETE_EVENT -> {
                var lote = event.objectMutable as Produccion
                //loteMainView?.confirmDelete(lote)
                //// Toast.makeText(activity,"Eliminar: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
           //LIST EVENTS

            RequestEventProduccion.LIST_EVENT_UNIDAD_MEDIDA -> {
                var list= event.mutableList as List<Unidad_Medida>
                mainView?.setListUnidadMedida(list)
            }

            RequestEventProduccion.LIST_EVENT_UP -> {
                var list= event.mutableList as List<UnidadProductiva>
                mainView?.setListUnidadProductiva(list)
            }

            RequestEventProduccion.LIST_EVENT_LOTE -> {
                var list= event.mutableList as List<Lote>
                mainView?.setListLotes(list)
            }

            RequestEventProduccion.LIST_EVENT_CULTIVO -> {
                var list= event.mutableList as List<Cultivo>
                mainView?.setListCultivos(list)
            }
        }
    }
    //endregion

    //region Methods
    override fun validarCampos(): Boolean? {
        if (mainView?.validarCampos() == true) {
            return true
        }
        return false
    }

    override fun registerProdcuccion(producccion: Produccion, cultivo_Id: Long) {
        mainView?.showProgress()
        //if(checkConnection()){
        mainView?.disableInputs()
        interactor?.registerProduccion(producccion, cultivo_Id)
    }

    override fun updateProducccion(produccion: Produccion, cultivo_id: Long) {
        mainView?.showProgress()
        if (checkConnection()) {
            mainView?.disableInputs()
            interactor?.registerProduccion(produccion, cultivo_id)
        } else {
            onMessageConectionError()
        }
    }


    override fun deleteProduccion(producccion: Produccion, cultivo_id: Long) {
        mainView?.showProgress()
        if (checkConnection()) {
            interactor?.deleteProducccion(producccion, cultivo_id)
        } else {
            onMessageConectionError()
        }
    }

    override fun getListProduccion(cultivo_id:Long?) {
        interactor?.execute(cultivo_id)
    }

    override fun getListas() {
        interactor?.getListas()
    }

    //endregion

    //region METHODS VIEWS
    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
       mainView?.setListSpinnerLote(unidad_productiva_id)
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        mainView?.setListSpinnerCultivo(lote_id)
    }


    //endregion

    //region Acciones de Respuesta a Post de Eventos
    private fun onSaveOk() {
        onMessageOk()
    }

    private fun onUpdateOk() {
        onMessageOk()
    }

    private fun onDeleteOk() {
        mainView?.requestResponseOK()
    }
    //endregion

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.enableInputs()
        mainView?.hideProgress()
        mainView?.limpiarCampos()
        mainView?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        mainView?.enableInputs()
        mainView?.hideProgress()
        mainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        mainView?.hideProgress()
        mainView?.verificateConnection()
    }
    //endregion
}