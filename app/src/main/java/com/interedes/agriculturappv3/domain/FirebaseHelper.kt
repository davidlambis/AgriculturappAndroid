package com.interedes.agriculturappv3.domain

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    val dataReference: DatabaseReference?

    init {
        dataReference = FirebaseDatabase.getInstance().reference
    }

    companion object {
        private val USERS_PATH = "Usuarios"
    }

}