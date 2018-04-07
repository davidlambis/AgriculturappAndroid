package com.interedes.agriculturappv3.productor.modules.main_menu.fragment.interactor

import com.interedes.agriculturappv3.productor.models.usuario.Usuario

interface MainMenuFragmentInteractor {

    //Online
    fun logOut(usuario: Usuario?)

    //Offline
    fun offlineLogOut(usuario: Usuario?)
}