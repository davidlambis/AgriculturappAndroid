package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.repository

import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario


interface MainMenuFragmentRepository {
    //online
    fun logOut(usuario: Usuario?)

    //offline
    fun offlineLogOut(usuario: Usuario?)
}