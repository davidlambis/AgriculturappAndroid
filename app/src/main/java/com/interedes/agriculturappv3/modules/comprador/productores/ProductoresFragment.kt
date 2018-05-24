package com.interedes.agriculturappv3.modules.comprador.productores


import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.comprador.productores.adapter.ProductorAdapter
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_productores.*
import kotlinx.android.synthetic.main.fragment_productos_comprador.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import com.interedes.agriculturappv3.util.EndlessRecyclerViewScrollListener
import com.interedes.agriculturappv3.util.EndlessScrollListenerKotlin


class ProductoresFragment : Fragment(),IMainViewProductor.MainView, SwipeRefreshLayout.OnRefreshListener {


    var tipoProductoIdGlobal:Long=0
    var presenter: IMainViewProductor.Presenter? = null
    var adapter:ProductorAdapter?=null
    var productosList:ArrayList<Producto>?=ArrayList<Producto>()

    //Progress
    private var hud: KProgressHUD?=null

    private var scrollListener: EndlessRecyclerViewScrollListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =  ProductorPresenter(this)
        presenter?.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_productores, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        val b = this.arguments
        if (b != null) {
            tipoProductoIdGlobal = b.getLong("idtipoProducto")
        }
        (activity as MenuMainActivity).toolbar.title=getString(R.string.tittle_productos)
        swipeRefreshLayout.setOnRefreshListener(this)
        setupInjection()
    }


    private fun setupInjection() {
        presenter?.getListProducto(tipoProductoIdGlobal,5,0)
        var tipoProducto= presenter?.getTipoProducto(tipoProductoIdGlobal)
        if(tipoProducto!=null){

            txtTipoProducto.setText(tipoProducto.Nombre)

            if(tipoProducto.Imagen!=null){
                // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                // imgTipoProducto.setImageBitmap(bitmap)
                try {
                    val foto = tipoProducto.Imagen?.blob
                    val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    logoTipoProducto.setImageBitmap(bitmapBlob)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }


        }
    }


    //region ADAPTER
    private fun initAdapter() {
        //recyclerView?.layoutManager = GridLayoutManager(activity,1)
        adapter = ProductorAdapter(productosList!!)
        recyclerView?.adapter = adapter

        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.setLayoutManager(linearLayoutManager)
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                presenter?.getListProducto(tipoProductoIdGlobal,totalItemsCount,page)
            }
        }
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener)
    }

    //region IMPLEMENTS METHODS INTERFACE
    override fun showProgress() {
        swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }



    override fun showProgressHud(){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white))
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun setListProducto(listTipoProducto: List<Producto>) {
        adapter?.clear()
        productosList?.clear()
        adapter?.setItems(listTipoProducto)
        setResults(listTipoProducto.size)
    }

    override fun setResults(listProduccion: Int) {
        var results = String.format(getString(R.string.results_global_search),
                listProduccion);
        txtResults.setText(results);
    }


    override fun requestResponseOK() {
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.grey_luiyi, error)
    }


    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container_fragment, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, message: String?) {

        onMessageOk(colorPrimary, message)
    }

    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_produccion_cultivo);
        return builder.show();
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }


    //endregion



    //region OVERRIDES METHODS

    override fun onRefresh() {
        showProgress()
        presenter?.getListProducto(tipoProductoIdGlobal,5,0)
    }


    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }


    override fun onPause() {
        super.onPause()
        presenter?.onPause( activity!!.applicationContext)
    }

    override fun onResume() {
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }

    //end region
}
