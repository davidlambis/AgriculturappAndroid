package com.interedes.agriculturappv3.comprador.comercial.productos


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.adapters.ProductosAdapter
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_productos_comprador.*
import java.util.ArrayList


class ProductosCompradorFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    var adapter: ProductosCompradorAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_productos_comprador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title = "Productos"
        ivBackButton.setOnClickListener(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        swipeRefreshLayout.isRefreshing = false
        setupInjection()

    }

    private fun initAdapter() {
        (activity as MenuMainActivity).getLastUserLogued()
        val usuariosList = SQLite.select().from(Usuario::class.java).where(Usuario_Table.RolNombre.eq("Productor")).queryList()
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = ProductosCompradorAdapter(usuariosList as ArrayList<Usuario>) { position ->


        }
        recyclerView?.adapter = adapter
    }

    private fun setupInjection() {

    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
        }
    }


}
