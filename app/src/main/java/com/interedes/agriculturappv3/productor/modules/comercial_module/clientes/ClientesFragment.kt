package com.interedes.agriculturappv3.productor.modules.comercial_module.clientes


import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.modules.comercial_module.clientes.adapters.ClientesAdapter
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_clientes.*
import java.util.ArrayList


class ClientesFragment : Fragment(), IClientes.View, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    var presenter: IClientes.Presenter? = null
    var clientesList: ArrayList<Usuario>? = ArrayList<Usuario>()
    var adapter: ClientesAdapter? = null

    companion object {
        var instance: ClientesFragment? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_clientes, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ClientesFragment.instance = this
        presenter = ClientesPresenter(this)
        presenter?.onCreate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_clientes)
        ivBackButton.setOnClickListener(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        searchFilter.setOnClickListener(this)
        setupInjection()
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = ClientesAdapter(clientesList!!)
        recyclerView?.adapter = adapter
    }

    private fun setupInjection() {
        presenter?.getListClientes()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton?.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
        }
    }

    //region Métodos Interfaz
    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun setListClientes(listClientes: List<Usuario>) {
        adapter?.clear()
        clientesList?.clear()
        adapter?.setItems(listClientes)
        hideProgress()
        setResults(listClientes.size)
    }

    override fun setResults(clientes: Int) {
        var results = String.format(getString(R.string.results_global_search),
                clientes)
        txtResults.setText(results)
    }

    override fun verificateConnection(): AlertDialog? {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.verificate_conexion))
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_clientes)
        return builder.show()
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }
    //endregion

    override fun onRefresh() {
        showProgress()
        presenter?.getListClientes()
    }
}
