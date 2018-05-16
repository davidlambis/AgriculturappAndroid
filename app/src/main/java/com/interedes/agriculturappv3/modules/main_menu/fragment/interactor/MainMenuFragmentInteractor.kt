package com.interedes.agriculturappv3.modules.main_menu.fragment.interactor

import com.interedes.agriculturappv3.modules.models.usuario.Usuario

interface MainMenuFragmentInteractor {

    //Online
    fun logOut(usuario: Usuario?)

    //Offline
    fun offlineLogOut(usuario: Usuario?)

    fun getListasIniciales()
}