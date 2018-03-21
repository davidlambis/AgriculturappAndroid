package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.UnidadProductiva

import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.UnidadProductiva.events.RequestEventUP
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

class UpRepository() :IUnidadProductiva.Repo{
    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    override fun getListUPs() {
        postEventOk(RequestEventUP.READ_EVENT,getUPs(),null)
    }

    override fun saveUp(mUnidadProductiva: UnidadProductiva) {
        mUnidadProductiva.save()
        postEventOk(RequestEventUP.SAVE_EVENT,getUPs(), mUnidadProductiva)
    }

    override fun updateUp(mUnidadProductiva: UnidadProductiva) {
        mUnidadProductiva.update()
        postEventOk(RequestEventUP.UPDATE_EVENT,getUPs(), mUnidadProductiva)
    }

    override fun deleteUp(mUnidadProductiva: UnidadProductiva) {
        mUnidadProductiva.delete()
        postEventOk(RequestEventUP.DELETE_EVENT,getUPs(), mUnidadProductiva)
    }

    override fun getUPs(): List<UnidadProductiva> {
        return SQLite.select().from(UnidadProductiva::class.java!!).queryList()
    }

    override fun getListas() {
        //return SQLite.select().from(UnidadProductiva::class.java!!).queryList()
        var listUnidadMedida= Listas.listaUnidadMedida()
        postEventListUnidadMedida(RequestEventUP.LIST_EVENT_UNIDAD_MEDIDA,listUnidadMedida,null);
    }

    //region EVENTS
    private fun postEventListUnidadMedida(type: Int, listUnidadMedida:List<Unidad_Medida>?, messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }


    private fun postEventOk(type: Int, listUnidadProductivas: List<UnidadProductiva>?, unidadProductiva: UnidadProductiva?){
        var UpListMutable = listUnidadProductivas as MutableList<Object>
        var UpMutable:Object?=null
        if(unidadProductiva!=null){
            UpMutable = unidadProductiva as Object
        }
        postEvent(type,UpListMutable, UpMutable,null)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventUP(type, listModel, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }

    //endregion
}