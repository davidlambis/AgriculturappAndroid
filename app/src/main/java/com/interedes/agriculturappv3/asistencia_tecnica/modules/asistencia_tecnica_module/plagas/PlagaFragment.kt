package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.PlagasAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_plaga.*


class PlagaFragment : Fragment(), IPlaga.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    //adapter
    var adapter: PlagasAdapter? = null

    //Presenter
    var presenter: IPlaga.Presenter? = null

    //List Plagas
    var plagasList: ArrayList<TipoEnfermedad>? = ArrayList<TipoEnfermedad>()

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

    override fun showRefresh() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }
    //endregion

    //region Métodos
    override fun onRefresh() {
        showRefresh()
        getPlagasByTipoProducto(1)
    }

    override fun setResults(plagas: Int) {
        var results = String.format(getString(R.string.results_global_search),
                plagas)
        txtResults.setText(results)
    }
    //endregion

    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).replaceCleanFragment(AsistenciaTecnicaFragment())
            }
        }
    }
    //endregion

}
