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

    override fun syncQuantityData(automatic:Boolean) {
        repository?.syncQuantityData(automatic)
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

    override fun makeUserOnline(checkConection:Boolean) {
      repository?.makeUserOnline(checkConection)
    }

    override fun makeUserOffline(checkConection:Boolean) {
        repository?.makeUserOffline(checkConection)
    }

    //Online
    override fun logOut(usuario: Usuario?) {
        repository?.logOut(usuario)
    }

}