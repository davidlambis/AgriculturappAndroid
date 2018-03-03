package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.adapters.SingleAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.services.listas.Listas
import kotlinx.android.synthetic.main.content_recyclerview.*


/**
 * A simple [Fragment] subclass.
 */
class MainMenuFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_fragment_main_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadItems()
    }

    private fun loadItems() {
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista = Listas.listaMenuProductor()
        val adapter = SingleAdapter(lista) { position ->

        }
        recyclerView?.adapter = adapter

    }


}// Required empty public constructor
