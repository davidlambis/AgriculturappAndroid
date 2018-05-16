package com.interedes.agriculturappv3.modules.main_menu.fragment.repository

import com.interedes.agriculturappv3.modules.models.usuario.Usuario


interface MainMenuFragmentRepository {
    //online
    fun logOut(usuario: Usuario?)

    //offline
    fun offlineLogOut(usuario: Usuario?)

    fun getListasIniciales()
}