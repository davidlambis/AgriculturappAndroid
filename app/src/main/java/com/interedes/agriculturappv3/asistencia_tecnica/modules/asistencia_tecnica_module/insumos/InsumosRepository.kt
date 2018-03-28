package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos

import com.interedes.agriculturappv3.asistencia_tecnica.models.insumos.Insumo
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.Enfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.events.InsumosEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite

class InsumosRepository : InterfaceInsumos.Repository {

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    //region Métodos Interfaz
    override fun getInsumosByPlaga(tipoEnfermedadId: Long?) {
        /*val lista_all_enfermedades = Listas.listaEnfermedad()
        val lista_enfermedad = ArrayList<Enfermedad>()
        val lista_all_insumos = Listas.listaInsumos()
        val lista_insumos = ArrayList<Insumo>()
        for (item in lista_all_enfermedades) {
            if (item.TipoEnfermedadId == tipoEnfermedadId) {
                lista_enfermedad.add(item)
            }
        }
        for (item in lista_all_insumos) {
            for (i in lista_enfermedad) {
                if (item.EnfermedadId == i.Id) {
                    lista_insumos.add(item)
                }
            }
        }
        postEventOk(InsumosEvent.READ_EVENT, lista_insumos, null) */
        for (item in Listas.listaInsumos()) {
            item.save()
        }

        val lista_all_enfermedades = SQLite.select().from(Enfermedad::class.java).queryList()
        val lista_enfermedad = ArrayList<Enfermedad>()
        val lista_all_insumos = SQLite.select().from(Insumo::class.java).queryList()
        val lista_insumos = ArrayList<Insumo>()
        for (item in lista_all_enfermedades) {
            if (item.TipoEnfermedadId == tipoEnfermedadId) {
                lista_enfermedad.add(item)
            }
        }
        for (a in lista_all_insumos) {
            for (b in lista_enfermedad) {
                if (a.EnfermedadId == b.Id) {
                    lista_insumos.add(a)
                }
            }
        }

        postEventOk(InsumosEvent.READ_EVENT, lista_insumos, null)
    }

    /*
    override fun setInsumo(insumoId: Long?) {
        val lista_all_insumos = Listas.listaInsumos()
        val lista_insumos = ArrayList<Insumo>()
        for (item in lista_all_insumos) {
            if (item.Id == insumoId) {
                lista_insumos.add(item)
            }
        }
        postEventOk(InsumosEvent.SET_EVENT, lista_insumos, null)
    }*/
    //endregion

    //region Events
    //Main Post Event
    private fun postEventOk(type: Int, listInsumos: List<Insumo>?, insumo: Insumo?) {
        val insumoListMutable = listInsumos as MutableList<Object>
        var insumoMutable: Object? = null
        if (insumo != null) {
            insumoMutable = insumo as Object
        }
        postEvent(type, insumoListMutable, insumoMutable, null)
    }

    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = InsumosEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}