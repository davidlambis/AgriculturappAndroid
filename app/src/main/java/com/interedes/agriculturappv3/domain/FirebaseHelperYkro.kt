package com.interedes.agriculturappv3.domain

import com.google.firebase.database.DatabaseReference
import android.provider.Contacts.PresenceColumns.OFFLINE
import com.google.firebase.database.DatabaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class FirebaseHelperYkro {
    val dataReference: DatabaseReference

    val authUserEmail: String?
        get() {
            val user = FirebaseAuth.getInstance().currentUser
            var email: String? = null
            if (user != null) {
                email = user.email
            }
            return email
        }

    val myUserReference: DatabaseReference?
        get() = getUserReference(authUserEmail)

  /*  val myContactsReference: DatabaseReference
        get() = getContactsReference(authUserEmail)*/

    private object SingletonHolder {
        val INSTANCE = FirebaseHelper()
    }

    init {
        dataReference = FirebaseDatabase.getInstance().reference
    }

    fun getUserReference(email: String?): DatabaseReference? {
        var userReference: DatabaseReference? = null
        if (email != null) {
            val emailKey = email.replace(".", "_")
            userReference = dataReference.root.child(USERS_PATH).child(emailKey)
        }
        return userReference
    }

    /*
    fun getContactsReference(email: String?): DatabaseReference {
        return getUserReference(email)!!.child(CONTACTS_PATH)
    }

    fun getOneContactReference(mainEmail: String, childEmail: String?): DatabaseReference {
        val childKey = childEmail!!.replace(".", "_")
        return getUserReference(mainEmail)!!.child(CONTACTS_PATH).child(childKey)
    }

    fun getChatsReference(receiver: String): DatabaseReference {
        val keySender = authUserEmail!!.replace(".", "_")
        val keyReceiver = receiver.replace(".", "_")

        var keyChat = keySender + SEPARATOR + keyReceiver
        if (keySender.compareTo(keyReceiver) > 0) {
            keyChat = keyReceiver + SEPARATOR + keySender
        }
        return dataReference.root.child(CHATS_PATH).child(keyChat)
    }

    fun changeUserConnectionStatus(online: Boolean) {
        if (myUserReference != null) {
            val updates = HashMap<String, Any>()
            updates.put("online", online)
            myUserReference!!.updateChildren(updates)

            notifyContactsOfConnectionChange(online)
        }
    }

    @JvmOverloads
    fun notifyContactsOfConnectionChange(online: Boolean, signoff: Boolean = false) {
        val myEmail = authUserEmail
        myContactsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val email = child.key
                    val reference = getOneContactReference(email, myEmail)
                    reference.setValue(online)
                }
                if (signoff) {
                    FirebaseAuth.getInstance().signOut()
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {}
        })
    }

    fun signOff() {
       // notifyContactsOfConnectionChange(SearchResponse.OFFLINE, true)
    }*/

    companion object {
        private val SEPARATOR = "___"
        private val CHATS_PATH = "chats"
        private val USERS_PATH = "users"
        val CONTACTS_PATH = "contacts"

        val instance: FirebaseHelper
            get() = SingletonHolder.INSTANCE
    }
}