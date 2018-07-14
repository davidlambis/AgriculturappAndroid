package com.interedes.agriculturappv3.services.notifications.repository

import android.app.Notification
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal_Table
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
        eventBus = GreenRobotEventBus()
        mUserDBRef = Chat_Resources.mUserDBRef
    }

    override fun syncToken(token:String?) {
        val usuario= getUserLogued()
        if(usuario!=null){
            mCurrentUserID = FirebaseAuth.getInstance().currentUser?.uid
            if(mCurrentUserID==null){
                mCurrentUserID=usuario.IdFirebase
            }

            if(mCurrentUserID!=null){
                val userTokenMessaginStatus= mUserDBRef?.child(mCurrentUserID+"/statusTokenFcm")
                val userTokenMessaging= mUserDBRef?.child(mCurrentUserID+"/tokenFcm")
                userTokenMessaginStatus?.setValue(true)
                userTokenMessaging?.setValue(token)
            }
            //postEventOk(RequestEventFirebaseService.POST_SYNC_EVENT_TOKEN)
            Log.d("TOKEN", "Token Generate ")
        }else{
            Log.d("TOKEN", "userLogued is null, token no generate ")
            //postEventError(RequestEventFirebaseService.ERROR_EVENT,"No existe un usuario logueado para el token: $token")
        }
    }

    override fun saveNotification(notification: NotificationLocal) {
        val lastNotification = getLastNotification()
        if (lastNotification == null) {
            notification.Id = 1
        } else {
            notification.Id = lastNotification.Id!! + 1
        }

        notification.time= System.currentTimeMillis()
        val lastUserLogued= getUserLogued()
        notification.userLoguedId=lastUserLogued?.Id
        notification.save()
    }


    override fun getLastNotification(): NotificationLocal? {
        val lastNotification = SQLite.select().from(NotificationLocal::class.java).orderBy(NotificationLocal_Table.Id, false).querySingle()
        return lastNotification
    }


    fun getUserLogued(): Usuario?{
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }
}