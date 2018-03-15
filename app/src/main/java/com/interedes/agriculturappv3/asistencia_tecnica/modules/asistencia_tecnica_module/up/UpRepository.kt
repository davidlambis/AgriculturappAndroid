package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up

import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

class UpRepository: IUnidadProductiva.Repo {
    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    override fun getListUPs() {
        postEventOk(RequestEvent.READ_EVENT,getUPs(),null)
    }

    override fun saveUp(mUnidadProductiva: UnidadProductiva) {
        mUnidadProductiva.save()
        postEventOk(RequestEvent.SAVE_EVENT,getUPs(), mUnidadProductiva)
    }

    override fun updateUp(mUnidadProductiva: UnidadProductiva) {
        mUnidadProductiva.update()
        postEventOk(RequestEvent.UPDATE_EVENT,getUPs(), mUnidadProductiva)
    }

    override fun deleteUp(mUnidadProductiva: UnidadProductiva) {
        mUnidadProductiva.delete()
        postEventOk(RequestEvent.DELETE_EVENT,getUPs(), mUnidadProductiva)
    }

    private fun postEventOk(type: Int, mups: List<UnidadProductiva>?, unidadProductiva: UnidadProductiva?){
        postEvent(type,mups, unidadProductiva,null)
    }

    private fun postEvent(type: Int, listUnidadProductivas: List<UnidadProductiva>?, unidadProductiva: UnidadProductiva?, message:String?){
        var UpMutable = unidadProductiva as MutableList<Object>
        val event = RequestEvent(type,UpMutable,null,message)
        event.eventType = type
        event.mensajeError = message
        eventBus?.post(event)
    }

    override fun getUPs(): List<UnidadProductiva> {
        return SQLite.select().from(UnidadProductiva::class.java!!).queryList()
    }
}