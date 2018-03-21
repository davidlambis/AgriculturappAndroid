package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion

import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.events.RequestEventProduccion
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

/**
 * Created by usuario on 20/03/2018.
 */
class ProduccionRepository :IMainProduccion.Repository {

    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }

    //region METHODS
    override fun saveProduccion(produccion: Produccion,cultivo_id:Long) {
        produccion.save()
        var listProduccion = getProductions(cultivo_id)
        postEventOk(RequestEventProduccion.READ_EVENT,listProduccion,null);
    }

    override fun getListProduccion(cultivo_id:Long?) {
        var listaProduccion = getProductions(cultivo_id)
        postEventOk(RequestEventProduccion.READ_EVENT,listaProduccion,null);
    }

    override fun getProductions(cultivo_id:Long?):List<Produccion> {
        var listResponse:List<Produccion>?=null
        if(cultivo_id==null){
            listResponse = SQLite.select().from(Produccion::class.java!!).queryList()
        }else{
            listResponse = SQLite.select().from(Produccion::class.java!!).where(Produccion_Table.CultivoId.eq(cultivo_id)).queryList()
        }
        return listResponse;
    }

    override fun updateProduccion(produccion: Produccion,unidad_productiva_id:Long) {
        produccion.update()
        postEventOk(RequestEventProduccion.UPDATE_EVENT, getProductions(unidad_productiva_id),produccion);
    }

    override fun deleteProduccion(produccion: Produccion,cultivo_id: Long) {
        produccion.delete()
        //SQLite.delete<Lote>(Lote::class.java).where(Lote_Table.Id.eq(lote.Id)).async().execute()
        postEventOk(RequestEventProduccion.DELETE_EVENT, getProductions(cultivo_id),produccion);
    }
    //endregion

    //region Events
    private fun postEventOk(type: Int, producciones: List<Produccion>?, produccion:Produccion?) {
        var produccionListMitable= producciones as MutableList<Object>
        var ProducciconMutable:Object?=null
        if(produccion!=null){
            ProducciconMutable = produccion as Object
        }
        postEvent(type, produccionListMitable,ProducciconMutable,null)
    }

    private fun postEventError(type: Int,messageError:String) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventProduccion(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}