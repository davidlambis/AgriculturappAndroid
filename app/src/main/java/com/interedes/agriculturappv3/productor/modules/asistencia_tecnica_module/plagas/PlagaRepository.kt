package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.plagas

import com.interedes.agriculturappv3.productor.models.plagas.Enfermedad
import com.interedes.agriculturappv3.productor.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.plagas.Enfermedad_Table
import com.interedes.agriculturappv3.productor.models.tipoproducto.TipoProducto
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
        var enfermedadesByProducto= SQLite.select().from(Enfermedad::class.java).where(Enfermedad_Table.TipoProductoId.eq(tipoProductoId)).queryList()
        postEventOk(PlagasEvent.READ_EVENT, enfermedadesByProducto, null)
    }


    override fun setPlaga(enfermedadId: Long?) {
        val lista_all_tipo_enfermedades = getAllListEnfermedad()
        val lista = ArrayList<Enfermedad>()
        for (item in lista_all_tipo_enfermedades) {
            if (item.Id == enfermedadId) {
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

    private fun getAllLisEnfermedad(): List<Enfermedad> {
        return SQLite.select().from(Enfermedad::class.java).queryList()
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

    private fun postEventOk(type: Int, listPlagas: List<Enfermedad>?, plaga: Enfermedad?) {
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