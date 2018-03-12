package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.UP
import com.interedes.agriculturappv3.events.ListEvent
import com.interedes.agriculturappv3.events.RequestEvent
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
    override fun saveLotes(lote: Lote) {
        lote.save()
        val lotes = getLotes()
        postEventOk(RequestEvent.SAVE_EVENT,lotes,lote);

    }

    override fun getListLotes() {
        val lotes = getLotes()
        postEventOk(RequestEvent.READ_EVENT,lotes,null);
    }

    override fun getListUp() {
        var listUp= Listas.listaUP()
        postEventList(ListEvent.LIST_EVENT,listUp,null);
    }


    override fun getLotes():List<Lote> {
        val lotes = SQLite.select().from(Lote::class.java!!).queryList()
        return lotes;
    }

    override fun updateLote(lote: Lote) {
        lote.update()
        postEventOk(RequestEvent.UPDATE_EVENT, getLotes(),lote);

    }

    override fun deleteLote(lote: Lote) {
        lote.delete()
        //SQLite.delete<Lote>(Lote::class.java).where(Lote_Table.Id.eq(lote.Id)).async().execute()
        postEventOk(RequestEvent.DELETE_EVENT, getLotes(),lote);
    }

    //endregion

    //region Events
    private fun postEventOk(type: Int,lotes:List<Lote>?,lote:Lote?) {
        postEvent(type, lotes,lote,null)
    }

    private fun postEventError(type: Int,messageError:String) {
        postEvent(type, null,null,messageError)
    }

    private fun postEvent(type: Int, lotes:List<Lote>?,lote:Lote?,errorMessage: String?) {
        var loteMitable= lotes as MutableList<Object>
        val event = RequestEvent(type, loteMitable, null, errorMessage)
        event.eventType = type
        event.mensajeError = errorMessage
        eventBus?.post(event)
    }


    private fun postEventList(type: Int, listUp:List<UP>?,error:String?) {
        var upMutable= listUp as MutableList<Object>
        val event = ListEvent(type, upMutable, error)
        event.eventType = type
        event.mensajeError = null
        eventBus?.post(event)
    }


    /*
    private fun postEventListLotes(type: Int, lotes:List<Lote>?,messageError: String) {

        var loteMitable= lotes as MutableList<Object>
        val listEvent = RequestEvent(type, loteMitable, null, messageError)
        listEvent.eventType = type
        listEvent.mensajeError = messageError
        listEvent.mutableList = loteMitable
        eventBus?.post(listEvent)
    }*/

    //endregion

}