package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.ui

import android.content.Intent
import android.os.Bundle


interface MainMenuFragmentView {

    fun loadItems()
    //Events
    fun onEventBroadcastReceiver(extras: Bundle, intent: Intent)

    fun getConnectivityState(): Boolean?

    fun navigateToLogin()
    fun errorLogOut(error: String?)
    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)
}