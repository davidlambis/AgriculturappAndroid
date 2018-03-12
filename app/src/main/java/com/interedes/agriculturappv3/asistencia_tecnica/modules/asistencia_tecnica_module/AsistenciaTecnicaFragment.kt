package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.activities.MapsActivity
import com.interedes.agriculturappv3.asistencia_tecnica.adapters.SingleAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui.LoteFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.MainMenuFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.listas.Listas
import kotlinx.android.synthetic.main.fragment_general.*


/**
 * A simple [Fragment] subclass.
 */
class AsistenciaTecnicaFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivBackButton?.setOnClickListener(this)
        loadItems()
    }

    private fun loadItems() {
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista = Listas.listaAsistenciaTecnicaProductor()
        val adapter = SingleAdapter(lista) { position ->

            if (lista[position].Identificador.equals("mis_lotes")) {
                (activity as MenuMainActivity).replaceFragment(LoteFragment())
            }
            if (lista[position].Identificador.equals("mis_unidades_productivas")){
                startActivity(Intent(activity, MapsActivity::class.java))
            }

        }
        recyclerView?.adapter = adapter
    }

    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                /*val mainMenuFragment = MainMenuFragment()
                (activity as MenuMainActivity).replaceFragment(mainMenuFragment)*/
                //fragmentManager?.popBackStackImmediate()
                (activity as MenuMainActivity).replaceCleanFragment(MainMenuFragment())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.grey_luiyi))
    }





    //endregion
}
