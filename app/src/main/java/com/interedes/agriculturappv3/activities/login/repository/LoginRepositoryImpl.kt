package com.interedes.agriculturappv3.activities.login.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.activities.login.events.LoginEvent
import com.interedes.agriculturappv3.productor.models.login.Login
import com.interedes.agriculturappv3.productor.models.usuario.*
import com.interedes.agriculturappv3.events.RequestEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.login.LoginResponse
import com.interedes.agriculturappv3.productor.models.rol.AspNetRolResponse
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

        val call = apiService?.postLogin(login)
        call?.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                val access_token: String = response?.body()?.access_token!!
                val query = Listas.queryGeneral("Email", login.username!!)
                val call_usuario = apiService?.getAuthUserByCorreo(access_token, query)

                call_usuario?.enqueue(object : Callback<GetUserResponse> {
                    override fun onResponse(call: Call<GetUserResponse>?, response: Response<GetUserResponse>?) {
                        val user_login: List<UserResponse>? = response?.body()?.value!!
                        for (item in user_login!!) {
                            val tipo_user = item.tipouser
                            val query = Listas.queryGeneral("Id", tipo_user.toString())
                            val call_asp_net_roles = apiService?.getAspNetRolesByTipoUser(access_token, query)
                            call_asp_net_roles?.enqueue(object : Callback<AspNetRolResponse> {
                                override fun onResponse(call: Call<AspNetRolResponse>?, response: Response<AspNetRolResponse>?) {
                                    val asp_net_rol = response?.body()?.value

                                }

                                override fun onFailure(call: Call<AspNetRolResponse>?, t: Throwable?) {
                                    postEventError(RequestEvent.ERROR_EVENT, t?.message.toString())
                                    Log.e("Failure Rol Response", t?.message.toString())
                                }
                            })
                        }
                    }

                    override fun onFailure(call: Call<GetUserResponse>?, t: Throwable?) {
                        postEventError(RequestEvent.ERROR_EVENT, t?.message.toString())
                        Log.e("Failure Get Login User", t?.message.toString())
                    }
                })

            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                postEventError(RequestEvent.ERROR_EVENT, t?.message.toString())
                Log.e("Failure Login Admin", t?.message.toString())
            }


        })


        //FIREBASE
        /*var emailConfirmed: Boolean? = false
        mAuth = FirebaseAuth.getInstance()
        mAuth?.signInWithEmailAndPassword(login.username!!, login.password!!)?.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                emailConfirmed = mAuth?.currentUser?.isEmailVerified
                //Si el correo está verificado
                if (emailConfirmed!!) {
                    postEvent(RequestEvent.SAVE_EVENT)
                    //Primero debe consultar si en el backend el usuario tiene el atributo EmailConfirmed = true
                    //LOGIN DESDE ADMIN
                    /***val em = LoginActivity.em
                    val ps = LoginActivity.ps
                    val admin_login = Login(em, ps)
                    val call = apiService?.postLogin(admin_login)
                    call?.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
                    if (response != null && response.code() == 200) {
                    val access_token: String = response.body()?.access_token!!
                    //TRAER USUARIO QUE VA A HACER LOGIN POR CORREO
                    val query = Listas.queryGeneral("Email", login.username!!)
                    val call_usuario = apiService?.getAuthUserByCorreo(access_token, query)
                    call_usuario?.enqueue(object : Callback<GetUserResponse> {
                    override fun onResponse(call: Call<GetUserResponse>?, response: Response<GetUserResponse>?) {
                    val user_login: List<UserResponse>? = response?.body()?.value!!
                    //Actualizar EmailConfirmed
                    }

                    override fun onFailure(call: Call<GetUserResponse>?, t: Throwable?) {
                    postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
                    Log.e("Failure Get Login User", t?.message.toString())
                    }
                    })
                    } else {
                    postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
                    Log.e("response Login Admin", response?.errorBody().toString())
                    }
                    }

                    override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                    postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
                    Log.e("Failure Login Admin", t?.message.toString())
                    }
                    }) ***/
                } else {
                    postEventError(RequestEvent.ERROR_EVENT, "Correo no Verificado")
                }
            } else {
                try {
                    throw task.exception!!
                } catch (firebaseException: FirebaseException) {
                    postEventError(RequestEvent.ERROR_EVENT, "Petición fallida a Firebase")
                    Log.e("Error Post", firebaseException.toString())
                }
            }
        }) */


        //Firebase
        //mAuth = FirebaseAuth.getInstance()
        //Si se ha verificado el correo electrónico hace los inicios de Sesión
        //if (mAuth?.currentUser?.isEmailVerified!!) {
        //LOGIN DESDE ADMIN
        /***val em = LoginActivity.em
        val ps = LoginActivity.ps
        val admin_login = Login(em, ps)
        val call = apiService?.postLogin(admin_login)
        call?.enqueue(object : Callback<LoginResponse> {
        override fun onResponse(call: Call<LoginResponse>?, response: Response<LoginResponse>?) {
        if (response != null && response.code() == 200) {
        val access_token: String = response.body()?.access_token!!
        //TRAER USUARIO QUE VA A HACER LOGIN POR CORREO
        val query = Listas.queryGeneral("Email", login.username!!)
        val call_usuario = apiService?.getAuthUserByCorreo(access_token, query)
        call_usuario?.enqueue(object : Callback<GetUserResponse> {
        override fun onResponse(call: Call<GetUserResponse>?, response: Response<GetUserResponse>?) {
        val user_login: List<UserResponse>? = response?.body()?.value!!
        //Actualizar EmailConfirmed
        }

        override fun onFailure(call: Call<GetUserResponse>?, t: Throwable?) {
        postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
        Log.e("Failure Get Login User", t?.message.toString())
        }
        })
        } else {
        postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
        Log.e("response Login Admin", response?.errorBody().toString())
        }
        }

        override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
        postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
        Log.e("Failure Login Admin", t?.message.toString())
        }
        }) ***/

        /*SI NO HA VERIFICADO EL CORREO */

        /* } else {
             postEventError(RequestEvent.ERROR_DIALOG_EVENT, "No se ha verificado el correo electrónico")
         }*/


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
                                             postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
                                             Log.e("Error Response Get", response?.errorBody()?.string())
                                         }
                                     }

                                     override fun onFailure(call: Call<UsuarioResponse>?, t: Throwable?) {
                                         postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
                                         Log.e("Error Get", t?.message.toString())
                                     }

                                 })*/
                             })*/
                            } else {
                                try {
                                    throw task.exception!!
                                } catch (firebaseException: FirebaseException) {
                                    postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida a Firebase")
                                    Log.e("Error Post", firebaseException.toString())
                                }
                            }
                        })
                    } else {
                        postEventError(RequestEvent.ERROR_DIALOG_EVENT, "No se ha verificado el correo electrónico")
                    }

                } else {
                    postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Usuario o Contraseña Incorrectos")
                    Log.e("Error Response Post", response?.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                postEventError(RequestEvent.ERROR_DIALOG_EVENT, "Petición fallida al Servidor")
                Log.e("Error Post", t?.message.toString())
            }


        })*/
    }

    override fun getSqliteUsuario(login: Login) {
        val usuario_sqlite = getUsuario(login)
        if (usuario_sqlite != null) {
            usuario_sqlite.UsuarioRemembered = true
            usuario_sqlite.save()
            postEvent(LoginEvent.SAVE_EVENT)
        } else {
            postEventError(LoginEvent.ERROR_EVENT, "Usuario o Contraseña Incorrectos")
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
        val event = LoginEvent(type, null, null, null)
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
        val event = LoginEvent(type, null, usuarioMutable, errorMessage)
        event.eventType = type
        event.mensajeError = errorMessage
        eventBus?.post(event)
    }

    //Post Event Error
    private fun postEventError(type: Int, messageError: String?) {
        val errorEvent = LoginEvent(type, null, null, messageError)
        errorEvent.eventType = type
        errorEvent.mensajeError = messageError
        eventBus?.post(errorEvent)
    }


    //endregion
}