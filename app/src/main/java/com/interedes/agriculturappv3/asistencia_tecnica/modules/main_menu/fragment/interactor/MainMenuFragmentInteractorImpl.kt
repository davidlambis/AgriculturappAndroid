package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.interactor

import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.repository.MainMenuFragmentRepository
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.repository.MainMenuFragmentRepositoryImpl

class MainMenuFragmentInteractorImpl : MainMenuFragmentInteractor {

    var repository: MainMenuFragmentRepository? = null

    init {
        repository = MainMenuFragmentRepositoryImpl()
    }

    //Online
    override fun logOut(usuario: Usuario?) {
        repository?.logOut(usuario)
    }

    override fun offlineLogOut(usuario: Usuario?) {
        repository?.offlineLogOut(usuario)
    }

}