package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.events.RegisterEvent
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPagoResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago_Table
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.RolResponse
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol_Table
//import com.interedes.agriculturappv3.asistencia_tecnica.models.metodopago.MetodoPago
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.api.ApiInterface
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.language.Select
import retrofit2.Call
import retrofit2.Callback


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
    override fun loadInfo() {
        val apiService = ApiInterface.create()
        val call = apiService.getMetodoPagos()
        call.enqueue(object : Callback<MetodoPagoResponse> {
            override fun onResponse(call: Call<MetodoPagoResponse>, response: retrofit2.Response<MetodoPagoResponse>?) {
                if (response != null && response.code() == 200) {
                    var metodos_pago = response.body()?.value!!
                    for (item: MetodoPago in metodos_pago) {
                        item.save()
                    }
                    val metodosPagoList = getMetodosPago()
                    postEventMetodoPago(RegisterEvent.onMetodosPagoExitoso, metodosPagoList, null)

                }
            }

            override fun onFailure(call: Call<MetodoPagoResponse>?, t: Throwable?) {
                postEventMetodoPago(RegisterEvent.onMetodosPagoError, null, "No se pudo cargar los métodos de Pago")
            }

        })

    }


    override fun getMetodosPago(): MutableList<MetodoPago> {
        val metodosPagoList = SQLite.select().from(MetodoPago::class.java).queryList()
        return metodosPagoList
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


}