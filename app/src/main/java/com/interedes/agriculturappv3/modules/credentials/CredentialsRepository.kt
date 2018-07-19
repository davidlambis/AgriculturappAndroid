package com.interedes.agriculturappv3.modules.credentials

import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.credentials.events.RequestCredentialsEvents
import com.interedes.agriculturappv3.modules.models.usuario.RequestCredentials
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CredentialsRepository:IMainViewCredentials.Repository {

    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null
    init {
        eventBus = GreenRobotEventBus()
        apiService = ApiInterface.create()
    }

    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    override fun updateCredentialsUserLogued(checkConection: Boolean,credentials: RequestCredentials) {
        if(checkConection){
            val userLogued= getLastUserLogued()
            val call = apiService?.changePasswordAccount("Bearer " + userLogued?.AccessToken,credentials)
            call?.enqueue(object : Callback<RequestCredentials> {
                override fun onResponse(call: Call<RequestCredentials>?, response: Response<RequestCredentials>?) {
                    if (response != null && response.code() == 204 || response?.code() == 200) {
                        userLogued?.Contrasena=credentials.newPassword
                        userLogued!!.save()
                        postEventOk(RequestCredentialsEvents.UPDATE_EVENT, null,userLogued)
                    } else {
                        postEventError(RequestCredentialsEvents.ERROR_PASSWORD_EVENT, "Contraseña Invalida")
                    }
                }
                override fun onFailure(call: Call<RequestCredentials>?, t: Throwable?) {
                    postEventError(RequestCredentialsEvents.ERROR_EVENT, "Comprueba tu conexión")
                }
            })
        }else{
            postEventError(RequestCredentialsEvents.ERROR_VERIFICATE_CONECTION, null)
        }
    }

    //region Events
    private fun postEventOk(type: Int, lisUsurio: List<Usuario>?, usuario: Usuario?) {
        var usuarioListMutable:MutableList<Object>? = null
        var usuarioMutable: Object? = null
        if (usuario != null) {
            usuarioMutable = usuario as Object
        }
        if (lisUsurio != null) {
            usuarioListMutable=lisUsurio as MutableList<Object>
        }
        postEvent(type, usuarioListMutable, usuarioMutable, null)
    }

    private fun postEventError(type: Int,messageError:String?) {
        postEvent(type, null,null,messageError)
    }

    //Main Post Event
    private fun postEvent(type: Int, listModel1:MutableList<Object>?,model:Object?,errorMessage: String?) {
        val event = RequestCredentialsEvents(type, listModel1, model, errorMessage)
        event.eventType = type
        eventBus?.post(event)
    }
    //endregion
    //endregion
}