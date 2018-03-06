package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario
import com.interedes.agriculturappv3.domain.FirebaseHelper


class RegisterUserRepositoryImpl : RegisterUserRepository {

    val firebaseHelper: FirebaseHelper? = null


    override fun registerUsuario(usuario: Usuario) {
        //TODO Primero debe hacer el registro en el Backend para hacer el de firebase
        //FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.correo, usuario.contrasena).addOnCompleteListener(OnCompleteListener<AuthResult>())
        FirebaseAuth.getInstance()?.createUserWithEmailAndPassword(usuario.correo, usuario.contrasena)?.addOnCompleteListener { task: Task<AuthResult> ->
            if (task.isSuccessful) {

            } else {

            }
        }

    }


}