package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote_Table
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.events.RequestEventLote
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.sql.language.SQLite


/**
 * Created by EnuarMunoz on 7/03/18.
 */
class LoteRepositoryImpl:LoteRepository {

    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()
    }

    //region METHODS
    override fun saveLotes(lote: Lote,unidad_productiva_id:Long?) {
        lote.save()
        val lotes = getLotes(unidad_productiva_id)
        postEventOk(RequestEventLote.SAVE_EVENT,lotes,lote);
    }

    override fun getListLotes(unidad_productiva_id:Long?) {
        val lotes = getLotes(unidad_productiva_id)
        postEventOk(RequestEventLote.READ_EVENT,lotes,null);
    }

    override fun loadListas() {
        var listUp=SQLite.select().from(UnidadProductiva::class.java!!).queryList()
        var listUnidadMedida= Listas.listaUnidadMedida()

        postEventListUnidadMedida(RequestEventLote.LIST_EVENT_UNIDAD_MEDIDA,listUnidadMedida,null);
        postEventListUp(RequestEventLote.LIST_EVENT_UP,listUp,null);
    }

    override fun getLotes(unidad_productiva_id:Long?):List<Lote> {
        var listResponse:List<Lote>?=null

        if(unidad_productiva_id==null){
            listResponse = SQLite.select().from(Lote::class.java!!).queryList()
        }else{
            listResponse = SQLite.select().from(Lote::class.java!!).where(Lote_Table.Unidad_Productiva_Id.eq(unidad_productiva_id)).queryList()
        }
        return listResponse;
    }

    override fun updateLote(lote: Lote,unidad_productiva_id:Long?) {
        lote.update()
        postEventOk(RequestEventLote.UPDATE_EVENT, getLotes(unidad_productiva_id),lote);
    }

    override fun deleteLote(lote: Lote,unidad_productiva_id:Long?) {
        lote.delete()
        //SQLite.delete<Lote>(Lote::class.java).where(Lote_Table.Id.eq(lote.Id)).async().execute()
        postEventOk(RequestEventLote.DELETE_EVENT, getLotes(unidad_productiva_id),lote);
    }

    //endregion

    //region Events

    private fun postEventOk(type: Int,lotes:List<Lote>?,lote:Lote?) {
        var loteListMitable= lotes as MutableList<Object>
        var LoteMutable:Object?=null
        if(lote!=null){
            LoteMutable = lote as Object
        }
        postEvent(type, loteListMitable,LoteMutable,null)
    }

    private fun postEventError(type: Int,messageError:String) {
        postEvent(type, null,null,messageError)
    }

    private fun postEventListUp(type: Int,listUnidadProductiva:List<UnidadProductiva>?,messageError:String?) {
        var upMutable= listUnidadProductiva as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }

    private fun postEventListUnidadMedida(type: Int,listUnidadMedida:List<Unidad_Medida>?,messageError:String?) {
        var upMutable= listUnidadMedida as MutableList<Object>
        postEvent(type, upMutable,null,messageError)
    }


    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventLote(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}