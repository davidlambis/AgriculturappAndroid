package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.events.RegisterEvent
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPago_Table
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol_Table
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.User
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.UserResponse
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseUser
import java.util.*


class RegisterUserRepositoryImpl : RegisterUserRepository {

    val mDatabase: DatabaseReference?
    var eventBus: EventBus? = null
    var apiService: ApiInterface? = null

    init {
        eventBus = GreenRobotEventBus()
        mDatabase = FirebaseDatabase.getInstance().reference
        apiService = ApiInterface.create()
    }


    //Registro de user en backend, firebase y sqlite
    override fun registerUsuario(user: User) {

        //BACKEND

        val call = apiService?.postRegistroUsers(user)
        call?.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>?, response: Response<UserResponse>?) {
                if (response != null && response.code() == 201) {
                    //FIREBASE
                    FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(user.Email!!, user.Password!!)?.addOnCompleteListener { task: Task<AuthResult> ->
                        if (task.isSuccessful) {
                            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                            val uid: String? = currentUser?.uid
                            val reference: DatabaseReference? = mDatabase?.child("Users")?.child(uid)
                            //val token: String? = FirebaseInstanceId.getInstance()?.token
                            // val uuid_tipo_user = user.Tipouser as UUID?
                            val rol: Rol? = SQLite.select().from(Rol::class.java).where(Rol_Table.Id.eq(user.Tipouser)).querySingle()
                            val rolName = rol?.Nombre

                            val userMap = HashMap<String?, String?>()
                            userMap.put("Rol", rolName)
                            userMap.put("Nombres", response.body()?.nombre)
                            userMap.put("Apellidos", response.body()?.apellido)
                            userMap.put("Cedula", response.body()?.identification)
                            userMap.put("Correo", response.body()?.email)
                            userMap.put("Celular", response.body()?.phoneNumber)
                            userMap.put("Foto", "")
                            // userMap.put("Token", token!!)

                            reference?.setValue(userMap)?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                    //ENVIAR EMAIL DE CONFIRMACIÓN
                                    sendEmailVerification(currentUser)
                                    //SQLITE, Traer usuario del servicio con todos los datos
                                    /* val query: String = Listas.queryGeneral("Email", user.Email!!)
                                     val callUsuario = apiService?.getUsuarioByCorreo(query)
                                     callUsuario?.enqueue(object : Callback<UsuarioResponse> {
                                         override fun onResponse(call: Call<UsuarioResponse>?, response: Response<UsuarioResponse>?) {
                                             if (response != null && response.code() == 200) {
                                                 //val usuario: Usuario = response.body()?.value!!
                                                 val usuario: List<Usuario>? = response.body()?.value!!
                                                 //TODO Encriptar Contraseña, Save sqlite
                                                 for (u: Usuario in usuario!!) {
                                                     if (u.Email.equals(user.Email)) {
                                                         u.Contrasena = user.Password
                                                         u.DetalleMetodoPagoNombre = SQLite.select().from(DetalleMetodoPago::class.java).where(DetalleMetodoPago_Table.Id.eq(u.DetalleMetodoPagoId?.toLong())).querySingle()?.Nombre
                                                         u.RolNombre = rolName
                                                         u.save()
                                                     }
                                                 }

                                                 postEvent(RegisterEvent.onRegistroExitoso)
                                             } else {
                                                 postEvent(RegisterEvent.onErrorRegistro, "Petición fallida al Servidor")
                                                 Log.e("Error code Get", response?.errorBody()?.string())
                                                 Log.v("url", response?.raw().toString())
                                             }
                                         }

                                         override fun onFailure(call: Call<UsuarioResponse>?, t: Throwable?) {
                                             postEvent(RegisterEvent.onErrorRegistro, "Petición fallida al Servidor")
                                             Log.e("Error Get", t?.message.toString())
                                         }

                                     }) */

                                } else {
                                    postEvent(RegisterEvent.onErrorRegistro, "Petición fallida a Firebase(registro user)")
                                    Log.e("Error Firebase", task.exception.toString())
                                }
                            }

                        } else {
                            try {
                                throw task.exception!!
                            } catch (existEmail: FirebaseAuthUserCollisionException) {
                                postEvent(RegisterEvent.onErrorRegistro, "Correo ya Registrado")
                            } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                                postEvent(RegisterEvent.onErrorRegistro, "Mal formato de correo")
                            } catch (firebaseException: FirebaseException) {
                                postEvent(RegisterEvent.onErrorRegistro, task.exception.toString())
                            }
                        }
                    }

                } else {
                    postEvent(RegisterEvent.onErrorRegistro, "Petición fallida al Servidor(Post response code)")
                    Log.e("Error code Post", response?.errorBody()?.string())
                }
            }

            override fun onFailure(call: Call<UserResponse>?, t: Throwable?) {
                postEvent(RegisterEvent.onErrorRegistro, "Petición fallida al Servidor(Post Register)")
                Log.e("Error Post", t?.message.toString())
            }

        })

    }

    //region Métodos Interfaz
    //Cargar información inicial de Métodos de Pago para el productor
    override fun loadMetodosPago() {
        val call = apiService?.getMetodoPagos()
        call?.enqueue(object : Callback<MetodoPagoResponse> {
            override fun onResponse(call: Call<MetodoPagoResponse>, response: retrofit2.Response<MetodoPagoResponse>?) {
                if (response != null && response.code() == 200) {
                    val metodos_pago = response.body()?.value!!
                    for (item: MetodoPago in metodos_pago) {
                        item.save()
                    }
                    val metodosPagoList = getMetodosPago()
                    postEventMetodoPago(RegisterEvent.onMetodoPagoExitoso, metodosPagoList, null)

                } else {
                    postEvent(RegisterEvent.onLoadInfoError, "No se pudo cargar los métodos de Pago")
                }
            }

            override fun onFailure(call: Call<MetodoPagoResponse>?, t: Throwable?) {
                postEvent(RegisterEvent.onLoadInfoError, "No se pudo cargar los métodos de Pago")
            }

        })

    }

    override fun loadDetalleMetodosPagoByMetodoPagoId(Id: Long?) {
        val call = apiService?.getDetalleMetodoPagos()
        call?.enqueue(object : Callback<DetalleMetodoPagoResponse> {
            override fun onResponse(call: Call<DetalleMetodoPagoResponse>?, response: Response<DetalleMetodoPagoResponse>?) {
                if (response != null && response.code() == 200) {
                    val detalle_metodos_pago = response.body()?.value!!
                    for (item: DetalleMetodoPago in detalle_metodos_pago) {
                        item.save()
                    }
                    val detalleMetodosPagoList = getDetalleMetodosPagoByMetodoPagoId(Id)
                    postEventDetalleMetodoPago(RegisterEvent.onDetalleMetodosPagoExitoso, detalleMetodosPagoList, null)

                } else {
                    postEvent(RegisterEvent.onLoadInfoError, "No se pudieron cargar los bancos")
                }
            }

            override fun onFailure(call: Call<DetalleMetodoPagoResponse>?, t: Throwable?) {

            }
        })
    }


    //cargar los métodos de pago con internet
    override fun getMetodosPago(): MutableList<MetodoPago> {
        val metodosPagoList = SQLite.select().from(MetodoPago::class.java).queryList()
        return metodosPagoList
    }

    //Cargar los detalles por método de pago con internet
    override fun getDetalleMetodosPagoByMetodoPagoId(Id: Long?): MutableList<DetalleMetodoPago> {
        val detalleMetodosPagoList = SQLite.select().from(DetalleMetodoPago::class.java).where(DetalleMetodoPago_Table.MetodoPagoId.eq(Id)).queryList()
        return detalleMetodosPagoList
    }

    //Cargar los métodos de sqlite
    override fun getSqliteMetodosPago() {
        val metodosPagoList = SQLite.select().from(MetodoPago::class.java).queryList()
        if (metodosPagoList.size > 0) {
            postEventMetodoPago(RegisterEvent.onMetodoPagoExitoso, metodosPagoList, null)
        } else {
            postEvent(RegisterEvent.onLoadInfoError, "No se pudo cargar los métodos de Pago")
        }
    }

    //Cargar los detalles de Sqlite
    override fun loadSqliteDetalleMetodosPagoByMetodoPagoId(Id: Long?) {
        val detalleMetodosPagoList = SQLite.select().from(DetalleMetodoPago::class.java).where(DetalleMetodoPago_Table.MetodoPagoId.eq(Id)).queryList()
        if (detalleMetodosPagoList.size > 0) {
            postEventDetalleMetodoPago(RegisterEvent.onDetalleMetodosPagoExitoso, detalleMetodosPagoList, null)
        } else {
            postEvent(RegisterEvent.onLoadInfoError, "No se pudieron cargar los bancos")
        }
    }
    //endregion

    //region Métodos

    //endregion
    private fun sendEmailVerification(currentUser: FirebaseUser?) {
        currentUser?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                postEvent(RegisterEvent.onRegistroExitoso)
            } else {
                postEvent(RegisterEvent.onErrorRegistro, "Petición fallida a Firebase(Email Verificación)")
            }

        }
    }


    //region Eventos
    private fun postEvent(type: Int) {
        postEvent(type, null)
    }

    private fun postEvent(type: Int, errorMessage: String?) {
        val registerEvent = RegisterEvent(type, null, errorMessage)
        registerEvent.eventType = type
        registerEvent.mensajeError = errorMessage
        eventBus?.post(registerEvent)
    }

    private fun postEventMetodoPago(type: Int, metodoPago: MutableList<MetodoPago>?, errorMessage: String?) {
        var metodoPagoMutableList = metodoPago as MutableList<Object>
        var metodoPagoEvent = RegisterEvent(type, metodoPagoMutableList, errorMessage)
        metodoPagoEvent.eventType = type
        metodoPagoEvent.mutableList = metodoPagoMutableList
        metodoPagoEvent.mensajeError = errorMessage
        eventBus?.post(metodoPagoEvent)

    }

    private fun postEventDetalleMetodoPago(type: Int, detalleMetodoPago: MutableList<DetalleMetodoPago>?, errorMessage: String?) {
        var detalleMetodoPagoMutableList = detalleMetodoPago as MutableList<Object>
        var detalleMetodoPagoEvent = RegisterEvent(type, detalleMetodoPagoMutableList, errorMessage)
        detalleMetodoPagoEvent.eventType = type
        detalleMetodoPagoEvent.mutableList = detalleMetodoPagoMutableList
        detalleMetodoPagoEvent.mensajeError = errorMessage
        eventBus?.post(detalleMetodoPagoEvent)

    }
    //endregion

}