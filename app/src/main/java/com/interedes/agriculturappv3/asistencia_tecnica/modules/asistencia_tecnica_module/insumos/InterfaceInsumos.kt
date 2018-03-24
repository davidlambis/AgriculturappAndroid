package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos

import com.interedes.agriculturappv3.asistencia_tecnica.models.Insumo
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.events.InsumosEvent


interface InterfaceInsumos {

    interface View {
        fun showRefresh()
        fun hideRefresh()
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
        fun setInsumosList(listInsumos: List<Insumo>)
        fun setResults(insumos: Int)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
        fun onEventMainThread(insumosEvent: InsumosEvent?)
    }

    interface Interactor {
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
    }

    interface Repository {
        fun getInsumosByPlaga(tipoEnfermedadId: Long?)
    }
}