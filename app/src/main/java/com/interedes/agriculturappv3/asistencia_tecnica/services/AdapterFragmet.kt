package com.interedes.agriculturappv3.asistencia_tecnica.services

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import com.interedes.agriculturappv3.R

/**
 * Created by EnuarMunoz on 2/03/18.
 */
class AdapterFragmet {

    constructor(fragment: Fragment, ft: FragmentTransaction?)  {

        ft?.addToBackStack(null)
        //ft?.addToBackStack(fragment::class.java!!.getName())
        ft?.replace(R.id.container, fragment, Fragment::class.java!!.getName())
        ft?.commit()

    }
}