package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import android.content.Intent
import android.support.v4.content.ContextCompat.startActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.domain.FirebaseHelper
import com.interedes.agriculturappv3.events.RegisterEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus


class RegisterUserRepositoryImpl : RegisterUserRepository {

    val mDatabase: DatabaseReference?
    var eventBus: EventBus? = null


    init {
        eventBus = GreenRobotEventBus()
        mDatabase = FirebaseDatabase.getInstance().reference
    }

    override fun registerUsuario(usuario: Usuario) {
        //FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.correo, usuario.contrasena).addOnCompleteListener(OnCompleteListener<AuthResult>())
        FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.correo, usuario.contrasena)?.addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                val uid: String? = currentUser?.uid
                val reference: DatabaseReference? = mDatabase?.child("Users")?.child(uid)
                val token: String? = FirebaseInstanceId.getInstance()?.token
                val userMap = HashMap<String, String>()
                userMap.put("Rol", usuario.rol!!)
                userMap.put("Nombres", usuario.nombres)
                userMap.put("Apellidos", usuario.apellidos)
                userMap.put("Cedula", usuario.cedula)
                userMap.put("Correo", usuario.correo)
                userMap.put("Celular", usuario.celular)
                userMap.put("Imagen", "default")
                userMap.put("Token", token!!)

                reference?.setValue(userMap)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //TODO Registrar en el Backend y cuando retorne un success en el backend, registrar en SqLite y posteriormente enviar el evento al presenter.
                        postEvent(RegisterEvent.onRegistroExitoso)
                    } else {
                        postEvent(RegisterEvent.onErrorRegistro, task.exception.toString())
                    }
                }

                /*mDatabase.setValue(userMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        mRegProgress.dismiss()

                        val mainIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(mainIntent)
                        finish()

                    }
                } */


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

    private fun postEvent(type: Int) {
        postEvent(type, null)
    }

    private fun postEvent(type: Int, errorMessage: String?) {
        val registerEvent = RegisterEvent(type, errorMessage)
        registerEvent.eventType = type
        registerEvent.mensajeError = errorMessage
        eventBus?.post(registerEvent)
    }


}