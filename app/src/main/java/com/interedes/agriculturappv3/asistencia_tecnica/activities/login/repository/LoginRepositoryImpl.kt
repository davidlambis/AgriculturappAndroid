package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

class LoginRepositoryImpl : LoginRepository {

    var eventBus: EventBus? = null
    val mDatabase: DatabaseReference?

    init {
        eventBus = GreenRobotEventBus()
        mDatabase = FirebaseDatabase.getInstance().reference
    }

    //region Interfaz
    override fun ingresar(email: String, password: String) {

    }

   /* //SAVE en SQLITE
    override fun updateUsuario(usuario: Usuario) {

    }

    override fun getUsuario(usuario: Usuario) {

    } */
    //endregion

    //region Eventos

    //endregion
}