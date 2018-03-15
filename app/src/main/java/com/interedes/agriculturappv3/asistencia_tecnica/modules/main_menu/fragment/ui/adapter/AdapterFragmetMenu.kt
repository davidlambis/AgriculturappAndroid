package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction


class AdapterFragmetMenu/*
        ft?.beginTransaction().addToBackStack(null)
        //ft?.addToBackStack(fragment::class.java!!.getName())
        ft?.replace(container, fragment, Fragment::class.java!!.getName())
        ft?.commit()
        */

//ft?.beginTransaction()
//          .replace(container, fragment).addToBackStack(null)
//        .commit()
(fragment: Fragment, ft: FragmentManager?, container: Int) {init {
    val fragmentTransaction = ft?.beginTransaction()
    fragmentTransaction?.replace(container, fragment, fragment::class.java.getName())
    fragmentTransaction?.addToBackStack(fragment::class.java.getName())
    fragmentTransaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
    fragmentTransaction?.commit()
}

}