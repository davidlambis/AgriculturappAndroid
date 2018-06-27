package com.interedes.agriculturappv3.services.notifications

import android.preference.PreferenceManager
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.interedes.agriculturappv3.services.Const

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {


     private val TAG = "MyFirebaseIIDService"
     override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Refreshed token service: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        val preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
        preferences.edit().putString(Const.FIREBASE_TOKEN, refreshedToken).apply()


        // TODO: Implement this method to send any registration to your app's servers.
        //sendRegistrationToServer(refreshedToken);


        /*
        ID de instancia es estable excepto cuando:

        La aplicación elimina la ID de la instancia
        La aplicación se restaura en un nuevo dispositivo
        El usuario desinstala / reinstala la aplicación
        El usuario borra los datos de la aplicación


        En los casos anteriores, se genera una nueva ID de instancia y la aplicación necesita recrear los tokens de autorización que se implementaron previamente onTokenRefresh().
    */


    }

    private fun sendRegistrationToServer(token: String) {
        // Add custom implementation, as needed.
    }

}