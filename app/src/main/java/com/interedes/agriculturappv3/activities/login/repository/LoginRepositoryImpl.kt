package com.interedes.agriculturappv3.activities.login.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.activities.login.events.LoginEvent
import com.interedes.agriculturappv3.productor.models.login.Login
import com.interedes.agriculturappv3.productor.models.usuario.*
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.login.LoginResponse
import com.interedes.agriculturappv3.productor.models.rol.Rol
import com.interedes.agriculturappv3.productor.models.rol.Rol_Table
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
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
                if (response != null && response.code() == 200) {
                    val access_token: String = response.body()?.access_token!!
                    val query = Listas.queryGeneral("Email", login.username!!)
                    val call_usuario = apiService?.getAuthUserByCorreo("Bearer " + access_token, query)


                    call_usuario?.enqueue(object : Callback<GetUserResponse> {
                        override fun onResponse(call: Call<GetUserResponse>?, response: Response<GetUserResponse>?) {
                            if (response != null && response.code() == 200) {
                                val user_login: MutableList<UserLoginResponse>? = response.body()?.value!!
                                val usuario = Usuario()
                                for (item in user_login!!) {
                                    val ultimo_usuario = getLastUser()
                                    var session_id: Long?
                                    if (ultimo_usuario == null) {
                                        session_id = 1
                                    } else {
                                        session_id = ultimo_usuario.SessionId!! + 1
                                    }
                                    val rol = SQLite.select().from(Rol::class.java).where(Rol_Table.Id.eq(item.tipouser)).querySingle()
                                    val rolNombre = rol?.Nombre
                                    usuario.DetalleMetodoPagoId = item.detalleMetodopagoId
                                    usuario.RolId = item.tipouser
                                    usuario.Apellidos = item.apellido
                                    usuario.Nombre = item.nombre
                                    usuario.NumeroCuenta = item.nroCuenta
                                    usuario.Identificacion = item.identification
                                    usuario.Id = item.id
                                    usuario.UserName = item.userName
                                    //TODO Encriptar contraseña
                                    usuario.Contrasena = login.password
                                    usuario.Email = item.email
                                    usuario.EmailConfirmed = item.emailConfirmed
                                    usuario.PhoneNumber = item.phoneNumber
                                    usuario.PhoneNumberConfirmed = item.phoneNumberConfirmed
                                    usuario.UsuarioRemembered = true
                                    usuario.AccessToken = access_token
                                    usuario.RolNombre = rolNombre
                                    usuario.SessionId = session_id
                                    usuario.save()
                                }
                                mAuth = FirebaseAuth.getInstance()
                                mAuth?.signInWithEmailAndPassword(login.username!!, login.password!!)?.addOnCompleteListener({ task ->
                                    if (task.isSuccessful) {
                                        postEventUsuarioOk(LoginEvent.SAVE_EVENT, usuario)
                                    } else {
                                        try {
                                            throw task.exception!!
                                        } catch (firebaseException: FirebaseException) {
                                            postEventError(LoginEvent.ERROR_EVENT, firebaseException.toString())
                                            Log.e("Error Post", firebaseException.toString())
                                        }
                                    }

                                })


                            } else {
                                postEventError(LoginEvent.ERROR_EVENT, "No puede ingresar, compruebe su conexión")
                                Log.e("Get Login User Response", response?.body().toString())
                            }
                        }

                        override fun onFailure(call: Call<GetUserResponse>?, t: Throwable?) {
                            postEventError(LoginEvent.ERROR_EVENT, "No puede ingresar, compruebe su conexión")
                            Log.e("Failure Get Login User", t?.message.toString())
                        }


                    })

                } else {
                    postEventError(LoginEvent.ERROR_EVENT, "Usuario o Contraseña Incorrectos")
                    Log.e("Failure Login", response?.message().toString())
                }

            }

            override fun onFailure(call: Call<LoginResponse>?, t: Throwable?) {
                postEventError(LoginEvent.ERROR_EVENT, "No puede ingresar, compruebe su conexión")
                Log.e("Failure Login", t?.message.toString())
            }


        })


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

    private fun getLastUser(): Usuario? {
        if (SQLite.select().from(Usuario::class.java).queryList().size > 0) {
            val usuarioLogued = SQLite.select().from(Usuario::class.java).where().orderBy(Usuario_Table.SessionId, false).querySingle()
            return usuarioLogued
        }
        return null
    }


    private fun getUsuario(login: Login): Usuario? {
        val sqlite_usuario = SQLite.select().from(Usuario::class.java).where(Usuario_Table.Email.eq(login.username)).and(Usuario_Table.Contrasena.eq(login.password)).querySingle()
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