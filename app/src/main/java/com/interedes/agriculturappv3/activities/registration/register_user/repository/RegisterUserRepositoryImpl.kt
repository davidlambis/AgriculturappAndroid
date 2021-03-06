package com.interedes.agriculturappv3.activities.registration.register_user.repository

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.interedes.agriculturappv3.activities.registration.register_user.events.RegisterEvent
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.modules.models.detalle_metodo_pago.DetalleMetodoPago_Table
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.modules.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.modules.models.rol.Rol
import com.interedes.agriculturappv3.modules.models.rol.Rol_Table
import com.interedes.agriculturappv3.modules.models.usuario.User
import com.interedes.agriculturappv3.modules.models.usuario.UserResponse
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ServerValue
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.services.resources.Status_Chat


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
                    postEvent(RegisterEvent.onRegistroExitoso)
                } else if (response?.code() == 400) {
                        postEvent(RegisterEvent.onErrorRegistro, "Correo ya Registrado")
                    Log.e("correo_registrado", response.errorBody()?.string())
                } else {
                    postEvent(RegisterEvent.onErrorRegistro, "No se puede registrar, compruebe su conexión")
                    Log.e("error", response?.message().toString())
                }
            }
            override fun onFailure(call: Call<UserResponse>?, t: Throwable?) {
                postEvent(RegisterEvent.onErrorRegistro, t?.message.toString())
                Log.e("error_connection", "No se puede registrar, compruebe su conexión")
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