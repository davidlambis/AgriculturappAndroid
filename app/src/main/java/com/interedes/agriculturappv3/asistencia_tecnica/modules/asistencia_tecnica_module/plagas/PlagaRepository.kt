package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas

import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto
import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto_Table
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.Enfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite


class PlagaRepository : IPlaga.Repository {

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    //region Interfaz
    override fun getPlagasByTipoProducto(tipoProductoId: Long?) {
        /*val lista_tipo_enfermedad = Listas.listaTipoEnfermedad()
        val lista_all_enfermedad = Listas.listaEnfermedad()
        var enfermedad: Enfermedad? = null
        val lista = ArrayList<TipoEnfermedad>()


        for (item in lista_tipo_enfermedad) {
            if (item.TipoProductoId == tipoProductoId) {
                lista.add(item)
            }
        }
        for (e in lista_all_enfermedad) {
            for (l in lista) {
                if (l.Id == e.TipoEnfermedadId) {
                    enfermedad = e
                }
            }
        }
        postEventOk(PlagasEvent.READ_EVENT, lista, null)
        postEventEnfermedad(PlagasEvent.SET_ENFERMEDAD_EVENT, enfermedad)*/

        //Trae el listado de todos los tipos de enfermedad y enfermedades y guarda en sqlite
        for (item in Listas.listaTipoEnfermedad()) {
            item.save()
        }
        for (item in Listas.listaEnfermedad()) {
            item.save()
        }

        //Listas Tipo_Enfermedades
        val lista_all_tipo_enfermedades = getAllListTipoEnfermedad()
        val lista_tipo_enfermedad = ArrayList<TipoEnfermedad>()
        for (item in lista_all_tipo_enfermedades) {
            if (item.TipoProductoId == tipoProductoId) {
                lista_tipo_enfermedad.add(item)
            }
        }

        //Listas Enfermedades
        val lista_all_enfermedades = getAllListEnfermedad()
        var enfermedad: Enfermedad? = null
        for (a in lista_all_enfermedades) {
            for (b in lista_tipo_enfermedad) {
                if (b.Id == a.TipoEnfermedadId) {
                    enfermedad = a
                }
            }
        }


        postEventOk(PlagasEvent.READ_EVENT, lista_tipo_enfermedad, null)
        postEventEnfermedad(PlagasEvent.SET_ENFERMEDAD_EVENT, enfermedad)
    }


    override fun setPlaga(tipoEnfermedadId: Long?) {
        val lista_all_tipo_enfermedades = getAllListTipoEnfermedad()
        val lista = ArrayList<TipoEnfermedad>()
        for (item in lista_all_tipo_enfermedades) {
            if (item.Id == tipoEnfermedadId) {
                lista.add(item)
            }
        }
        postEventOk(PlagasEvent.SET_EVENT, lista, null)
    }
    //endregion

    //region Métodos
    override fun getTiposProducto() {
        val lista_tipo_producto = SQLite.select().from(TipoProducto::class.java).queryList()
        postEventListaTiposProducto(PlagasEvent.LOAD_LIST_TIPO_PRODUCTO, lista_tipo_producto)
    }

    private fun getAllListTipoEnfermedad(): List<TipoEnfermedad> {
        return SQLite.select().from(TipoEnfermedad::class.java).queryList()
    }

    private fun getAllListEnfermedad(): List<Enfermedad> {
        return SQLite.select().from(Enfermedad::class.java).queryList()
    }
    //endregion

    //region Events
    //Main Post Event
    private fun postEventEnfermedad(type: Int, enfermedad: Enfermedad?) {
        var enfermedadMutable: Object? = null
        if (enfermedad != null) {
            enfermedadMutable = enfermedad as Object
        }
        postEvent(type, null, enfermedadMutable, null)
    }

    private fun postEventListaTiposProducto(type: Int, listTipoProducto: List<TipoProducto>) {
        val listTipoProductotMutable = listTipoProducto as MutableList<Object>
        postEvent(type, listTipoProductotMutable, null, null)
    }
    /*
      private fun postEventOk(type: Int, tratamiento: Tratamiento?) {
        //val listTratamientostMutable = listTratamientos as MutableList<Object>
        var tratamientoMutable: Object? = null
        if (tratamiento != null) {
            tratamientoMutable = tratamiento as Object
        }
        postEvent(type, null, tratamientoMutable, null)
    }

     */

    private fun postEventOk(type: Int, listPlagas: List<TipoEnfermedad>?, plaga: TipoEnfermedad?) {
        val plagaListMutable = listPlagas as MutableList<Object>
        var plagaMutable: Object? = null
        if (plaga != null) {
            plagaMutable = plaga as Object
        }
        postEvent(type, plagaListMutable, plagaMutable, null)
    }

    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = PlagasEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}