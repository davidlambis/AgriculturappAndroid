package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas


import android.content.DialogInterface
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Insumo
import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.InsumosFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.PlagasAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.TipoProductosAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.listas.Listas
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_list_plagas.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.content_recyclerview.view.*
import kotlinx.android.synthetic.main.dialog_list_general.view.*
import kotlinx.android.synthetic.main.fragment_plaga.*


class PlagaFragment : Fragment(), IPlaga.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    //adapter
    var adapter: PlagasAdapter? = null

    //Presenter
    var presenter: IPlaga.Presenter? = null

    //List Plagas
    var plagasList: ArrayList<TipoEnfermedad>? = ArrayList<TipoEnfermedad>()

    //Dialog Tipo Productos
    var dialogProducto: MaterialDialog? = null
    var viewDialogTipoProductos: View? = null;

    companion object {
        var instance: PlagaFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        presenter = PlagaPresenter(this)
        presenter?.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plaga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setupInjection()
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_plagas)
        swipeRefreshLayout?.setOnRefreshListener(this)
        ivBackButton?.setOnClickListener(this)
        plagas_search_edit_frame?.setOnClickListener(this)
        plagas_search_view.clearFocus()
        plagas_search_view.setIconifiedByDefault(false)
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = PlagasAdapter(plagasList!!)
        recyclerView?.adapter = adapter
    }

    private fun setupInjection() {
        getPlagasByTipoProducto(1)
    }


    //region Métodos Interfaz
    override fun getPlagasByTipoProducto(tipoProductoId: Long?) {
        presenter?.getPlagasByTipoProducto(tipoProductoId)
    }

    override fun setListPlagas(list_plagas: List<TipoEnfermedad>) {
        adapter?.clear()
        plagasList?.clear()
        adapter?.setItems(list_plagas)
        hideRefresh()
        setResults(list_plagas.size)
    }

    override fun verInsumos(tipoEnfermedad: TipoEnfermedad) {
        val bundle = Bundle()
        bundle.putLong("tipoEnfermedadId", tipoEnfermedad.Id!!)
        bundle.putString("nombreTipoEnfermedad", tipoEnfermedad.Nombre)
        val insumosFragment: InsumosFragment
        insumosFragment = InsumosFragment()
        insumosFragment.arguments = bundle
        (activity as MenuMainActivity).replaceFragment(insumosFragment)
    }

    override fun showRefresh() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun hideDialog(tipoProducto: TipoProducto) {
        dialogProducto?.dismiss()
        plagas_search_view.setQuery(tipoProducto.Nombre, false)
    }
    //endregion

    //region Métodos
    override fun onRefresh() {
        showRefresh()
        getPlagasByTipoProducto(1)
    }

    override fun setResults(plagas: Int) {
        val results = String.format(getString(R.string.results_global_search),
                plagas)
        txtResults.setText(results)
    }


    fun showAlertDialogTipoProduccion() {
        val inflater = this.layoutInflater
        viewDialogTipoProductos = inflater.inflate(R.layout.dialog_list_general, null)


        ///var view=inflater.inflate(R.layout.dialog_list_general, null) as View




        viewDialogTipoProductos?.recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        val lista: java.util.ArrayList<TipoProducto>? = java.util.ArrayList<TipoProducto>()
        val adapterLocal = TipoProductosAdapter(lista!!)
        viewDialogTipoProductos?.recyclerView?.adapter = adapterLocal

        adapterLocal.setItems(Listas.listaTipoProducto())
        val results = String.format(getString(R.string.results_global_search), lista.size)
        viewDialogTipoProductos?.txtResults?.setText(results)
        viewDialogTipoProductos?.txtResults?.setTextColor(resources.getColor(R.color.white))

        viewDialogTipoProductos?.swipeRefreshLayout?.setOnRefreshListener(this)
        viewDialogTipoProductos?.swipeRefreshLayout?.isRefreshing = false
        viewDialogTipoProductos?.swipeRefreshLayout?.isEnabled = false

        viewDialogTipoProductos?.ivClosetDialogUp?.setOnClickListener(this)

        //Config Style Dialog
        /*
       var image = ContextCompat.getDrawable(activity!!.applicationContext, R.drawable.ic_produccion_cultivo)
       var icon = image?.mutate()
       icon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN);
       var title= getString(R.string.title_selected_tipo_productos)
      val dialog = AlertDialog.Builder(context!!,R.style.Theme_Sphinx_Dialog_Alert)
              .setView(viewDialogTipoProductos)
              .setIcon(icon)
              . setTitle(title)
              .setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

              })
              .create()
       //var  resources = dialog.getContext().getResources();
       var color = resources.getColor(R.color.colorPrimary) // your color here
       var titleDividerId = dialog.context.resources.getIdentifier("titleDivider", "id", "android");
       var titleDivider = dialog.getWindow().getDecorView().findViewById<View>(titleDividerId);
       if (titleDivider != null) {
           titleDivider.setBackgroundColor(color);
       }
      dialog?.show()
       dialogProducto=dialog

       */



        val dialog = MaterialDialog.Builder(activity!!)
                .title(getString(R.string.title_selected_tipo_productos))
                .customView(viewDialogTipoProductos!!, true)
                //.positiveText(R.string.btn_save)
                .negativeText(android.R.string.cancel)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.light_green_800)
                //.limitIconToDefaultSize()
                //.maxIconSize(R.dimen.text_size_40)
                .limitIconToDefaultSize()
                // .positiveColorRes(R.color.material_red_400)
                .backgroundColorRes(R.color.black_transparencia)
                // .negativeColorRes(R.color.material_red_400)
                .icon(resources.getDrawable(R.drawable.ic_produccion_cultivo))
                .dividerColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                //.positiveColor(Color.WHITE)
                .negativeColor(resources.getColor(R.color.white_solid))
                .autoDismiss(false)
                //.negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                })
                .build()


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)
        dialogProducto=dialog




    }



    //endregion

    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).replaceCleanFragment(AsistenciaTecnicaFragment())
            }
            R.id.plagas_search_edit_frame -> {
                showAlertDialogTipoProduccion()
            }
            R.id.ivClosetDialogUp -> {
                dialogProducto?.dismiss()
            }
        }
    }
    //endregion

}
