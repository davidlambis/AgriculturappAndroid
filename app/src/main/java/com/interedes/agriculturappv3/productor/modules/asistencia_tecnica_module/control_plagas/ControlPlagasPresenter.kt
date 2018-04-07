package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.Lote
import com.interedes.agriculturappv3.productor.models.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.events.ControlPlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList


class ControlPlagasPresenter(var view: IControlPlagas.View?) : IControlPlagas.Presenter {

    var eventBus: EventBus? = null
    var interactor: IControlPlagas.Interactor? = null

    //GLOBALS
    var listUnidadProductivaGlobal: List<UnidadProductiva>? = ArrayList<UnidadProductiva>()
    var listLoteGlobal: List<Lote>? = ArrayList<Lote>()
    var listCultivosGlobal: List<Cultivo>? = ArrayList<Cultivo>()

    companion object {
        var instance: ControlPlagasPresenter? = null
    }

    init {
        instance = this
        eventBus = GreenRobotEventBus()
        interactor = ControlPlagasInteractor()
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
        context.unregisterReceiver(this.mNotificationReceiver);
    }

    //endregion

    override fun getListas() {
        interactor?.getListas()
    }

    override fun getListControlPlaga(cultivo_id: Long?) {
        interactor?.getListControlPlaga(cultivo_id)
    }

    override fun validarListasFilter(): Boolean {
        if (view?.validarListasFilter() == true) {
            return true
        }
        return false
    }

    override fun getCultivo(cultivo_id: Long?) {
        interactor?.getCultivo(cultivo_id)
    }

    override fun deleteControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?) {
        view?.showProgress()
        if (checkConnection()) {
            interactor?.deleteControlPlaga(controlPlaga, cultivo_id)
        } else {
            onMessageConectionError()
        }
    }

    override fun updateControlPlaga(controlPlaga: ControlPlaga?) {
        interactor?.updateControlPlaga(controlPlaga)
    }

    @Subscribe
    override fun onEventMainThread(event: ControlPlagasEvent?) {
        when (event?.eventType) {
            ControlPlagasEvent.READ_EVENT -> {
                val list = event.mutableList as List<ControlPlaga>
                view?.setListControlPlagas(list)
            }
        //LIST EVENTS
            ControlPlagasEvent.LIST_EVENT_UP -> {
                listUnidadProductivaGlobal = event.mutableList as List<UnidadProductiva>
            }

            ControlPlagasEvent.LIST_EVENT_LOTE -> {
                listLoteGlobal = event.mutableList as List<Lote>
            }

            ControlPlagasEvent.LIST_EVENT_CULTIVO -> {
                listCultivosGlobal = event.mutableList as List<Cultivo>
            }

            ControlPlagasEvent.GET_EVENT_CULTIVO -> {
                val cultivo = event.objectMutable as Cultivo
                view?.setCultivo(cultivo)
            }

        //ITEMS LISTA
            ControlPlagasEvent.ITEM_DELETE_EVENT -> {
                val controlPlaga = event.objectMutable as ControlPlaga
                view?.confirmDelete(controlPlaga)
            }

            ControlPlagasEvent.DELETE_EVENT -> {
                val list = event.mutableList as List<ControlPlaga>
                view?.setListControlPlagas(list)
                //onDeleteOk()
            }

            ControlPlagasEvent.ITEM_ERRADICAR_EVENT -> {
                val controlPlaga = event.objectMutable as ControlPlaga
                view?.updatePlaga(controlPlaga)
            }
        }
    }

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

    //endregion

    private fun onDeleteOk() {
        view?.requestResponseOK()
    }

    private fun onMessageConectionError() {
        view?.hideProgress()
        view?.verificateConnection()
    }


}