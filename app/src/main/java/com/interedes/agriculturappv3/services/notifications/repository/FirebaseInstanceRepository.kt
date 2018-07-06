package com.interedes.agriculturappv3.services.notifications.repository

import android.app.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.notifications.events.RequestEventFirebaseService
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite

class FirebaseInstanceRepository:IMainFirebaseInstance.Repository {


    var mUserDBRef: DatabaseReference? = null
    var eventBus: EventBus? = null

    private var  mCurrentUserID: String? = null
    init {
        mUserDBRef = Chat_Resources.mUserDBRef
    }

    override fun syncToken(token:String?) {
        val usuario= getUserLogued()
        if(usuario!=null){
            mCurrentUserID = FirebaseAuth.getInstance().currentUser?.uid
            if(mCurrentUserID==null){
                mCurrentUserID=usuario.IdFirebase
            }
            val userTokenMessaginStatus= mUserDBRef?.child(mCurrentUserID+"/statusTokenFcm")
            val userTokenMessaging= mUserDBRef?.child(mCurrentUserID+"/tokenFcm")
            userTokenMessaginStatus?.setValue(true)
            userTokenMessaging?.setValue(token)
            postEventOk(RequestEventFirebaseService.POST_SYNC_EVENT_TOKEN)
        }else{
            postEventError(RequestEventFirebaseService.ERROR_EVENT,"No existe un usuario logueado para el token: $token")
        }
    }


    override fun saveNotification(notification: NotificationLocal) {
        val lastNotification = getLastNotification()
        if (lastNotification == null) {
            notification.Id = 1
        } else {
            notification.Id = lastNotification.Id!! + 1
        }
        val lastUserLogued= getUserLogued()
        notification.userLoguedId=lastUserLogued?.Id
        notification.save()
    }



    override fun getLastNotification(): NotificationLocal? {
        //val lastNotification = SQLite.select().from(Notification::class.java).orderBy(Notification_Table.Id, false).querySingle()
        return null
    }


    fun getUserLogued(): Usuario?{
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    //region Events
    private fun postEventOk(type: Int) {
        postEvent(type, null,null,null)
    }


    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestEventFirebaseService(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
}