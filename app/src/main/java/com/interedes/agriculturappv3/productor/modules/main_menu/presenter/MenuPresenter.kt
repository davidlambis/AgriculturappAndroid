package com.interedes.agriculturappv3.productor.modules.main_menu.presenter

import android.content.Context

/**
 * Created by EnuarMunoz on 8/03/18.
 */
interface MenuPresenter {

    fun onCreate()
    fun onDestroy(context:Context)
    fun onResume(context: Context)


    //Conection
    fun checkConnection(): Boolean?

}
