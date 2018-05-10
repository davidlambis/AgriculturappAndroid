package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento

import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento.events.TratamientoEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite


class TratamientoRepository : ITratamiento.Repository {

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    override fun registerControlPlaga(controlPlaga: ControlPlaga, cultivo_id: Long?) {
        controlPlaga.save()
        val list_control_plagas = getControlPlagasByCultivo(cultivo_id)
        postEventControlPlaga(TratamientoEvent.SAVE_CONTROL_PLAGA_EVENT, list_control_plagas)
    }

    override fun getTratamiento(insumoId: Long?) {
        /* val lista_all_tratamientos = Listas.listaTratamientos()
         var tratamiento: Tratamiento? = null
         for (item in lista_all_tratamientos) {
             if (item.InsumoId == insumoId) {
                 tratamiento = item
             }
         }
         postEventOk(TratamientoEvent.SET_EVENT, tratamiento)*/
        for (item in Listas.listaTratamientos()) {
            item.save()
        }

        val lista_all_tratamientos = SQLite.select().from(Tratamiento::class.java).queryList()
        var tratamiento: Tratamiento? = null
        for (item in lista_all_tratamientos) {
            if (item.InsumoId == insumoId) {
                tratamiento = item
            }
        }
        postEventOk(TratamientoEvent.SET_EVENT, tratamiento)
    }

    override fun getListas() {
        val listUnidadProductiva = SQLite.select().from(Unidad_Productiva::class.java).queryList()
        val listLotes = SQLite.select().from(Lote::class.java).queryList()
        val listCultivos = SQLite.select().from(Cultivo::class.java).queryList()
        val listUnidadMedida = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.CategoriaMedidaId.eq(4)).queryList()
        postEventListUnidadProductiva(TratamientoEvent.LIST_EVENT_UP, listUnidadProductiva, null)
        postEventListLotes(TratamientoEvent.LIST_EVENT_LOTE, listLotes, null)
        postEventListCultivos(TratamientoEvent.LIST_EVENT_CULTIVO, listCultivos, null);
        postEventListUnidadMedida(TratamientoEvent.LIST_EVENT_UNIDAD_MEDIDA, listUnidadMedida, null);
    }
    //endregion

    //region Methods
    private fun getControlPlagasByCultivo(cultivo_id: Long?): List<ControlPlaga>? {
        return SQLite.select().from(ControlPlaga::class.java).where(ControlPlaga_Table.CultivoId.eq(cultivo_id)).queryList()
    }
    //endregion

    //region Events
    //Main Post Event
    private fun postEventControlPlaga(type: Int, listControlPlagas: List<ControlPlaga>?) {
        val listControlPlagasMutable = listControlPlagas as MutableList<Object>
        postEvent(type, listControlPlagasMutable, null, null)
    }

    private fun postEventOk(type: Int, tratamiento: Tratamiento?) {
        //val listTratamientostMutable = listTratamientos as MutableList<Object>
        var tratamientoMutable: Object? = null
        if (tratamiento != null) {
            tratamientoMutable = tratamiento as Object
        }
        postEvent(type, null, tratamientoMutable, null)
    }

    private fun postEventListUnidadProductiva(type: Int, listUnidadProductiva: List<Unidad_Productiva>?, messageError: String?) {
        val upMutable = listUnidadProductiva as MutableList<Object>
        postEvent(type, upMutable, null, messageError)
    }

    private fun postEventListLotes(type: Int, listLotes: List<Lote>?, messageError: String?) {
        val loteMutable = listLotes as MutableList<Object>
        postEvent(type, loteMutable, null, messageError)
    }

    private fun postEventListCultivos(type: Int, listCultivos: List<Cultivo>?, messageError: String?) {
        val cultivoMutable = listCultivos as MutableList<Object>
        postEvent(type, cultivoMutable, null, messageError)
    }

    private fun postEventListUnidadMedida(type: Int, listUnidadMedida: List<Unidad_Medida>?, messageError: String?) {
        val unidadMedidaMutable = listUnidadMedida as MutableList<Object>
        postEvent(type, unidadMedidaMutable, null, messageError)
    }


    private fun postEvent(type: Int, listModel: MutableList<Object>?, model: Object?, errorMessage: String?) {
        val event = TratamientoEvent(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}