package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up

import com.interedes.agriculturappv3.asistencia_tecnica.models.UP
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

    override fun saveUp(mUp: UP) {
        mUp.save()
        postEventOk(RequestEvent.SAVE_EVENT,getUPs(),mUp)
    }

    override fun updateUp(mUp: UP) {
        mUp.update()
        postEventOk(RequestEvent.UPDATE_EVENT,getUPs(),mUp)
    }

    override fun deleteUp(mUp: UP) {
        mUp.delete()
        postEventOk(RequestEvent.DELETE_EVENT,getUPs(),mUp)
    }

    private fun postEventOk(type: Int,mups: List<UP>?,up:UP?){
        postEvent(type,mups,up,null)
    }

    private fun postEvent(type: Int,listUps: List<UP>?,up: UP?,message:String?){
        var UpMutable = up as MutableList<Object>
        val event = RequestEvent(type,UpMutable,null,message)
        event.eventType = type
        event.mensajeError = message
        eventBus?.post(event)
    }

    override fun getUPs(): List<UP> {
        return SQLite.select().from(UP::class.java!!).queryList()
    }
}