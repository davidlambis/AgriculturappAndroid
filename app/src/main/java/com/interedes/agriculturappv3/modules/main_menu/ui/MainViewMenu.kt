package com.interedes.agriculturappv3.modules.main_menu.ui

/**
 * Created by EnuarMunoz on 8/03/18.
 */
interface MainViewMenu {
    fun onConnectivity()
    fun offConnectivity()

    fun onMessageOk(colorPrimary: Int, message: String?)
    fun onMessageError(colorPrimary: Int, message: String?)
}