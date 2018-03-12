package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.events.RegisterEvent
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPago_Table
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.detalle_metodo_pago.DetalleMetodoPagoResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPagoResponse
//import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterUserRepositoryImpl : RegisterUserRepository {

    val mDatabase: DatabaseReference?
    var eventBus: EventBus? = null


    init {
        eventBus = GreenRobotEventBus()
        mDatabase = FirebaseDatabase.getInstance().reference
    }


    //Registro de usuario en backend, firebase y sqlite
    override fun registerUsuario(usuario: Usuario) {

        //TODO Primero Registrar en el Backend y cuando retorne un success en el backend, registrar en Firebase, SQLITE y posteriormente enviar el evento al presenter.
        //FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.correo, usuario.contrasena).addOnCompleteListener(OnCompleteListener<AuthResult>())
        FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.Email!!, usuario.Contrasena!!)?.addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                val uid: String? = currentUser?.uid
                val reference: DatabaseReference? = mDatabase?.child("Users")?.child(uid)
                val token: String? = FirebaseInstanceId.getInstance()?.token
                val userMap = HashMap<String, String>()
                userMap.put("Rol", usuario.RolNombre!!)
                userMap.put("Nombres", usuario.Nombre!!)
                userMap.put("Apellidos", usuario.Apellidos!!)
                userMap.put("Cedula", usuario.Identificacion!!)
                userMap.put("Correo", usuario.Email!!)
                userMap.put("Celular", usuario.Nro_movil!!)
                userMap.put("Imagen", usuario.Fotopefil!!)
                userMap.put("Token", token!!)

                reference?.setValue(userMap)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        postEvent(RegisterEvent.onRegistroExitoso)

                    } else {
                        postEvent(RegisterEvent.onErrorRegistro, task.exception.toString())
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

    }

    //Cargar información inicial de Métodos de Pago para el productor
    override fun loadMetodosPago() {
        val apiService = ApiInterface.create()
        val call = apiService.getMetodoPagos()
        call.enqueue(object : Callback<MetodoPagoResponse> {
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
        val apiService = ApiInterface.create()
        val call = apiService.getDetalleMetodoPagos()
        call.enqueue(object : Callback<DetalleMetodoPagoResponse> {
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


}