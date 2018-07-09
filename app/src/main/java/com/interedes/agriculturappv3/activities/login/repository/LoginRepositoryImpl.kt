package com.interedes.agriculturappv3.activities.login.repository

import android.content.Context
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.interedes.agriculturappv3.activities.login.events.LoginEvent
import com.interedes.agriculturappv3.modules.models.login.Login
import com.interedes.agriculturappv3.modules.models.usuario.*
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.ResetPassword
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.login.LoginResponse
import com.interedes.agriculturappv3.modules.models.rol.Rol
import com.interedes.agriculturappv3.modules.models.rol.RolUserLogued
import com.interedes.agriculturappv3.modules.models.rol.Rol_Table
import com.interedes.agriculturappv3.modules.productor.comercial_module.productos.events.ProductosEvent
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.interedes.agriculturappv3.services.listas.Listas
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.kotlinextensions.update
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.Transaction
import com.google.firebase.iid.FirebaseInstanceId
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga_Table
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.lote.Lote_Table
import com.interedes.agriculturappv3.modules.models.ofertas.DetalleOferta
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.produccion.Produccion
import com.interedes.agriculturappv3.modules.models.produccion.Produccion_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva_Table
import com.interedes.agriculturappv3.modules.models.ventas.Tercero
import com.interedes.agriculturappv3.modules.models.ventas.Transaccion
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.chat.SharedPreferenceHelper
import com.interedes.agriculturappv3.services.resources.Chat_Resources
import com.interedes.agriculturappv3.services.resources.RolResources
import com.interedes.agriculturappv3.services.resources.Status_Sync_Data_Resources
import java.io.IOException


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
    override fun ingresar(login: Login,context: Context) {
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


                                val usuario:Usuario= Usuario()
                                val ultimo_usuario = getLastUser()

                                for (item in user_login!!) {


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

                                }


                                //Verificate Rol User
                                val call_usuario = apiService?.getUsuarioLogued(usuario?.Id.toString())
                                call_usuario?.enqueue(object : Callback<Usuario> {
                                    override fun onResponse(call: Call<Usuario>?, response: Response<Usuario>?) {
                                        if (response != null && response.code() == 200) {

                                            val userLoguedResponse: Usuario = response.body()!!

                                            val rol = Rol()
                                            rol.Id=userLoguedResponse?.Rol?.Id
                                            rol.Nombre=userLoguedResponse?.Rol?.Nombre
                                            rol.save()


                                            if(rol?.Nombre.equals(RolResources.PRODUCTOR) && ultimo_usuario?.RolNombre.equals(RolResources.COMPRADOR) ||
                                                    rol?.Nombre.equals(RolResources.COMPRADOR) && ultimo_usuario?.RolNombre.equals(RolResources.PRODUCTOR)){
                                                cleanDataSqlite()
                                            }



                                            /*if(userLoguedResponse.Fotopefil!=null){
                                                try {
                                                    val base64String = userLoguedResponse.Fotopefil
                                                    val base64Image = base64String?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }!!.toTypedArray()[1]
                                                    val byte = Base64.decode(base64Image, Base64.DEFAULT)
                                                    usuario.blobImagenUser = Blob(byte)
                                                }catch (ex:Exception){
                                                    var ss= ex.toString()
                                                    Log.d("Convert Image", "defaultValue = " + ss);
                                                }
                                            }*/

                                            usuario.Fotopefil=userLoguedResponse.Fotopefil
                                            usuario.RolNombre = rol?.Nombre
                                            usuario.save()

                                            loginFirebaseUser(usuario,context)

                                        } else {
                                            postEventError(LoginEvent.ERROR_EVENT, "No puede ingresar, compruebe su conexión")
                                            Log.e("Get Login User Response", response?.body().toString())
                                        }
                                    }
                                    override fun onFailure(call: Call<Usuario>?, t: Throwable?) {
                                        postEventError(LoginEvent.ERROR_EVENT, "No puede ingresar, compruebe su conexión")
                                        Log.e("Failure Get Login User", t?.message.toString())
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

                } else if( response?.code() == 503){
                    postEventError(LoginEvent.ERROR_EVENT, "Estamos experiemntando inconvenientes para ingresar, disculpa las molestias")
                    Log.e("Failure Login", response?.message().toString())
                }

                else {
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

    private fun loginFirebaseUser(usuario: Usuario, context:Context) {
        mAuth = FirebaseAuth.getInstance()
        mAuth?.signInWithEmailAndPassword(usuario.Email!!, usuario.Contrasena!!)?.addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                var mCurrentUserID =task.result.user.uid
                usuario.IdFirebase=mCurrentUserID
                usuario.save()

                val reference: DatabaseReference?  = Chat_Resources.mUserDBRef
                val userPassword= reference?.child(mCurrentUserID+"/token_Account")
                userPassword?.setValue(usuario.Contrasena);

                val id_Account_Remote= reference?.child(mCurrentUserID+"/id_Account_Remote")
                id_Account_Remote?.setValue(usuario.Id.toString());

                resetTokenFCM(context)
                //val usuarioLoguedList = SQLite.select().from(Usuario::class.java).queryList()
                postEventUsuarioOk(LoginEvent.SAVE_EVENT, usuario)
            } else {
                /*try {
                    throw task.exception!!
                } catch (firebaseException: FirebaseException) {
                }
                */
                try {
                    throw task.exception!!
                    // TODO: El usuario no existe
                } catch (invalidEmail: FirebaseAuthInvalidUserException) {
                    //if email doesn't exist or disabled.
                    Log.d("AUTH FIREBASE", "onComplete: invalid_email")
                    //FIREBASE
                    registerFirebaseUser(usuario,context)
                    // TODO: Credenciales de acceso incoreto
                } catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                    Log.d("AUTH FIREBASE", "onComplete: wrong_password")
                    postEventError(LoginEvent.ERROR_EVENT, "Contraseña Incorrecta")
                    // TODO: Excepcion desconocida
                } catch (e: Exception) {
                    Log.d("AUTH FIREBASE", "onComplete: " + e.message)
                    postEventError(LoginEvent.ERROR_EVENT, e.toString())
                    Log.e("Error Post", e.toString())
                }
                // if user enters wrong email.
                // if user enters wrong password.
            }
        })
    }

    private fun registerFirebaseUser(usuario: Usuario,context:Context) {
        //FIREBASE
        FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.Email!!, usuario.Contrasena!!)?.addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                val newUser = task.result.user
                //success creating user, now set display name as name
                val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(usuario.Nombre+" "+usuario.Apellidos)
                        .build()
                newUser.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                /***CREATE USER IN FIREBASE DB AND REDIRECT ON SUCCESS */
                                //createUserInDb(newUser.uid, newUser.displayName, newUser.email)
                                createUserInDb(newUser.uid,usuario,context)
                            } else {
                                postEventError(LoginEvent.ERROR_EVENT,  task.exception?.localizedMessage)
                                //error
                                //Toast.makeText(this@SignUpActivity, "Error " + task.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
            } else {
                try {
                    throw task.exception!!
                } catch (existEmail: FirebaseAuthUserCollisionException) {
                    postEventError(LoginEvent.ERROR_EVENT, "Correo ya Registrado")
                } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                    postEventError(LoginEvent.ERROR_EVENT, "Mal formato de correo")
                } catch (firebaseException: FirebaseException) {
                    postEventError (LoginEvent.ERROR_EVENT, task.exception.toString())
                }
            }
        }

    }




    private fun createUserInDb(user_id: String,usuario:Usuario,context:Context) {
        //val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        //val uid: String? = currentUser?.uid
        // val reference: DatabaseReference? = mDatabase?.child("Users")?.child(uid)
        /*---------------*/
        //val token: String? = FirebaseInstanceId.getInstance()?.token
        //val uuid_tipo_user = user.Tipouser as UUID?
        val rolName =usuario.RolNombre
        val reference: DatabaseReference?  = FirebaseDatabase.getInstance().reference.child("Users")
        var userFirebase = UserFirebase(user_id, usuario.Nombre, usuario.Apellidos, usuario.Identificacion, usuario.Email, rolName, usuario.PhoneNumber, Status_Chat.OFFLINE, 0, usuario.Contrasena,usuario.Id.toString())
        reference?.child(user_id)?.setValue(userFirebase)?.addOnCompleteListener(OnCompleteListener<Void> { task ->
            if (!task.isSuccessful) {
                //error
                postEventError(LoginEvent.ERROR_EVENT, task.exception.toString())
                Log.e("Error Firebase", task.exception.toString())
            } else {
                //success adding user to db as well
                //go to users chat list
                var userLastOnlineRef= reference?.child(user_id+"/last_Online")
                userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);
                loginFirebaseUser(usuario,context)
            }
        })
        /*
         val userMap = HashMap<String?, String?>()
         userMap.put("Rol", rolName)
         userMap.put("Nombres", response.body()?.nombre)
         userMap.put("Apellidos", response.body()?.apellido)
         userMap.put("Cedula", response.body()?.identification)
         userMap.put("Correo", response.body()?.email)
         userMap.put("Celular", response.body()?.phoneNumber)
         //userMap.put("FotoEnfermedad", "")
         // userMap.put("Token", token!!)

         reference?.setValue(userMap)?.addOnCompleteListener { task ->
             if (task.isSuccessful) {
                 postEvent(RegisterEvent.onRegistroExitoso)
             } else {
                 postEvent(RegisterEvent.onErrorRegistro, task.exception.toString())
                 Log.e("Error Firebase", task.exception.toString())
             }
         }
         */
    }


    fun cleanDataSqlite(){

        SQLite.delete<Usuario>(Usuario::class.java)
                .async()
                .execute()

        SQLite.delete<Unidad_Productiva>(Unidad_Productiva::class.java)
                .async()
                .execute()

        SQLite.delete<Lote>(Lote::class.java)
                .async()
                .execute()

        SQLite.delete<Cultivo>(Cultivo::class.java)
                .async()
                .execute()

        SQLite.delete<Produccion>(Produccion::class.java)
                .async()
                .execute()

        SQLite.delete<Producto>(Producto::class.java)
                .async()
                .execute()

        SQLite.delete<ControlPlaga>(ControlPlaga::class.java)
                .async()
                .execute()

        SQLite.delete<Transaccion>(Transaccion::class.java)
                .async()
                .execute()

        SQLite.delete<Oferta>(Oferta::class.java)
                .async()
                .execute()


        SQLite.delete<DetalleOferta>(DetalleOferta::class.java)
                .async()
                .execute()

        SQLite.delete<Tercero>(Tercero::class.java)
                .async()
                .execute()
    }

    override fun getSqliteUsuario(login: Login,context:Context) {
        val usuario_sqlite = getUsuario(login)
        if (usuario_sqlite != null) {

            val ultimo_usuario = getLastUser()
            var session_id: Long?
            if (ultimo_usuario == null) {
                session_id = 1
            } else {
                session_id = ultimo_usuario.SessionId!! + 1
            }

            if(usuario_sqlite?.RolNombre.equals(RolResources.PRODUCTOR) && ultimo_usuario?.RolNombre.equals(RolResources.COMPRADOR) || usuario_sqlite?.RolNombre.equals(RolResources.COMPRADOR) && ultimo_usuario?.RolNombre.equals(RolResources.PRODUCTOR)){
                cleanDataSqlite()
            }

            resetTokenFCM(context)

            usuario_sqlite.SessionId = session_id
            usuario_sqlite.UsuarioRemembered = true
            usuario_sqlite.save()


            postEventUsuarioOk(LoginEvent.SAVE_EVENT, usuario_sqlite)

        } else {
            postEventError(LoginEvent.ERROR_EVENT, "Usuario o Contraseña Incorrectos")
        }
    }

    private fun resetTokenFCM(context: Context) {
        try {
            // Check for current token
            val originalToken = getTokenFromPrefs(context)
            Log.d("TAG", "Token before deletion: $originalToken")
            // Resets Instance ID and revokes all tokens.
            Thread(Runnable {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                    // Clear current saved token
                    saveTokenToPrefs("",context)

                    // Check for success of empty token
                    val tokenCheck = getTokenFromPrefs(context)
                    Log.d("TAG", "Token deleted. Proof: $tokenCheck")

                    // Now manually call onTokenRefresh()
                    Log.d("TAG", "Getting new token")
                    FirebaseInstanceId.getInstance().token

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun saveTokenToPrefs(_token:String?,context: Context )
    {
        // Access Shared Preferences
        //var preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //var editor = preferences.edit();
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        // Save to SharedPreferences
        //editor.putString("registration_id", _token);
        //editor.apply();
        preferences.edit().putString(Const.FIREBASE_TOKEN, _token).apply()
        SharedPreferenceHelper.getInstance(context).savePostSyncData(Status_Sync_Data_Resources.STOP);
    }

    private fun  getTokenFromPrefs(context:Context):String?
    {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(Const.FIREBASE_TOKEN, null);
    }


    override fun resetPassword(correo: String) {
        val resetPassword = ResetPassword(correo)
        val call = apiService?.resetPassword(resetPassword)
        call?.enqueue(object : Callback<ResetPassword> {
            override fun onResponse(call: Call<ResetPassword>?, response: Response<ResetPassword>?) {
                if (response != null && response.code() == 200) {
                    postEventUsuarioOk(LoginEvent.RESET_PASSWORD_EVENT, null)
                } else {
                    postEventError(LoginEvent.ERROR_EVENT, "Usuario o Contraseña Incorrectos")
                }
            }

            override fun onFailure(call: Call<ResetPassword>?, t: Throwable?) {
                postEventError(ProductosEvent.ERROR_EVENT, "Comprueba tu conexión")
            }
        })
    }
    //region Querys Sqlite


    private fun getLastUser(): Usuario? {
        if (SQLite.select().from(Usuario::class.java).queryList().size > 0) {
            val usuarioLoguedSession = SQLite.select().from(Usuario::class.java).where().orderBy(Usuario_Table.SessionId, false).querySingle()
            return usuarioLoguedSession
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