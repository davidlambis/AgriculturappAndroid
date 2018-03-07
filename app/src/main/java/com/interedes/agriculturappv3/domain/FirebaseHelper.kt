package com.interedes.agriculturappv3.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseHelper {

    val dataReference: DatabaseReference?

    init {
        dataReference = FirebaseDatabase.getInstance().reference
    }


}