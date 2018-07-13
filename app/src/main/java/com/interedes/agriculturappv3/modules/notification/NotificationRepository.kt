package com.interedes.agriculturappv3.modules.notification

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal_Table
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.notification.events.RequestEventsNotification
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.delete
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite

class NotificationRepository:IMainViewNotification.Repository {

    var eventBus: EventBus? = null
    init {
        eventBus = GreenRobotEventBus()

    }

    override fun getListNotification() {
        val userlogued= getLastUserLogued()
        val listNotification= SQLite.select().from(NotificationLocal::class.java)
                .where(NotificationLocal_Table.userLoguedId.eq(userlogued?.Id))
                .and(NotificationLocal_Table.ReadNotification.eq(false))
                .queryList()
        for (item in listNotification){
            postEventOk(RequestEventsNotification.ITEM_NEW_EVENT,null,item)
        }
        postEventOk(RequestEventsNotification.LIST_EVENT_NOTIFICATION,listNotification,null)
    }


    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    override fun updateNotifications(notification: NotificationLocal) {
        notification.ReadNotification=true
        notification.update()
        postEventOk(RequestEventsNotification.UPDATE_EVENT,null,notification)
    }

    override fun deleteNotification(notification: NotificationLocal) {
       notification.delete()

        val userlogued= getLastUserLogued()
        val listNotification= SQLite.select().from(NotificationLocal::class.java).where(NotificationLocal_Table.userLoguedId.eq(userlogued?.Id)).queryList()
        postEventOk(RequestEventsNotification.LIST_EVENT_NOTIFICATION,listNotification,null)
        postEventOk(RequestEventsNotification.DELETE_EVENT,null,notification)
    }


    //region Events
    private fun postEventOk(type: Int, list: List<NotificationLocal>?, notification: NotificationLocal?) {
        var listMutable:MutableList<Object>? = null
        var notificationMutable: Object? = null
        if (notification != null) {
            notificationMutable = notification as Object
        }
        if (list != null) {
            listMutable=list as MutableList<Object>
        }
        postEvent(type, listMutable, notificationMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }



    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventsNotification(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion

}