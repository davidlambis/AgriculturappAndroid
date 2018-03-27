package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.InsumosFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.PlagasAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.TipoProductosAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.listas.Listas
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.content_recyclerview.view.*
import kotlinx.android.synthetic.main.dialog_list_general.view.*
import kotlinx.android.synthetic.main.fragment_plaga.*
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.SelectPlagasAdapter


class PlagaFragment : Fragment(), IPlaga.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    //adapter
    var adapter: PlagasAdapter? = null

    //Presenter
    var presenter: IPlaga.Presenter? = null

    //List Plagas
    var plagasList: ArrayList<TipoEnfermedad>? = ArrayList<TipoEnfermedad>()

    //Dialog Tipo Productos
    var dialogProducto: AlertDialog? = null
    var dialogPlaga: AlertDialog? = null
    var viewDialogTipoProductos: View? = null;
    var viewDialogPlagas: View? = null

    var Tipo_Producto_id: Long? = 0
    var Tipo_Producto_Nombre: String? = null
    var Tipo_Enfermedad_id: Long? = 0

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
        searchFilter?.setOnClickListener(this)
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = PlagasAdapter(plagasList!!)
        recyclerView?.adapter = adapter
    }

    private fun setupInjection() {
        val a = this.arguments
        if (a != null) {
            Tipo_Enfermedad_id = a.getLong("tipoEnfermedadId")
            Tipo_Producto_Nombre = a.getString("nombreTipoProducto")
            searchFilter.setText(Tipo_Producto_Nombre)
            presenter?.setPlaga(Tipo_Enfermedad_id)
        } else {
            showAlertDialogTipoProduccion()
        }
    }


    //region Métodos Interfaz
    override fun getPlagasByTipoProducto(tipoProductoId: Long?) {
        presenter?.getPlagasByTipoProducto(tipoProductoId)
    }

    override fun setListPlagas(list_plagas: ArrayList<TipoEnfermedad>) {
        dialogPlaga?.dismiss()
        adapter?.clear()
        plagasList?.clear()
        adapter?.setItems(list_plagas)
        hideRefresh()
        setResults(list_plagas.size)
    }

    override fun setDialogListPlagas(list_plagas: ArrayList<TipoEnfermedad>) {
        val inflater = this.layoutInflater
        viewDialogPlagas = inflater.inflate(R.layout.dialog_list_general, null)

        viewDialogPlagas?.recyclerView?.layoutManager = LinearLayoutManager(activity)
        val adapterLocal = SelectPlagasAdapter(list_plagas)
        viewDialogPlagas?.recyclerView?.adapter = adapterLocal

        //adapterLocal.setItems(Listas.listaTipoProducto())
        val results = String.format(getString(R.string.results_global_search), list_plagas.size)
        viewDialogPlagas?.txtResults?.setText(results)
        viewDialogPlagas?.txtResults?.setTextColor(resources.getColor(R.color.white))

        viewDialogPlagas?.swipeRefreshLayout?.setOnRefreshListener(this)
        viewDialogPlagas?.swipeRefreshLayout?.isRefreshing = false
        viewDialogPlagas?.swipeRefreshLayout?.isEnabled = false

        viewDialogPlagas?.ivClosetDialogGeneral?.setOnClickListener(this)

        viewDialogPlagas?.title?.setText(getString(R.string.title_selected_plaga))

        val dialog = AlertDialog.Builder(context!!)
                .setView(viewDialogPlagas!! )
                .create()
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.black_transparencia)))


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)
        dialogPlaga = dialog
        /*var item_id: Long? = 0
        val adapter = MaterialSimpleListAdapter { dialog, index1, item -> presenter?.setPlaga(item_id) }
        for (item in list_plagas) {
            item_id = item.Id
            adapter.add(
                    MaterialSimpleListItem.Builder(activity)
                            .content(item.Nombre)
                            .icon(R.drawable.ic_plagas)
                            .backgroundColor(Color.WHITE)
                            .build())
        }

        val dialog = MaterialDialog.Builder(activity!!).title(R.string.title_selected_plaga).adapter(adapter, null).build()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
        dialogPlaga = dialog*/
    }

    override fun verInsumos(tipoEnfermedad: TipoEnfermedad) {
        val bundle = Bundle()
        Tipo_Enfermedad_id = tipoEnfermedad.Id
        bundle.putLong("tipoEnfermedadId", Tipo_Enfermedad_id!!)
        bundle.putString("nombreTipoEnfermedad", tipoEnfermedad.Nombre)
        bundle.putString("nombreTipoProducto", tipoEnfermedad.NombreTipoProducto)
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
        searchFilter.setText(tipoProducto.Nombre)
        Tipo_Producto_id = tipoProducto.Id
        Tipo_Producto_Nombre = tipoProducto.Nombre
        // plagas_search_view.setQuery(tipoProducto.Nombre, false)
    }
    //endregion

    //region Métodos
    override fun onRefresh() {
        showRefresh()
        if (Tipo_Producto_id!! > 0) {
            presenter?.getPlagasByTipoProducto(Tipo_Producto_id)
        } else {
            hideRefresh()
        }
        //showAlertDialogTipoProduccion()
        // presenter?.setPlaga(1)
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

        viewDialogTipoProductos?.ivClosetDialogGeneral?.setOnClickListener(this)

        viewDialogTipoProductos?.title?.setText(getString(R.string.title_selected_tipo_productos))

        val dialog = AlertDialog.Builder(context!!)
                .setView(viewDialogTipoProductos!! )
                .create()
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.black_transparencia)))

/*        val dialog = MaterialDialog.Builder(activity!!)
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

        */


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)
        dialogProducto = dialog


    }


    //endregion

    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).replaceCleanFragment(AsistenciaTecnicaFragment())
            }
            R.id.searchFilter -> {
                showAlertDialogTipoProduccion()
            }
            R.id.ivClosetDialogGeneral -> {
                dialogProducto?.dismiss()
                dialogPlaga?.dismiss()
            }
        }
    }
    //endregion

    //region Ciclo de Vida Fragment

    //endregion


}
