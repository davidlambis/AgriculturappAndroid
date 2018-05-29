package com.interedes.agriculturappv3.modules.comprador.productores


import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
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
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.fragment_productores.*
import kotlinx.android.synthetic.main.fragment_productos_comprador.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.interedes.agriculturappv3.util.EndlessRecyclerViewScrollListener
import com.interedes.agriculturappv3.util.EndlessScrollListenerKotlin
import com.interedes.agriculturappv3.R.id.recyclerView
import com.interedes.agriculturappv3.modules.comprador.productores.adapter.*
import com.interedes.agriculturappv3.util.PaginationScrollListener


class ProductoresFragment : Fragment(),IMainViewProductor.MainView, SwipeRefreshLayout.OnRefreshListener {


    var tipoProductoIdGlobal:Long=0
    var presenter: IMainViewProductor.Presenter? = null
    var adapter: ProductorMoreAdapter?=null
    var productosList:ArrayList<Producto>?=ArrayList<Producto>()

    //Progress
    private var hud: KProgressHUD?=null


    //Productos
    private  val  TAG = "MainActivity";
    //var paginationProductoAdapter:PaginationProductoAdapter?= null;
    var linearLayoutManager:LinearLayoutManager?=null;

    private val PAGE_START:Int = 1;

    private var isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private var TOTAL_PAGES = 5;
    private var currentPage = PAGE_START;
    var loadNext=false
    var PAGE_SIZE=3
    var isLoading:Boolean = false;




    private var loading = true
    var pastVisiblesItems: Int? = 0
    var visibleItemCount:Int? = 0
    var totalItemCount:Int? = 0


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
        presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,0)
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
        adapter = ProductorMoreAdapter(recyclerView, productosList, activity!!)
        recyclerView.adapter = adapter

        //set load more listener for the RecyclerView adapter
        adapter?.setOnLoadMoreListener(object : OnLoadMoreListener {
            override  fun onLoadMore() {
                if (productosList?.size!! <= 10) {


                    //productosList?.add(null)
                    adapter?.notifyItemInserted(productosList?.size!! - 1)
                    Handler().postDelayed({
                        productosList?.removeAt(productosList?.size!! - 1)
                        adapter?.notifyItemRemoved(productosList?.size!!)

                        //Generating more data
                        val index = productosList?.size
                        val end = index!! + 3
                        presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,end)
                    }, 1000)
                } else {
                    Toast.makeText(activity, "Loading data completed", Toast.LENGTH_SHORT).show()
                }
            }
        })

       /* //recyclerView?.layoutManager = GridLayoutManager(activity,1)
        //adapter = ProductorAdapter(ArrayList<Producto>())
        //recyclerView?.adapter = adapter
        adapter =  ProductorAdapter(ArrayList<Producto>());
        //linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.setLayoutManager(linearLayoutManager)

        //recyclerView.setItemAnimator(DefaultItemAnimator());
        recyclerView.adapter=adapter


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0)
                //check for scroll down
                {
                    visibleItemCount = linearLayoutManager?.getChildCount()!!
                    totalItemCount = linearLayoutManager?.getItemCount()
                    pastVisiblesItems = linearLayoutManager?.findFirstVisibleItemPosition()

                    if (loading) {
                        if (visibleItemCount!! + pastVisiblesItems!! >= totalItemCount!!) {
                            loading = false
                            Log.v("...", "Last Item Wow !")
                            //Do pagination.. i.e. fetch new data
                            currentPage += productosList?.size!!
                            loadNext=true
                            // mocking network delay for API call
                            Handler().postDelayed({ presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,currentPage) }, 1000)
                        }
                    }
                }
            }
        })


        */

        /*

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleItemCount = linearLayoutManager?.getChildCount()
                val totalItemCount = linearLayoutManager?.getItemCount()
                val firstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition()

                if(dx>0){
                    Toast.makeText(activity,"dx > 0",Toast.LENGTH_SHORT).show()
                }

                if (!isLoading && !isLastPage) {
                    if (visibleItemCount!! + firstVisibleItemPosition!! >= totalItemCount!!
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {

                        currentPage += productosList?.size!!
                        loadNext=true
                        // mocking network delay for API call
                        Handler().postDelayed({ presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,currentPage) }, 1000)

                        //loadMoreItems()
                    }
                }

            }
        })
*/
       /* recyclerView.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
            override fun loadMoreItems() {
                this@ProductoresFragment.isLoading = true
                currentPage += 1
                loadNext=true
                // mocking network delay for API call
                Handler().postDelayed({ presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,currentPage) }, 1000)
            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES;
            }

            override fun isLastPage(): Boolean {
                return isLastPage;
            }

            override fun isLoading(): Boolean {
                return isLoading;
            }

        })*/

        /*
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = ProductorAdapter(ArrayList<Producto>())
        recyclerView?.adapter = adapter


        recyclerView.setOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                //overallXScroll = overallXScroll + dx

                if(dy>0){
                    Toast.makeText(activity,"Scroll",Toast.LENGTH_SHORT).show()
                }
               // Log.i("check", "overall X  = $overallXScroll")

            }
        })
*/

        /*adapter?.setLoadMoreListener(object : ProductorAdapter.OnLoadMoreListener {
            override fun onLoadMore() {

                recyclerView.post {
                    val index = productosList?.size!! - 1
                   // loadMore(index)// a method which requests remote data
                }
                //Calling loadMore function in Runnable to fix the
                // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
            }
        })
        recyclerView.adapter = adapter
        */

        /*
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.setLayoutManager(linearLayoutManager)
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                if(last==true){
                    presenter?.getListProducto(tipoProductoIdGlobal,10,page)
                }
            }
        }

        recyclerView?.addOnScrollListener(scrollListener)*/


        /*_-----------------------------------*/

        /*
        var loading = true
        var pastVisiblesItems: Int
        var visibleItemCount: Int
        var totalItemCount: Int

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                if (dy > 0)
                //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount()
                    totalItemCount = linearLayoutManager.getItemCount()
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()

                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false

                            presenter?.getListProducto(tipoProductoIdGlobal,totalItemCount,dx)

                            ///Log.v("...", "Last Item Wow !")
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        })
        */

        // Adds the scroll listener to RecyclerView




    }




     fun loadFirstPage() {
       presenter?.getListProducto(tipoProductoIdGlobal,10,0)

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


        /*
        productosList?.addAll(listTipoProducto)


        if(loadNext==true){
            adapter?.removeLoadingFooter();
            isLoading = false;
        }

        if (currentPage <= TOTAL_PAGES) adapter?.addLoadingFooter();
        else isLastPage = true

        adapter?.addAll(listTipoProducto);

        */

        //adapter?.clear()
        //productosList?.clear()
        productosList?.addAll(listTipoProducto)
        adapter?.notifyDataSetChanged()
        adapter?.setLoaded()
        setResults(productosList?.size!!)
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
        //adapter?.clear()
        productosList?.clear()
        adapter?.notifyDataSetChanged()
        adapter?.setLoaded()
        presenter?.getListProducto(tipoProductoIdGlobal,PAGE_SIZE,0)
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
