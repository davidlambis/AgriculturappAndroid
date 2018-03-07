package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.MapsActivity
import com.interedes.agriculturappv3.asistencia_tecnica.adapters.SingleAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.accounting_module.AccountingFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.asistencia_tecnica.services.listas.Listas
import kotlinx.android.synthetic.main.fragment_main_menu.*


/**
 * A simple [Fragment] subclass.
 */
class MainMenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadItems()
    }

    private fun loadItems() {
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista = Listas.listaMenuProductor()
        val adapter = SingleAdapter(lista) { position ->
            if (lista[position].Identificador.equals("asistencia_tecnica")) {
                (activity as MenuMainActivity).replaceFragment(AsistenciaTecnicaFragment())
            }
            /*else if (lista[position].Identificador.equals("contabilidad")) {
                (activity as MenuMainActivity).replaceFragment(AccountingFragment())
            }*/
        }
        recyclerView?.adapter = adapter

    }


}// Required empty public constructor
