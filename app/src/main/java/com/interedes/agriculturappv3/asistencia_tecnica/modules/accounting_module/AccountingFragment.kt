package com.interedes.agriculturappv3.asistencia_tecnica.modules.accounting_module


import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.adapters.SingleAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.accounting_module.ventas.Ventas_Fragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.ui.MainMenuFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.Resources_Menu
import com.interedes.agriculturappv3.services.listas.Listas
import kotlinx.android.synthetic.main.activity_menu_main.*
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
        setupInitDesign()
        ivBackButton?.setOnClickListener(this)
        loadItems()


    }


    private fun setupInitDesign() {
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_module_contable)
        (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.green));
        var iconMenu = (activity as MenuMainActivity).menuItemGlobal
        iconMenu?.isVisible = true

        var iconc = iconMenu?.setIcon(ContextCompat.getDrawable((activity as MenuMainActivity), R.drawable.ic_contabilidad))
        var icon = iconc?.icon?.mutate()
        icon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = (activity as MenuMainActivity).getWindow()
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.statusBarColor = ContextCompat.getColor((activity as MenuMainActivity), R.color.green)
        }
    }


    private fun loadItems() {
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista = Listas.listaModuloContableProductor()
        val adapter = SingleAdapter(lista, Resources_Menu.MENU_MODULE_ACCOUNTANT, activity) { position ->

            if (lista[position].Identificador.equals("ventas")) {
                //startActivity(Intent(activity, MapsActivity::class.java))
                (activity as MenuMainActivity).replaceFragment(Ventas_Fragment())
            } else if (lista[position].Identificador.equals("ofertas")) {

            } else if (lista[position].Identificador.equals("ventas_realizadas")) {

            } else if (lista[position].Identificador.equals("clientes")) {

            } else if (lista[position].Identificador.equals("preguntas")) {

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
                (activity as MenuMainActivity).onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.grey_luiyi))
    }


    //endregion
}