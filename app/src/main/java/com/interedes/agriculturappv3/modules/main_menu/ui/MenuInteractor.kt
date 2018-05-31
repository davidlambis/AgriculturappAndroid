package com.interedes.agriculturappv3.modules.main_menu.ui

import com.interedes.agriculturappv3.modules.models.usuario.Usuario

class MenuInteractor:MainViewMenu.Interactor {


    var repository: MainViewMenu.Repository? = null

    init {
        this.repository = MenuRepository()
    }


    override fun syncData() {
       repository?.syncData()
    }

    override fun syncQuantityData() {
        repository?.syncQuantityData()
    }

    override fun getListasIniciales() {
        repository?.getListasIniciales()
    }

    override fun getLastUserLogued(): Usuario? {
       return  repository?.getLastUserLogued()
    }

    override fun getListSyncEnfermedadesAndTratamiento() {
       repository?.getListSyncEnfermedadesAndTratamiento()
    }

}