package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos

import com.interedes.agriculturappv3.asistencia_tecnica.models.Insumo
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.events.InsumosEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
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
        eventBus?.unregister(this)
    }

    override fun getInsumosByPlaga(tipoEnfermedadId: Long?) {
        interactor?.getInsumosByPlaga(tipoEnfermedadId)
    }

    @Subscribe
    override fun onEventMainThread(insumosEvent: InsumosEvent?) {
        when (insumosEvent?.eventType) {
            InsumosEvent.READ_EVENT -> {
                val list_insumos = insumosEvent.mutableList as List<Insumo>
                view?.setInsumosList(list_insumos)
            }
        }
    }
    //endregion

}