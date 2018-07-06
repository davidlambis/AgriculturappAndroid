package com.interedes.agriculturappv3.services.chat

import android.util.Log
import com.google.firebase.database.ServerValue
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.raizlabs.android.dbflow.sql.language.SQLite

class ChatJobRepository:IMainViewJobChat.Repository {

    private val TAG = "FIREBASE UIID"
    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    override fun updateUserStatus() {
        val userLogued= getLastUserLogued()
        if(userLogued!=null){
            val uid= userLogued.IdFirebase
            if(uid!=""){
                Log.d(TAG, "FIREBASE SERVICE SECOND PLANE: $uid")
                Chat_Resources.mUserDBRef.child("$uid/status").setValue(Status_Chat.ONLINE)
                //Chat_Resources.Companion.getMUserDBRef().child(uid+"/last_Online").setValue(ServerValue.TIMESTAMP);
                Chat_Resources.mUserDBRef.child("$uid/status").onDisconnect().setValue(Status_Chat.OFFLINE)
            }
        }
    }
}