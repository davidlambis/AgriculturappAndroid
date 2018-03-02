package com.interedes.agriculturappv3.asistencia_tecnica.services

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

/**
 * Created by EnuarMunoz on 2/03/18.
 */
class AdapterFragmet {

    constructor(fragment: Fragment, ft: FragmentManager, container: Int)  {
/*
        ft?.beginTransaction().addToBackStack(null)
        //ft?.addToBackStack(fragment::class.java!!.getName())
        ft?.replace(container, fragment, Fragment::class.java!!.getName())
        ft?.commit()
        */

        val fragmentTransaction = ft.beginTransaction()
        fragmentTransaction.replace(container, fragment, fragment::class.java!!.getName());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();

        //ft?.beginTransaction()
        //          .replace(container, fragment).addToBackStack(null)
        //        .commit()



    }
}