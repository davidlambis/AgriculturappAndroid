package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository.RegisterUserRepository
import com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_user.repository.RegisterUserRepositoryImpl
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.User

class RegisterUserInteractorImpl : RegisterUserInteractor {

    var registerUserRepository: RegisterUserRepository? = null

    init {
        registerUserRepository = RegisterUserRepositoryImpl()
    }

    override fun registerUsuario(user: User) {
        registerUserRepository?.registerUsuario(user)
    }

    override fun loadMetodosPago() {
        registerUserRepository?.loadMetodosPago()
    }

    override fun loadDetalleMetodosPagoByMetodoPagoId(Id : Long?) {
        registerUserRepository?.loadDetalleMetodosPagoByMetodoPagoId(Id)
    }

    override fun getSqliteMetodosPago() {
        registerUserRepository?.getSqliteMetodosPago()
    }

    override fun loadSqliteDetalleMetodosPagoByMetodoPagoId(Id: Long?) {
        registerUserRepository?.loadSqliteDetalleMetodosPagoByMetodoPagoId(Id)
    }

/*
    override fun getMetodosPago() {
        registerUserRepository?.getMetodosPago()
    }*/

}