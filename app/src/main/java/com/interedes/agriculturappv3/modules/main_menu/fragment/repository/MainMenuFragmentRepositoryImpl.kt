package com.interedes.agriculturappv3.modules.main_menu.fragment.repository

import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.google.gson.JsonParser
import com.google.gson.Gson
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import java.util.*


class MainMenuFragmentRepositoryImpl : MainMenuFragmentRepository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null


    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
        //mUserDatabase = mDatabase.child("Users")
        //mAuth = FirebaseAuth.getInstance()
        //mUserReference = mUserDatabase?.child(mAuth?.currentUser?.uid)


    }


    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    fun <T> getObjectList(jsonString: String, cls: Class<T>): List<T> {
        val list = ArrayList<T>()
        try {
            val gson = Gson()
            val arry = JsonParser().parse(jsonString).asJsonArray
            for (jsonElement in arry) {
                list.add(gson.fromJson(jsonElement, cls))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }




    //endregion


    //region Events
    private fun postEvent(type: Int) {
        postEvent(type, null)
    }

    private fun postEvent(type: Int, errorMessage: String?) {
        val event = RequestEvent(type, null, null, errorMessage)
        event.eventType = type
        event.mensajeError = errorMessage
        eventBus?.post(event)
    }
    //endregion

}