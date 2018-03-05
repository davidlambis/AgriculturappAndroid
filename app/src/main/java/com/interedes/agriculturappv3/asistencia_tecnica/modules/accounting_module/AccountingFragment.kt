package com.interedes.agriculturappv3.asistencia_tecnica.modules.accounting_module


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.adapters.SingleAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.MainMenuFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.asistencia_tecnica.services.listas.Listas
import kotlinx.android.synthetic.main.fragment_general.*


/**
 * A simple [Fragment] subclass.
 */
class AccountingFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivBackButton?.setOnClickListener(this)
        //loadItems()
    }

    private fun loadItems() {
        /*recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista = Listas.listaMenuProductor()
        val adapter = SingleAdapter(lista) { position ->

        }
        recyclerView?.adapter = adapter*/
    }

    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton?.setColorFilter(resources.getColor(R.color.colorPrimary))
                (activity as MenuMainActivity).replaceCleanFragment(MainMenuFragment())
                //fragmentManager?.popBackStackImmediate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ivBackButton?.setColorFilter(resources.getColor(R.color.grey_luiyi))
    }


    //endregion
}
