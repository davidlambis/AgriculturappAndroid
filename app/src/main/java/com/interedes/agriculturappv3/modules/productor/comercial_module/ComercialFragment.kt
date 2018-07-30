package com.interedes.agriculturappv3.modules.productor.comercial_module


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
import com.interedes.agriculturappv3.modules.adapters.SingleAdapter
import com.interedes.agriculturappv3.modules.productor.comercial_module.clientes.ClientesFragment
import com.interedes.agriculturappv3.modules.ofertas.OfertasFragment
import com.interedes.agriculturappv3.modules.productor.comercial_module.productos.ProductosFragment
import com.interedes.agriculturappv3.modules.productor.comercial_module.ventas.VentasFragment
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.resources.Menu_Resources
import com.interedes.agriculturappv3.services.resources.ListasResources
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.fragment_general.*


class ComercialFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInitDesign()
        ivBackButton?.setOnClickListener(this)
        loadItems()


    }


    private fun setupInitDesign() {
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_module_comercial)
        (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.blue));
        var iconMenu = (activity as MenuMainActivity).menuItemGlobal
        iconMenu?.isVisible = true

        var iconc = iconMenu?.setIcon(ContextCompat.getDrawable((activity as MenuMainActivity), R.drawable.ic_icon_comercial))
        var icon = iconc?.icon?.mutate()
        icon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = (activity as MenuMainActivity).getWindow()
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.statusBarColor = ContextCompat.getColor((activity as MenuMainActivity), R.color.blue)
            (activity as MenuMainActivity).app_bar_main.elevation = 0f
            (activity as MenuMainActivity).app_bar_main.stateListAnimator = null

        } else {
            (activity as MenuMainActivity).app_bar_main.targetElevation = 0f
        }

    }


    private fun loadItems() {
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista = ListasResources.listaModuloComercialProductor()
        val adapter = SingleAdapter(lista, Menu_Resources.MENU_MODULE_COMERCIAL, activity) { position ->
            if (lista[position].Identificador.equals("mis_productos")) {
                (activity as MenuMainActivity).replaceFragment(ProductosFragment())
            } else if (lista[position].Identificador.equals("ofertas")) {
                (activity as MenuMainActivity).replaceFragment(OfertasFragment())
            } else if (lista[position].Identificador.equals("ventas_realizadas")) {
                (activity as MenuMainActivity).replaceFragment(VentasFragment())
            } else if (lista[position].Identificador.equals("clientes")) {
                (activity as MenuMainActivity).replaceFragment(ClientesFragment())
            } else if (lista[position].Identificador.equals("preguntas")) {

            }
        }
        recyclerView?.adapter = adapter
        /*else if ((activity as MenuMainActivity).getLastUserLogued()?.RolNombre.equals("Comprador")) {
            val lista = Listas.listaModuloComercialComprador()
            val adapter = SingleAdapter(lista, Menu_Resources.MENU_MODULE_COMERCIAL, activity) { position ->
                if (lista[position].Identificador.equals("productos")) {
                    (activity as MenuMainActivity).replaceFragment(ProductosCompradorFragment())
                }
            }
            recyclerView?.adapter = adapter
        }*/
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
