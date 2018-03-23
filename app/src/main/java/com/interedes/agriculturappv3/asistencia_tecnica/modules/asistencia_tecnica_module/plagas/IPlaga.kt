package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas

import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events.PlagasEvent

interface IPlaga {

    interface View {
        fun showRefresh()
        fun hideRefresh()
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun setListPlagas(list_plagas: List<TipoEnfermedad>)
        fun setResults(plagas: Int)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
        fun onEventMainThread(plagasEvent: PlagasEvent?)
    }

    interface Interactor {
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
    }

    interface Repository {
        fun getPlagasByTipoProducto(tipoProductoId: Long?)
    }
}