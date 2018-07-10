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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.fragment_productores.*
import kotlinx.android.synthetic.main.fragment_productos_comprador.*
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.interedes.agriculturappv3.modules.comprador.detail_producto.DetalleProductoFragment
import com.interedes.agriculturappv3.modules.comprador.productores.adapter.*


class ProductoresFragment : Fragment(),View.OnClickListener,IMainViewProductor.MainView, SwipeRefreshLayout.OnRefreshListener {



    var tipoProductoIdGlobal:Long=0
    var presenter: IMainViewProductor.Presenter? = null
    var adapter: ProductorAdapter?=null
    var productosList:List<Producto>?=ArrayList<Producto>()

    val TAG = ProductoresFragment::class.java!!.getSimpleName()
    //Progress
    private var hud: KProgressHUD?=null


    private var loadedFragment:Boolean=false


    //Productos
    var PAGE_SIZE=4
    var pastVisiblesItems: Int? = 0



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
        ivBackButton.setOnClickListener(this)
       // swipeRefreshLayout.setOnRefreshListener(this)
        setupInjection()
    }

    private fun setupInjection() {
        if(!loadedFragment){
            showProgressHud()
            loadFirstPageProducts()
            loadedFragment=true
        }else{
            //setListProducto(list)
            if(productosList!=null){
                for (item in productosList!!){
                    adapter?.add(item)
                }
            }

            setResults(productosList?.size!!)
        }


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

       recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ProductorAdapter(ArrayList<Producto>())
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(VerticalLineDecorator(2))


        /*For Load More Pagination
       adapter = ProductorMoreAdapter(productosList, activity)
        adapter?.setLoadMoreListener(object : ProductorMoreAdapter.OnLoadMoreListener {
            override fun onLoadMore() {
                if(pastVisiblesItems!!>=PAGE_SIZE){
                    recyclerView.post {
                        //val index = productosList?.size!! - 1
                        val index = productosList?.size!!
                        loadMore(index)
                    }
                }
                //Calling loadMore function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        })

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(VerticalLineDecorator(2))
        recyclerView.adapter = adapter
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0)
        */
    }


    private fun loadMore(index: Int) {
        //productosList?.add(Producto(Enabled = false))
        adapter?.notifyItemInserted(productosList?.size!! - 1)
        presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,index,false)
    }

    fun loadFirstPageProducts() {
       presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,0,true)
    }

    //region IMPLEMENTS METHODS INTERFACE

    override fun addNewItem(producto: Producto) {
        adapter?.add(producto)
    }
    override fun showProgress() {

        //  swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
       // swipeRefreshLayout.setRefreshing(false);
    }

    override fun showProgressHud(){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white))
        hud?.show()
    }

    override fun hideProgressHud(){
        if(hud?.isShowing!!){
            hud?.dismiss()
        }
    }



    override fun setListProductoFirts(listProducto: List<Producto>) {
       /* productosList?.clear()
        productosList?.addAll(listProducto)
        adapter?.notifyDataChanged()
        setResults(productosList?.size!!)

        //Se asigna para que no ejecute el evento More del Adaptador
        if(presenter?.checkConnection()!!){
            //productosList?.removeAt(productosList?.size!! - 1)
            pastVisiblesItems=listProducto.size

        }else{
            pastVisiblesItems=0
        }

        */
    }

    override fun setListProducto(listProducto: List<Producto>) {

        /*
            productosList?.removeAt(productosList?.size!! - 1)
            if (listProducto.size > 0) {
                //add loaded data
                productosList?.addAll(listProducto!!)
            } else {//result size 0 means there is no more data available at server
                //adapter?.setMoreDataAvailable(false)
                //telling adapter to stop calling load more as no more server data available
                Toast.makeText(context, "No More Data Available", Toast.LENGTH_LONG).show()
            }
            adapter?.notifyDataChanged()
            pastVisiblesItems=listProducto.size
            setResults(productosList?.size!!)
        */
        productosList=listProducto
        setResults(listProducto.size)
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


    //region EVENTS

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
        }
    }

    override fun navigateDetalleTipoProductoUser(poducto: Producto) {
        val bundle = Bundle()
        bundle.putLong("ProductoId", poducto.ProductoId!!)
        val detalleproductosFragment: DetalleProductoFragment
        detalleproductosFragment = DetalleProductoFragment()
        detalleproductosFragment.arguments = bundle
        (activity as MenuMainActivity).replaceFragment(detalleproductosFragment)
    }


    //endregion

    //region OVERRIDES METHODS
    override fun onRefresh() {
        hideProgress()
       // showProgress()
        //adapter?.clear()
        //productosList?.clear()
        //adapter?.notifyDataSetChanged()
        //adapter?.setLoaded()
        //presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,0)
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
        // add this piece of code in onResume method
        this.getView()?.setFocusableInTouchMode(true);
        this.getView()?.requestFocus();
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }

    //end region
}
