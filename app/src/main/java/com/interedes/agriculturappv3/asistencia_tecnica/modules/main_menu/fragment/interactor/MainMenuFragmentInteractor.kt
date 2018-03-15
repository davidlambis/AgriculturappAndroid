package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario

interface MainMenuFragmentInteractor {

    //Online
    fun logOut(usuario: Usuario?)

    //Offline
    fun offlineLogOut(usuario: Usuario?)
}