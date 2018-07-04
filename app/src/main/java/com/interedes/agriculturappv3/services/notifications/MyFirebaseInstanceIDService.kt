package com.interedes.agriculturappv3.services.notifications

import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.notifications.events.RequestEventFirebaseService
import com.interedes.agriculturappv3.services.notifications.repository.FirebaseInstanceRepository
import com.interedes.agriculturappv3.services.notifications.repository.IMainFirebaseInstance
import com.interedes.agriculturappv3.services.services.JobSyncService
import org.greenrobot.eventbus.Subscribe

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    var repository:
    IMainFirebaseInstance.Repository? = null
    var eventBus: EventBus? = null

    init {
        repository = FirebaseInstanceRepository()
        eventBus = GreenRobotEventBus()
    }

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

         sendRegistrationToServer(refreshedToken)


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


    //region Suscribe Events
    @Subscribe
    fun onEventMainThread(event: RequestEventFirebaseService?) {
        when (event?.eventType) {
            RequestEventFirebaseService.POST_SYNC_EVENT_TOKEN -> {

                Toast.makeText(this, "Token Efectuado...", Toast.LENGTH_SHORT).show()
            }
            RequestEventFirebaseService.ERROR_EVENT -> {

                Toast.makeText(this, event.mensajeError, Toast.LENGTH_SHORT).show()

            }
        }
    }
    //endregion

    private fun sendRegistrationToServer(token: String) {
        // Add custom implementation, as needed.
        repository?.syncToken(token)
    }

    override fun onCreate() {
        //Log.d(TAG, "+onCreate()")
        super.onCreate()
        //Log.d(TAG, "-onCreate()")
        eventBus?.register(this)
    }


    override fun onDestroy() {
        Log.d(TAG, "+onDestroy()")
        super.onDestroy()
        eventBus?.unregister(this)
    }
}