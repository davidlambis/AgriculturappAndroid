package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository.RegisterUserRepository
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository.RegisterUserRepositoryImpl
import com.interedes.agriculturappv3.asistencia_tecnica.models.Usuario

class RegisterUserInteractorImpl : RegisterUserInteractor {

    var registerUserRepository: RegisterUserRepository? = null

    init {
        registerUserRepository = RegisterUserRepositoryImpl()
    }

    override fun registerUsuario(usuario: Usuario) {
        registerUserRepository?.registerUsuario(usuario)
    }

    override fun loadInfo() {
        registerUserRepository?.loadInfo()
    }

/*
    override fun getMetodosPago() {
        registerUserRepository?.getMetodosPago()
    }*/

}