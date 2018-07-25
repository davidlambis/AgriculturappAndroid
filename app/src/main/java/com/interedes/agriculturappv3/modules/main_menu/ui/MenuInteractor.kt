package com.interedes.agriculturappv3.modules.main_menu.ui

import android.content.Context
import com.interedes.agriculturappv3.modules.models.usuario.Usuario

class MenuInteractor:MainViewMenu.Interactor {



    var repository: MainViewMenu.Repository? = null

    init {
        this.repository = MenuRepository()
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

    /*override fun getListSyncEnfermedadesAndTratamiento() {
       repository?.getListSyncEnfermedadesAndTratamiento()
    }*/

    override fun makeUserOnline(checkConection:Boolean,context: Context) {
      repository?.makeUserOnline(checkConection,context)
    }

    override fun makeUserOffline(checkConection:Boolean,context: Context) {
        repository?.makeUserOffline(checkConection,context)
    }

    //Online
    override fun logOut(usuario: Usuario?) {
        repository?.logOut(usuario)
    }

    override fun checkListPlagas(): Long {
      return  repository?.checkListPlagas()!!
    }


    //Navigation to chat
    override fun navigateChatOnline(checkConection: Boolean, usuario: Usuario) {
       repository?.navigateChatOnline(checkConection,usuario)
    }

    override fun navigateChatOnlineNotification(uiFirebase: String) {
        repository?.navigateChatOnlineNotification(uiFirebase)
    }

    //Notifications
    override fun getCountNotifications(): Int {
        return  repository?.getCountNotifications()!!
    }
}