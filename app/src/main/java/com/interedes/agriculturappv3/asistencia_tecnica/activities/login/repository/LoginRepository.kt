package com.interedes.agriculturappv3.asistencia_tecnica.activities.login.repository

interface LoginRepository {

    fun ingresar(email: String, password: String)
}