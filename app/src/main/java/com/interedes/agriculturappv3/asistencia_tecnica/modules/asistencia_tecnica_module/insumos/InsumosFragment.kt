package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.insumos.Insumo
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.adapters.InsumosAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.PlagaFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.tratamiento.TratamientoFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_plaga.*

class InsumosFragment : Fragment(), InterfaceInsumos.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {


    //adapter
    var adapter: InsumosAdapter? = null

    //Presenter
    var presenter: InterfaceInsumos.Presenter? = null

    //List Insumos
    var insumosList: ArrayList<Insumo>? = ArrayList<Insumo>()

    var dialogInsumo: MaterialDialog? = null

    //Variables Globales
    var tipoEnfermedadId: Long? = 0
    var nombreTipoEnfermedad: String? = null
    var nombreTipoProducto: String? = null
    var tipoProductoId: Long? = 0
    var enfermedadId: Long? = 0

    companion object {
        var instance: InsumosFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        InsumosFragment.instance = this
        presenter = InsumosPresenter(this)
        presenter?.onCreate()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_insumos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val b = this.arguments
        if (b != null) {
            tipoEnfermedadId = b.getLong("tipoEnfermedadId")
            nombreTipoEnfermedad = b.getString("nombreTipoEnfermedad")
            nombreTipoProducto = b.getString("nombreTipoProducto")
            tipoProductoId = b.getLong("tipoProductoId")
            enfermedadId = b.getLong("enfermedadId")
        }
        initAdapter()
        setupInjection()
        (activity as MenuMainActivity).toolbar.title = "Insumos-" + nombreTipoEnfermedad + "(" + nombreTipoProducto + ")"
        swipeRefreshLayout?.setOnRefreshListener(this)
        ivBackButton?.setOnClickListener(this)
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = InsumosAdapter(insumosList!!)
        recyclerView?.adapter = adapter
    }


    private fun setupInjection() {
        getInsumosByPlaga(tipoEnfermedadId)
    }

    //region Métodos Interfaz
    override fun getInsumosByPlaga(tipoEnfermedadId: Long?) {
        presenter?.getInsumosByPlaga(tipoEnfermedadId)
    }

    override fun setInsumosList(listInsumos: List<Insumo>) {
        dialogInsumo?.dismiss()
        adapter?.clear()
        insumosList?.clear()
        adapter?.setItems(listInsumos)
        hideRefresh()
        setResults(listInsumos.size)
    }

    override fun verTratamiento(insumoId: Long?) {
        val bundle = Bundle()
        bundle.putLong("insumoId", insumoId!!)
        bundle.putLong("tipoProductoId", tipoProductoId!!)
        bundle.putLong("enfermedadId", enfermedadId!!)
        val tratamientoFragment: TratamientoFragment
        tratamientoFragment = TratamientoFragment()
        tratamientoFragment.arguments = bundle
        (activity as MenuMainActivity).replaceFragment(tratamientoFragment)
    }

    /*
    override fun setDialogListInsumos(listInsumos: List<Insumo>) {
        if (listInsumos.size > 0) {
            var item_id: Long? = 0
            val adapter = MaterialSimpleListAdapter { dialog, index1, item -> presenter?.setInsumo(item_id) }
            for (item in listInsumos) {
                item_id = item.Id
                adapter.add(
                        MaterialSimpleListItem.Builder(activity)
                                .content(item.Nombre)
                                .icon(R.drawable.ic_insumos)
                                .backgroundColor(Color.WHITE)
                                .build())
            }

            val dialog = MaterialDialog.Builder(activity!!).title(R.string.title_selected_insumo).adapter(adapter, null).build()
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
            dialogInsumo = dialog
        }
    } */

    override fun showRefresh() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun setResults(insumos: Int) {
        val results = String.format(getString(R.string.results_global_search),
                insumos)
        txtResults.setText(results)
    }
    //endregion

    //region Método Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                /* val bundle = Bundle()
                 bundle.putLong("tipoEnfermedadId", tipoEnfermedadId!!)
                 bundle.putString("nombreTipoProducto", nombreTipoProducto)
                 val plagaFragment: PlagaFragment
                 plagaFragment = PlagaFragment()
                 plagaFragment.arguments = bundle
                 (activity as MenuMainActivity).replaceCleanFragment(plagaFragment)*/
                (activity as MenuMainActivity).onBackPressed()
            }
        }
    }
    //endregion

    //region Métodos
    override fun onRefresh() {
        showRefresh()
        getInsumosByPlaga(tipoEnfermedadId)
    }
    //endregion
}
