package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas

import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import org.greenrobot.eventbus.Subscribe


class PlagaPresenter(var view: IPlaga.View?) : IPlaga.Presenter {
    var interactor: IPlaga.Interactor? = null
    var eventBus: EventBus? = null

    init {
        interactor = PlagaInteractor()
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun onCreate() {
        eventBus?.register(this)
    }

    override fun onDestroy() {
        eventBus?.unregister(this)
    }

    @Subscribe
    override fun onEventMainThread(plagasEvent: PlagasEvent?) {
        when(plagasEvent?.eventType){
            PlagasEvent.READ_EVENT -> {
                val list_plagas = plagasEvent.mutableList as List<TipoEnfermedad>
                view?.setListPlagas(list_plagas)
            }
        }
    }

    override fun getPlagasByTipoProducto(tipoProductoId: Long?) {
        interactor?.getPlagasByTipoProducto(tipoProductoId)
    }
    //endregion
}