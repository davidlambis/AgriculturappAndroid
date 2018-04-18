package com.interedes.agriculturappv3.productor.modules.main_menu.fragment.repository

import com.interedes.agriculturappv3.productor.models.usuario.Usuario


interface MainMenuFragmentRepository {
    //online
    fun logOut(usuario: Usuario?)

    //offline
    fun offlineLogOut(usuario: Usuario?)

    fun getListasIniciales()
}