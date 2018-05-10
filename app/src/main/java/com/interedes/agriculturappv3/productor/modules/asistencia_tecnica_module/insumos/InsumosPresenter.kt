package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.insumos

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.productor.models.insumos.Insumo
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.insumos.events.InsumosEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe


class InsumosPresenter(var view: InterfaceInsumos.View?) : InterfaceInsumos.Presenter {

    var interactor: InterfaceInsumos.Interactor? = null
    var eventBus: EventBus? = null

    init {
        interactor = InsumosInteractor()
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        view = null
        eventBus?.unregister(this)
    }

    override fun getInsumosByPlaga(tipoEnfermedadId: Long?) {
        interactor?.getInsumosByPlaga(tipoEnfermedadId)
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

    /*
    override fun setInsumo(insumoId: Long?) {
        interactor?.setInsumo(insumoId)
    }*/

    @Subscribe
    override fun onEventMainThread(insumosEvent: InsumosEvent?) {
        when (insumosEvent?.eventType) {
            InsumosEvent.READ_EVENT -> {
                val list_insumos = insumosEvent.mutableList as List<Tratamiento>
                view?.setTratamientosList(list_insumos)
            }
            InsumosEvent.ITEM_EVENT -> {
                val tratamiento = insumosEvent.objectMutable as Tratamiento
                view?.verTratamiento(tratamiento)
            }
        /*
        InsumosEvent.SET_EVENT -> {
            val list_insumos = insumosEvent.mutableList as List<Insumo>
            view?.setInsumosList(list_insumos)
        }*/
        }
    }
    //endregion

}