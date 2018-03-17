package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.asistencia_tecnica.models.login.Login
import com.interedes.agriculturappv3.asistencia_tecnica.models.login.LoginResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.UsuarioResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.raizlabs.android.dbflow.kotlinextensions.and
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepositoryImpl : LoginRepository {

    var eventBus: EventBus? = null
    var mAuth: FirebaseAuth? = null
    val mDatabase: DatabaseReference?
    var mUserDatabase: DatabaseReference? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        mDatabase = FirebaseDatabase.getInstance().reference
        apiService = ApiInterface.create()
    }

    //region Interfaz
    override fun ingresar(login: Login) {
        //Firebase
        mAuth = FirebaseAuth.getInstance()
        //Si se ha verificado el correo electrónico hace los inicios de Sesión
        if (mAuth?.currentUser?.isEmailVerified!!) {
            val call = apiService?.postLogin(login)
            call?.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                    if (response != null && response.code() == 200) {
                        
                    }
                }

                override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                    postEventError(RequestEvent.ERROR_EVENT, "Petición fallida al Servidor")
                    Log.e("Error Post", t?.message.toString())
                }
            })

            /*SI NO HA VERIFICADO EL CORREO */
        } else {
            postEventError(RequestEvent.ERROR_EVENT, "No se ha verificado el correo electrónico")
        }


        /*val call = apiService?.postLogin(login)
        call?.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                if (response != null && response.code() == 200) {
                    val access_token: String? = response.body()?.access_token
                    //FIREBASE
                    mAuth = FirebaseAuth.getInstance()
                    //Si se ha verificado el correo electrónico hace los inicios de Sesión
                    if (mAuth?.currentUser?.isEmailVerified!!) {
                        mAuth?.signInWithEmailAndPassword(login.username!!, login.password!!)?.addOnCompleteListener({ task ->
                            if (task.isSuccessful) {
                                val current_user_id = mAuth?.getCurrentUser()?.getUid()
                                mUserDatabase = mDatabase?.child("Users")
                                if (current_user_id != null) {

                                }

                                /* mUserDatabase?.child(current_user_id)?.child("Access_Token")?.setValue(access_token)?.addOnSuccessListener({
                                 val usuario_remembered = true
                                 if (mAuth?.currentUser?.isEmailVerified!!) {

                                 }


                                 /*
                                 val query: String = Listas.queryGeneral("Email", login.username!!)
                                 //Get usuario
                                 val callUsuario = apiService?.getUsuarioByCorreo(query)
                                 callUsuario?.enqueue(object : Callback<UsuarioResponse> {
                                     override fun onResponse(call: Call<UsuarioResponse>?, response: Response<UsuarioResponse>?) {
                                         if (response != null && response.code() == 200) {
                                             val usuario: List<Usuario>? = response.body()?.value!!
                                             for (u: Usuario in usuario!!) {
                                                 if (u.Email.equals(login.username)) {
                                                     u.Contrasena = login.password
                                                     u.AccessToken = access_token
                                                     u.UsuarioRemembered = usuario_remembered
                                                     u.save()
                                                 }
                                             }

                                             postEvent(RequestEvent.SAVE_EVENT)

                                         } else {
                                             postEventError(RequestEvent.ERROR_EVENT, "Petición fallida al Servidor")
                                             Log.e("Error Response Get", response?.errorBody()?.string())
                                         }
                                     }

                                     override fun onFailure(call: Call<UsuarioResponse>?, t: Throwable?) {
                                         postEventError(RequestEvent.ERROR_EVENT, "Petición fallida al Servidor")
                                         Log.e("Error Get", t?.message.toString())
                                     }

                                 })*/
                             })*/
                            } else {
                                try {
                                    throw task.exception!!
                                } catch (firebaseException: FirebaseException) {
                                    postEventError(RequestEvent.ERROR_EVENT, "Petición fallida a Firebase")
                                    Log.e("Error Post", firebaseException.toString())
                                }
                            }
                        })
                    } else {
                        postEventError(RequestEvent.ERROR_EVENT, "No se ha verificado el correo electrónico")
                    }

                } else {
                    postEventError(RequestEvent.ERROR_EVENT, "Usuario o Contraseña Incorrectos")
                    Log.e("Error Response Post", response?.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                postEventError(RequestEvent.ERROR_EVENT, "Petición fallida al Servidor")
                Log.e("Error Post", t?.message.toString())
            }


        })*/
    }

    override fun getSqliteUsuario(login: Login) {
        val usuario_sqlite = getUsuario(login)
        if (usuario_sqlite != null) {
            usuario_sqlite.UsuarioRemembered = true
            usuario_sqlite.save()
            postEvent(RequestEvent.SAVE_EVENT)
        } else {
            postEventError(RequestEvent.ERROR_EVENT, "Usuario o Contraseña Incorrectos")
        }
    }

    //region Querys Sqlite

    private fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }

    private fun getUsuario(login: Login): Usuario? {
        val sqlite_usuario = SQLite.select().from(Usuario::class.java).where(Usuario_Table.Email.eq(login.username) and Usuario_Table.Contrasena.eq(login.password)).querySingle()
        return sqlite_usuario
    }

    //endregion

    //region Eventos
    private fun postEvent(type: Int) {
        val event = RequestEvent(type, null, null, null)
        event.eventType = type
        eventBus?.post(event)
    }


    //Post Event User Logued
    private fun postEventUsuarioOk(type: Int, usuario: Usuario?) {
        postEventObjectUsuario(type, usuario, null)
    }

    //Post Object user
    private fun postEventObjectUsuario(type: Int, usuario: Usuario?, errorMessage: String?) {
        var usuarioMutable = usuario as Object
        val event = RequestEvent(type, null, usuarioMutable, errorMessage)
        event.eventType = type
        event.mensajeError = errorMessage
        eventBus?.post(event)
    }

    //Post Event Error
    private fun postEventError(type: Int, messageError: String?) {
        val errorEvent = RequestEvent(type, null, null, messageError)
        errorEvent.eventType = type
        errorEvent.mensajeError = messageError
        eventBus?.post(errorEvent)
    }


    //endregion
}