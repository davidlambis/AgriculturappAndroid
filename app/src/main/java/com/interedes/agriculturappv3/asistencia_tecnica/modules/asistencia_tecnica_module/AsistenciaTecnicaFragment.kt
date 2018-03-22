package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.adapters.SingleAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Lote.ui.Lote_Fragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.ProduccionFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.UnidadProductiva.UnidadProductiva_Fragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.Cultivo_Fragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu.fragment.ui.MainMenuFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.Resources_Menu
import com.interedes.agriculturappv3.services.listas.Listas
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.fragment_general.*
import android.view.WindowManager
import android.R.attr.data
import android.util.TypedValue






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
        setupInit()
        ivBackButton?.setOnClickListener(this)
        loadItems()


    }


    private fun setupInit() {
        (activity as MenuMainActivity).toolbar.title=getString(R.string.title_module_asistencia_tecnica)
        (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.purple) );
        var iconMenu=(activity as MenuMainActivity).menuItemGlobal
        iconMenu?.isVisible=true

        var iconc =iconMenu?.setIcon(ContextCompat.getDrawable( (activity as MenuMainActivity), R.drawable.ic_action_menu_asistencia_tecnica))
        var icon=iconc?.icon?.mutate()
        icon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = (activity as MenuMainActivity).getWindow()
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.statusBarColor = ContextCompat.getColor((activity as MenuMainActivity), R.color.purple)
        }
    }

    fun getActionBarHeight(): Int {
        var actionBarHeight = 0
        val tv = TypedValue()
        if (activity?.getTheme()!!.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        return actionBarHeight
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }



    private fun loadItems() {
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista = Listas.listaAsistenciaTecnicaProductor()
        val adapter = SingleAdapter(lista,Resources_Menu.MENU_MODULE_ASISTENCIA_TECNICA,activity) { position ->

            if (lista[position].Identificador.equals("mis_lotes")) {
                (activity as MenuMainActivity).replaceFragment(Lote_Fragment())
            } else if (lista[position].Identificador.equals("mis_unidades_productivas")) {
                //startActivity(Intent(activity, MapsActivity::class.java))
                (activity as MenuMainActivity).replaceFragment(UnidadProductiva_Fragment())
            } else if (lista[position].Identificador.equals("mis_cultivos")) {
                (activity as MenuMainActivity).replaceFragment(Cultivo_Fragment())
            }
            else if (lista[position].Identificador.equals("produccion")) {
                (activity as MenuMainActivity).replaceFragment(ProduccionFragment())
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
