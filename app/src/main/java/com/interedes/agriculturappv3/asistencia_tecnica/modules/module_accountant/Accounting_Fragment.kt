package com.interedes.agriculturappv3.asistencia_tecnica.modules.module_accountant


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.interedes.agriculturappv3.R


/**
 * A simple [Fragment] subclass.
 */
class Accounting_Fragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val textView = TextView(activity)
        textView.setText(R.string.hello_blank_fragment)
        return textView
    }

}// Required empty public constructor
