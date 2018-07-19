package com.interedes.agriculturappv3.modules.comprador.productos

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.comprador.productores.ProductoresFragment
import com.interedes.agriculturappv3.modules.comprador.productos.adapter.ProductosCompradorAdapter
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_productos_comprador.*


class ProductosCompradorFragment : Fragment(),IMainViewProductoComprador.MainView, SwipeRefreshLayout.OnRefreshListener {

    var presenter: IMainViewProductoComprador.Presenter? = null
    var adapter:ProductosCompradorAdapter?=null
    var tipoProductosList:ArrayList<TipoProducto>?=ArrayList<TipoProducto>()

    //Progress
    private var hud: KProgressHUD?=null

    companion object {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =  ProductoCompradorPresenter(this)
        presenter?.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_productos_comprador, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title=getString(R.string.tittle_productos)
        swipeRefreshLayout.setOnRefreshListener(this)


        setupInit()
        setupInjection()

    }

    private fun setupInit() {
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_menu)
        val sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.colorPrimary));

        } else {
            (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.colorPrimary));
        }
        (activity as MenuMainActivity).toolbar.setTitleTextColor(resources.getColor(R.color.white))
        val iconMenu = (activity as MenuMainActivity).menuItemGlobal
        iconMenu?.isVisible = false


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = (activity as MenuMainActivity).getWindow()
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.statusBarColor = ContextCompat.getColor((activity as MenuMainActivity), R.color.colorPrimary)
            (activity as MenuMainActivity).app_bar_main.elevation = 4f

        } else {
            (activity as MenuMainActivity).app_bar_main.targetElevation = 4f
        }
    }


    private fun setupInjection() {
        presenter?.getListTipoProducto()
    }


    //region ADAPTER
    private fun initAdapter() {
        recyclerView?.layoutManager = GridLayoutManager(activity,2)
        adapter = ProductosCompradorAdapter(tipoProductosList!!)
        recyclerView?.adapter = adapter
    }
    //endregion


    //region Implements Methods Interface

    override fun showProgress() {
        swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        swipeRefreshLayout.setRefreshing(false);
    }

    override fun showProgressHud(){

        val imageView = ImageView(activity);
        imageView.setBackgroundResource(R.drawable.spin_animation_load_comprador);
        val drawable = imageView.getBackground() as AnimationDrawable;
        drawable.start();
        hud = KProgressHUD.create(activity)
                .setCustomView(imageView)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                //.setLabel("Cargando...", resources.getColor(R.color.white_solid))
                .setDetailsLabel("Cargando Informacion")
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }


    override fun setListTipoProducto(listTipoProducto: List<TipoProducto>) {
        adapter?.clear()
        tipoProductosList?.clear()
        adapter?.setItems(listTipoProducto)
        setResults(listTipoProducto.size)
    }

    override fun setResults(listProduccion: Int) {
        val results = String.format(getString(R.string.results_global_search),
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
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }



    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }


    override  fun navigateDetalleTipoProducto(idtipoProducto:Long){
        val bundle = Bundle()
        bundle.putLong("idtipoProducto", idtipoProducto)
        val productosFragment: ProductoresFragment
        productosFragment = ProductoresFragment()
        productosFragment.arguments = bundle
        (activity as MenuMainActivity).replaceFragment(productosFragment)
    }

    //endregion


    //region Overrides Methods
    //call this method in your onCreateMethod
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }


    override fun onRefresh() {
        showProgress()
        presenter?.getListTipoProducto()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause( activity!!.applicationContext)
    }

    override fun onResume() {
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }
    //endregion
}
