package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Produccion

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.services.resources.Const_Resources
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
    var listUnidadProductivaGlobal:List<Unidad_Productiva>?= ArrayList<Unidad_Productiva>()
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
        context.registerReceiver(mNotificationReceiver, IntentFilter(Const_Resources.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiver);
    }


    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventProduccion?) {
        when (event?.eventType) {


            RequestEventProduccion.READ_EVENT -> {
                val list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
            }
            RequestEventProduccion.SAVE_EVENT -> {
                val list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
                onSaveOk()
            }
            RequestEventProduccion.UPDATE_EVENT -> {
                val list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
                onUpdateOk()
            }
            RequestEventProduccion.DELETE_EVENT -> {
                val list = event.mutableList as List<Produccion>
                mainView?.setListProduccion(list)
                onDeleteOk()
            }
            RequestEventProduccion.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

           //EVENTS ONITEM CLICK
            RequestEventProduccion.ITEM_EVENT -> {
                val proiduccion = event.objectMutable as Produccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Item: "+lote.Nombre)
            }
            RequestEventProduccion.ITEM_READ_EVENT -> {
                val producccion = event.objectMutable as Produccion
                //loteMainView?.onMessageOk(R.color.colorPrimary,"Leer: "+lote.Nombre)
                ///  Toast.makeText(activity,"Leer: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }
            RequestEventProduccion.ITEM_EDIT_EVENT -> {
                val producccion = event.objectMutable as Produccion
                mainView?.showAlertDialogAddProduccion(producccion)
            }
            RequestEventProduccion.ITEM_DELETE_EVENT -> {
                val producccion = event.objectMutable as Produccion
                mainView?.confirmDelete(producccion)
                //// Toast.makeText(activity,"Eliminar: "+lote.Nombre,Toast.LENGTH_LONG).show()
            }


           //LIST EVENTS
            RequestEventProduccion.LIST_EVENT_UNIDAD_MEDIDA -> {
                listUnidadMedidaGlobal= event.mutableList as List<Unidad_Medida>
            }

            RequestEventProduccion.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal= event.mutableList as List<Unidad_Productiva>
            }

            RequestEventProduccion.LIST_EVENT_LOTE -> {
                listLoteGlobal= event.mutableList as List<Lote>
            }

            RequestEventProduccion.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal= event.mutableList as List<Cultivo>
            }

            //Get Single
            RequestEventProduccion.GET_EVENT_CULTIVO -> {
                val cultivo = event.objectMutable as Cultivo
                mainView?.setCultivo(cultivo)
            }

           //Error Conection
            RequestEventProduccion.ERROR_VERIFICATE_CONECTION -> {
                onMessageConectionError()
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
        interactor?.saveProduccion(producccion, cultivo_Id,checkConnection())
    }

    override fun updateProducccion(produccion: Produccion, cultivo_id: Long) {
        mainView?.showProgress()
        mainView?.disableInputs()
        interactor?.updateProduccion(produccion,cultivo_id,checkConnection())

    }

    override fun deleteProduccion(producccion: Produccion, cultivo_id: Long?) {
        mainView?.showProgress()
        interactor?.deleteProducccion(producccion, cultivo_id,checkConnection())
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
        val list= listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id==unidad_productiva_id }
        mainView?.setListLotes(list)
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        val list= listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId==lote_id }
        mainView?.setListCultivos(list)
    }

    override fun setListSpinnerUnidadProductiva() {
        mainView?.setListUnidadProductiva(listUnidadProductivaGlobal)
    }

    override fun setListSpinnerUnidadMedida() {
        mainView?.setListUnidadMedida(listUnidadMedidaGlobal)
    }

    //endregion


    //endregion
    //
    // region Acciones de Respuesta a Post de Eventos
    private fun onSaveOk() {
        onMessageOk()
    }

    private fun onUpdateOk() {
        onMessageOk()
    }

    private fun onDeleteOk() {
        mainView?.hideProgress()
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
}