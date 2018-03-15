package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.presenter

import android.content.Context
import com.interedes.agriculturappv3.asistencia_tecnica.models.usuario.Usuario
import com.interedes.agriculturappv3.events.RequestEvent

interface MainMenuFragmentPresenter {

    fun onCreate()
    fun onDestroy()
    fun onResume(context: Context)
    fun onPause(context: Context)
    fun onEventMainThread(event: RequestEvent?)
    fun logOut(usuario: Usuario?)
}