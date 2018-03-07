package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.domain.FirebaseHelper
import com.interedes.agriculturappv3.events.RegisterEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus


class RegisterUserRepositoryImpl : RegisterUserRepository {

    val firebaseHelper: FirebaseHelper? = null
    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
    }

    override fun registerUsuario(usuario: Usuario) {
        //TODO Primero debe hacer el registro en el Backend para hacer el de firebase
        //FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.correo, usuario.contrasena).addOnCompleteListener(OnCompleteListener<AuthResult>())
        FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.correo, usuario.contrasena)?.addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {
                postEvent(RegisterEvent.onRegistroExitoso)
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
                /*
                //postEvent(RegisterEvent.onErrorRegistro, task.exception.toString())
                try {
                    throw  task.exception!!
                    //postEvent(RegisterEvent.onErrorRegistro, task.exception.toString())
                } catch (existEmail: FirebaseAuthUserCollisionException) {
                    postEvent(RegisterEvent.onErrorRegistro, "Correo ya Registrado")
                } /*catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                    postEvent(RegisterEvent.onErrorRegistro, "Mal Formato de Correo")
                } catch (weakPassword: FirebaseAuthWeakPasswordException) {
                    postEvent(RegisterEvent.onErrorRegistro, "Contraseña muy Corta")
                } */
                //postEvent(RegisterEvent.onErrorRegistro, task.exception.toString())*/
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