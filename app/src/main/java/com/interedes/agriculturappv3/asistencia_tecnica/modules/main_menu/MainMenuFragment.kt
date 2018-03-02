package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R


/**
 * A simple [Fragment] subclass.
 */
class MainMenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_fragment_main_menu, container, false)
    }

}// Required empty public constructor
