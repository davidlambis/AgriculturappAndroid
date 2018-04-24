package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Produccion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.produccion.Produccion
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

/**
 * Created by usuario on 21/03/2018.
 */


class ProduccionPresenter(var mainView: IMainProduccion.MainView?) : IMainProduccion.Presenter {

    var interactor: IMainProduccion.Interactor? = null
    var eventBus: EventBus? = null

    //GLOBALS
    var listUnidadProductivaGlobal:List<UnidadProductiva>?= ArrayList<UnidadProductiva>()
    var listLoteGlobal:List<Lote>?= ArrayList<Lote>()
    var listCultivosGlobal:List<Cultivo>?= ArrayList<Cultivo>()
    var listUnidadMedidaGlobal:List<Unidad_Medida>?= ArrayList<Unidad_Medida>()



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
                var list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
            }
            RequestEventProduccion.SAVE_EVENT -> {
                var list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
                onSaveOk()
            }
            RequestEventProduccion.UPDATE_EVENT -> {
                var list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
                onUpdateOk()
            }
            RequestEventProduccion.DELETE_EVENT -> {
                var list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
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
                var producccion = event.objectMutable as Produccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Leer: "+lote.Nombre)
                ///  Toast.makeText(activity,"Leer: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
            RequestEventProduccion.ITEM_EDIT_EVENT -> {
                var producccion = event.objectMutable as Produccion
                mainView?.showAlertDialogAddProduccion(producccion)
            }
            RequestEventProduccion.ITEM_DELETE_EVENT -> {
                var producccion = event.objectMutable as Produccion
                mainView?.confirmDelete(producccion)
                //// Toast.makeText(activity,"Eliminar: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }

           //LIST EVENTS
            RequestEventProduccion.LIST_EVENT_UNIDAD_MEDIDA -> {
                listUnidadMedidaGlobal= event.mutableList as List<Unidad_Medida>
            }

            RequestEventProduccion.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal= event.mutableList as List<UnidadProductiva>
            }

            RequestEventProduccion.LIST_EVENT_LOTE -> {
                listLoteGlobal= event.mutableList as List<Lote>
            }

            RequestEventProduccion.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal= event.mutableList as List<Cultivo>
            }

            //Get Single
            RequestEventProduccion.GET_EVENT_CULTIVO -> {
                var cultivo = event.objectMutable as Cultivo
                mainView?.setCultivo(cultivo)
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

    override fun validarListasAddProduccion(): Boolean? {
        if (mainView?.validarListasAddProduccion() == true) {
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


    override fun deleteProduccion(producccion: Produccion, cultivo_id: Long?) {
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


    override fun getCultivo(cultivo_id:Long?){
        interactor?.getCultivo(cultivo_id)
    }

    //endregion

    //region METHODS VIEWS
    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
        var list= listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id==unidad_productiva_id }
        mainView?.setListLotes(list)
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        var list= listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId==lote_id }
        mainView?.setListCultivos(list)
    }

    override fun setListSpinnerUnidadProductiva() {
        mainView?.setListUnidadProductiva(listUnidadProductivaGlobal)
    }

    override fun setListSpinnerUnidadMedida() {
        mainView?.setListUnidadMedida(listUnidadMedidaGlobal)
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