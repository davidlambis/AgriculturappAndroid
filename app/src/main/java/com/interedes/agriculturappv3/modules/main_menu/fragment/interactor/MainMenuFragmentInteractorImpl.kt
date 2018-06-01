package com.interedes.agriculturappv3.modules.main_menu.fragment.interactor

import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.main_menu.fragment.repository.MainMenuFragmentRepository
import com.interedes.agriculturappv3.modules.main_menu.fragment.repository.MainMenuFragmentRepositoryImpl

class MainMenuFragmentInteractorImpl : MainMenuFragmentInteractor {

    var repository: MainMenuFragmentRepository? = null

    init {
        repository = MainMenuFragmentRepositoryImpl()
    }





}