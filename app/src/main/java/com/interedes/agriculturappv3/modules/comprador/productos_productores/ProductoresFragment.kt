package com.interedes.agriculturappv3.modules.comprador.productos_productores


import android.app.Dialog
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

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.kaopiz.kprogresshud.KProgressHUD
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.interedes.agriculturappv3.modules.comprador.detail_producto.DetalleProductoFragment
import com.interedes.agriculturappv3.modules.comprador.productos_productores.adapter.*
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import android.view.ViewAnimationUtils
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.interedes.agriculturappv3.libs.eventbus_rx.Rx_Bus
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.comprador.productos_productores.events.EventDepartamentCities
import com.interedes.agriculturappv3.modules.comprador.productos_productores.resources.RequestFilter
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.dialog_filter_products.view.*
import kotlinx.android.synthetic.main.fragment_productores.*
import java.math.BigDecimal
import java.math.MathContext


import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto_Table
import com.interedes.agriculturappv3.modules.models.producto.RangePrice
import com.interedes.agriculturappv3.services.resources.ListasResources
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.raizlabs.android.dbflow.sql.language.SQLite


class ProductoresFragment : Fragment(),View.OnClickListener,IMainViewProductor.MainView, SwipeRefreshLayout.OnRefreshListener {



    var tipoProductoIdGlobal:Long=0
    var presenter: IMainViewProductor.Presenter? = null
    var adapter: ProductorMoreAdapter?=null
    var productosList:ArrayList<Producto>?=ArrayList<Producto>()

    val TAG = ProductoresFragment::class.java!!.getSimpleName()
    //Progress
    private var hud: KProgressHUD?=null
    private var loadedFragment:Boolean=false


    //Productos
    var PAGE_SIZE=10
    var pastVisiblesItems: Int? = 0

    //DialogsFilter
    var _viewdialogFilterProducts: View? = null
    var selectedIndexCiudades=-1
    var selectedDepartment:Departamento?=null
    var selectedCity:Ciudad?=null


    var arraySearch = arrayOf<String>("")
    var stringSearch=""
    //var arraySearch = arrayOf("Hola","Hola 2")


    var priceFilterMin=0f
    var priceFilterMax=1000000f

    var itemPriceRangeFilter:RangePrice=RangePrice(1000000.0)


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
        imageViewFilter.setOnClickListener(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        setupInjection()
        setupSearch()
    }

    private fun setupSearch() {
        val detalleTipoProducto=SQLite.select().from(DetalleTipoProducto::class.java).where(DetalleTipoProducto_Table.TipoProductoId.eq(tipoProductoIdGlobal)).queryList()
        for (item in detalleTipoProducto){
               arraySearch+= arrayOf(item.Nombre!!)
        }

        (activity as MenuMainActivity).menuItemSearchGlobal?.isVisible=true
        (activity as MenuMainActivity).search_view.setCursorDrawable(R.drawable.custom_cursor);
        (activity as MenuMainActivity).search_view.setSuggestions(arraySearch)
        //(activity as MenuMainActivity).search_view.setVoiceSearch(false)
        //(activity as MenuMainActivity).search_view.setEllipsize(true)
        (activity as MenuMainActivity).search_view.setOnQueryTextListener( object:MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                //Toast.makeText(context, "SUBTMIT: ", Toast.LENGTH_SHORT).show()
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                //if(newText?.length!!>2){
                   // Toast.makeText(context, "QUERY: "+newText, Toast.LENGTH_SHORT).show()
                //}
                return false
            }
        });

        (activity as MenuMainActivity).search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                (activity as MenuMainActivity).search_view.dismissSuggestions();
                //(activity as MenuMainActivity).search_view.setQuery("", false);
                //(activity as MenuMainActivity).search_view.closeSearch();
                //Do some magic
                //Toast.makeText(context, "CLICK: ", Toast.LENGTH_SHORT).show()
            }
            override fun onSearchViewClosed() {
                stringSearch=""
                //loadFirstPageProducts()
                //Do some magic
                //Toast.makeText(context, "CLOSED", Toast.LENGTH_SHORT).show()
            }
        })

        (activity as MenuMainActivity).search_view.setOnItemClickListener(object :AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                (activity as MenuMainActivity).search_view.dismissSuggestions();
                (activity as MenuMainActivity).search_view.hideKeyboard(view)
                (activity as MenuMainActivity).search_view.setQuery("", false);

                (activity as MenuMainActivity).search_view.setSuggestions(null)
                //(activity as MenuMainActivity).search_view.setQuery(parent?.getItemAtPosition(position).toString(),false)
                //(activity as MenuMainActivity).search_view.setEllipsize(false)
                //(activity as MenuMainActivity).search_view.closeSearch();
               // Toast.makeText(activity, parent?.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                stringSearch=parent?.getItemAtPosition(position).toString()
                (activity as MenuMainActivity).search_view.setHint(stringSearch)
                loadFirstPageProducts()


                //hideKeyboard()
               // (activity as MenuMainActivity).search_view.setAdapter( SearchAdapter(activity, parent?.getItemAtPosition(position).toString()));
            }
        })
    }

    private fun setupInjection() {
        if(!loadedFragment){
            loadFirstPageProducts()
            loadedFragment=true
        }else{
            //setListProducto(list)
            //if(productosList!=null){
                //adapter?.setItems(productosList!!)
                /*for (item in productosList!!){
                    adapter?.add(item)
                }*/
            //}
            setResults(productosList?.size!!)
        }

        val tipoProducto= presenter?.getTipoProducto(tipoProductoIdGlobal)
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
                    val ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }
        }
    }

    //region ADAPTER
    private fun initAdapter() {
        //recyclerView.layoutManager = LinearLayoutManager(activity)
        //adapter = ProductorAdapter(ArrayList<Producto>())
       // recyclerView.adapter = adapter
       //recyclerView.addItemDecoration(VerticalLineDecorator(2))
        /*For Load More Pagination*/
       adapter = ProductorMoreAdapter(productosList!!, activity)
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
    }


    private fun loadMore(index: Int) {
        productosList?.add(Producto(Enabled = false))
        adapter?.notifyItemInserted(productosList?.size!! - 1)


        var ciudadId=0L
        if(selectedIndexCiudades>=0){
            ciudadId=selectedCity?.Id!!
        }else{
            ciudadId=0
        }

        val priceMaxBig = BigDecimal(priceFilterMax.toDouble(), MathContext.DECIMAL64)
        val priceMinBig = BigDecimal(priceFilterMin.toDouble(), MathContext.DECIMAL64)

        val filter= RequestFilter(
                false,
                tipoProductoIdGlobal,
                ciudadId,
                priceMinBig,
                priceMaxBig,
                false,
                PAGE_SIZE,
                index,
                stringSearch
        )

        presenter?.getListProducto(filter)
    }

    fun loadFirstPageProducts() {
        showProgressHud()

        var ciudadId=0L
        if(selectedIndexCiudades>=0){
            ciudadId=selectedCity?.Id!!
        }else{
            ciudadId=0
        }

        val priceMaxBig = BigDecimal(priceFilterMax.toDouble(), MathContext.DECIMAL64)
        val priceMinBig = BigDecimal(priceFilterMin.toDouble(), MathContext.DECIMAL64)


        val filter= RequestFilter(
               false,
                tipoProductoIdGlobal,
                ciudadId,
                priceMinBig,
                priceMaxBig,
          true,
                PAGE_SIZE,
           0,
                stringSearch
        )

       presenter?.getListProducto(filter)
    }

    //region IMPLEMENTS METHODS INTERFACE

    override fun addNewItem(producto: Producto) {
        //adapter?.add(producto)
    }
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
        if(hud?.isShowing!!){
            hud?.dismiss()
        }
    }

    private fun hideKeyboard() {
        val inputManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity?.getCurrentFocus()?.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    override fun setListProductoFirts(listProducto: List<Producto>) {
        //arraySearch= arrayOf("")
        productosList?.clear()
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

        /*for (item in listProducto){
            val verificate= arraySearch.filter {s -> s == item.Nombre }
            if(verificate.isEmpty()){
                arraySearch+= arrayOf(item.Nombre!!)
            }
        }*/
        //(activity as MenuMainActivity).search_view.setSuggestions(arraySearch)
    }

    override fun setListProducto(listProducto: List<Producto>) {
            productosList?.removeAt(productosList?.size!! - 1)
            if (listProducto.size > 0) {
                //add loaded data
                productosList?.addAll(listProducto)
            } else {//result size 0 means there is no more data available at server
                //adapter?.setMoreDataAvailable(false)
                //telling adapter to stop calling load more as no more server data available
                Toast.makeText(context, "No More Data Available", Toast.LENGTH_LONG).show()
            }
            adapter?.notifyDataChanged()
            pastVisiblesItems=listProducto.size
            setResults(productosList?.size!!)
        /*for (item in listProducto){
            arraySearch+= arrayOf(item.Nombre!!)
        }*/
        //productosList=listProducto
        //setResults(listProducto.size)
    }

    override fun setResults(listProduccion: Int) {
        val results = String.format(getString(R.string.results_global_search),
                listProduccion);
        if(txtResults!=null){
            txtResults.setText(results);
        }
    }

    override fun requestResponseOK() {
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.grey_luiyi, error)
    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar.make(container_fragment, message!!, Snackbar.LENGTH_LONG)
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
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
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



    override fun showAlertDialogFilterProducts() {
        val inflater = this.layoutInflater
        val viewDialog  = inflater.inflate(R.layout.dialog_filter_products, null)
        _viewdialogFilterProducts=viewDialog
        // get seekbar from view
        val rangeSeekbar = viewDialog.rangeSeekbar5

        // get min and max text view
        val tvMin = viewDialog.textMin5
        val tvMax = viewDialog.textMax5

        viewDialog.txtSetFilter.setOnClickListener(this)

        setListRangePrice(viewDialog)

        val departments = View.OnClickListener { showDialogDepartment() }
        viewDialog.btnFilterDepartment.setOnClickListener(departments)

        if(selectedIndexCiudades>=0){
            viewDialog?.txtCityDepartment?.setText(String.format("%s / %s", selectedDepartment?.Nombre, selectedCity?.Nombre))
        }


       /* viewDialog?.rangeSeekbar5?.setIndicatorTextDecimalFormat("0");
        viewDialog?.rangeSeekbar5?.setOnRangeChangedListener( object :OnRangeChangedListener {
            override fun onRangeChanged(view: RangeSeekBar,  leftValue:Float,  rightValue:Float,  isFromUser:Boolean) {
                if (leftValue <= 50){
                    view.setProgressColor(getResources().getColor(R.color.colorAccent));
                    view.getLeftSeekBar().setThumbDrawableId(R.drawable.thumb_activated);
                    view.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.colorAccent));
                }else {
                    view.setProgressColor(getResources().getColor(R.color.colorPrimary));
                    view.getLeftSeekBar().setIndicatorBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    view.getLeftSeekBar().setThumbDrawableId(R.drawable.thumb_pressed);
                }
            }
            override fun onStartTrackingTouch(view:RangeSeekBar , isLeft:Boolean ) {

            }
            override fun onStopTrackingTouch(view:RangeSeekBar , isLeft:Boolean ) {
            }
        });*/
        
        setRangeSnackbar(viewDialog)

      /*  Handler().postDelayed({
            rangeSeekbar.setSteps(50000f).setMinValue(0F).setMaxValue(1000000F).setMinStartValue(priceFilterMin).setMaxStartValue(priceFilterMax).apply()
            // rangeSeekbar.setMinValue(10000).setMaxValue(20000).setMinStartValue(12000).setMaxStartValue(12000).apply();
        }, 500)*/


        var firts= true
        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->

            if(!firts){

                priceFilterMin= minValue.toFloat()
                priceFilterMax= maxValue.toFloat()

                tvMin.text=String.format(context!!.getString(R.string.price),
                        priceFilterMin.toDouble())
                tvMax.text=String.format(context!!.getString(R.string.price),
                        priceFilterMax.toDouble())

                firts=false
            }else{
                firts=false
            }
        }

        val dialog = AlertDialog.Builder(context!!, R.style.MyAlertDialogStyle)
                .setView(viewDialog)
                .create()
        //dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
       /* val dialog = Dialog(context!!, R.style.MyAlertDialogStyle)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(viewDialog)*/
        dialog.setOnShowListener { revealShow(viewDialog!!, true, null) }
        dialog.setOnKeyListener(DialogInterface.OnKeyListener { dialogInterface, i, keyEvent ->
            if (i == KeyEvent.KEYCODE_BACK) {
                revealShow(viewDialog!!, false, dialog)
                return@OnKeyListener true
            }
            false
        })


        val filter = View.OnClickListener {
            if(dialog!=null){
                dialog.dismiss()
            }
            loadFirstPageProducts()
        }

        viewDialog.txtSetFilter.setOnClickListener(filter)

        val closeDialog = View.OnClickListener {  revealShow(viewDialog!!, false, dialog)}
        viewDialog.ivClosetDialogFilter.setOnClickListener(closeDialog)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        //lp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Hide KeyBoard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        dialog.show()
        //dialog.getWindow().setAttributes(lp)
        //_dialogRegisterUpdate=dialog
    }

    private fun setRangeSnackbar(viewD: View) {
        if(priceFilterMax<=1000f){
            Handler().postDelayed({
                viewD.rangeSeekbar5.setSteps(100f).setMinValue(0f).setMaxValue(1000f).setMinStartValue(priceFilterMin).setMaxStartValue(priceFilterMax).apply()
                // rangeSeekbar.setMinValue(10000).setMaxValue(20000).setMinStartValue(12000).setMaxStartValue(12000).apply();
            }, 500)
        }
        else if(priceFilterMax>1000f && priceFilterMax<=10000f){
            Handler().postDelayed({
                viewD.rangeSeekbar5.setSteps(500f).setMinValue(0f).setMaxValue(10000f).setMinStartValue(priceFilterMin).setMaxStartValue(priceFilterMax).apply()
                // rangeSeekbar.setMinValue(10000).setMaxValue(20000).setMinStartValue(12000).setMaxStartValue(12000).apply();
            }, 500)
        }

        else if(priceFilterMax>10000f && priceFilterMax<=100000f){
            Handler().postDelayed({
                viewD.rangeSeekbar5.setSteps(5000f).setMinValue(0f).setMaxValue(100000f).setMinStartValue(priceFilterMin).setMaxStartValue(priceFilterMax).apply()
                // rangeSeekbar.setMinValue(10000).setMaxValue(20000).setMinStartValue(12000).setMaxStartValue(12000).apply();
            }, 500)
        }

        else if(priceFilterMax>100000f && priceFilterMax<=1000000f){
            Handler().postDelayed({
                viewD.rangeSeekbar5.setSteps(50000f).setMinValue(0F).setMaxValue(1000000F).setMinStartValue(priceFilterMin).setMaxStartValue(priceFilterMax).apply()
                // rangeSeekbar.setMinValue(10000).setMaxValue(20000).setMinStartValue(12000).setMaxStartValue(12000).apply();
            }, 500)
        }

        else if(priceFilterMax>1000000f && priceFilterMax<=10000000f){

            Handler().postDelayed({
                viewD.rangeSeekbar5.setSteps(500000f).setMinValue(0f).setMaxValue(10000000f).setMinStartValue(priceFilterMin).setMaxStartValue(priceFilterMax).apply()
                // rangeSeekbar.setMinValue(10000).setMaxValue(20000).setMinStartValue(12000).setMaxStartValue(12000).apply();
            }, 500)
        }

        else if(priceFilterMax>10000000f && priceFilterMax<=100000000f){
            Handler().postDelayed({
                viewD.rangeSeekbar5.setSteps(1000000f).setMinValue(0f).setMaxValue(100000000f).setMinStartValue(priceFilterMin).setMaxStartValue(priceFilterMax).apply()
                // rangeSeekbar.setMinValue(10000).setMaxValue(20000).setMinStartValue(12000).setMaxStartValue(12000).apply();
            }, 500)
        }
    }

    override fun setListRangePrice(viewD:View) {
        val listRangePrice= ListasResources.listaRangePrice()
            ///Adapaters
        viewD.spinnerRangePrice.setAdapter(null)
            val uMedidaArrayAdapter = ArrayAdapter<RangePrice>(activity, android.R.layout.simple_list_item_activated_1, listRangePrice);
        viewD.spinnerRangePrice?.setAdapter(uMedidaArrayAdapter);
        viewD.spinnerRangePrice.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->

                itemPriceRangeFilter= listRangePrice[position] as RangePrice

                if(itemPriceRangeFilter.Valor_Producto==1000.0){
                    viewD.rangeSeekbar5.setSteps(100f).setMinValue(0f).setMaxValue(1000f)
                    priceFilterMin= 0f
                    priceFilterMax= 1000f

                }
                else if(itemPriceRangeFilter.Valor_Producto==10000.0){
                    viewD.rangeSeekbar5.setSteps(500f).setMinValue(0f).setMaxValue(10000f)
                    priceFilterMin= 0f
                    priceFilterMax= 10000f
                }

                else if(itemPriceRangeFilter.Valor_Producto==100000.0){
                    viewD.rangeSeekbar5.setSteps(5000f).setMinValue(0f).setMaxValue(100000f)
                    priceFilterMin= 0f
                    priceFilterMax= 100000f
                }

                else if(itemPriceRangeFilter.Valor_Producto==1000000.0){
                    viewD.rangeSeekbar5.setSteps(50000f).setMinValue(0f).setMaxValue(1000000f)
                    priceFilterMin= 0f
                    priceFilterMax= 1000000f
                }

                else if(itemPriceRangeFilter.Valor_Producto==10000000.0){
                    viewD.rangeSeekbar5.setSteps(500000f).setMinValue(0f).setMaxValue(10000000f)
                    priceFilterMin= 0f
                    priceFilterMax= 10000000f
                }

                else if(itemPriceRangeFilter.Valor_Producto==100000000.0){
                    viewD.rangeSeekbar5.setSteps(1000000f).setMinValue(0f).setMaxValue(100000000f)
                    priceFilterMin= 0f
                    priceFilterMax= 100000000f
                }



                viewD.textMax5.text=String.format(context!!.getString(R.string.price),
                        priceFilterMax.toDouble())
                viewD.textMin5.text=String.format(context!!.getString(R.string.price),
                    priceFilterMin.toDouble())


                //Toast.makeText(activity,""+ unidadMedidaGlobal!!.Id.toString(),Toast.LENGTH_SHORT).show()
            }

        if(itemPriceRangeFilter!=null){
            viewD.spinnerRangePrice?.setText(itemPriceRangeFilter.toString())

        }
    }

    private fun showDialogDepartment() {

        val listCiudades:ArrayList<Ciudad> = ArrayList<Ciudad>()
        val listDepartamentos:ArrayList<Departamento> = ArrayList<Departamento>()
        val departamentoOptionAll=Departamento()

        //Add new item
        departamentoOptionAll.Id=0
        departamentoOptionAll.Nombre="Todos"
        departamentoOptionAll.codigodpto=0
        listDepartamentos.add(departamentoOptionAll)

        val adapter = MaterialSimpleListAdapter { dialog, index1, item ->
            if(index1>0){
                val itemSelected= listDepartamentos.get(index1)
                val listCiuudad= listCiudades.filter { ciudad: Ciudad -> ciudad.departmentoId==itemSelected.Id }
                showDialogCities(itemSelected,dialog,itemSelected.Nombre,listCiuudad)
            }else{
                selectedIndexCiudades=-1
                if(_viewdialogFilterProducts!=null){
                    _viewdialogFilterProducts?.txtCityDepartment?.setText(String.format("%s", "Todos"))
                }
                dialog.dismiss()
            }
            ///onMessageOk(R.color.orange,String.format("%s : %s",index1 , item))
        }

        MaterialDialog.Builder(activity!!)
                .title(R.string.department)
                .negativeText(android.R.string.cancel)
                .adapter(adapter, null)
                .onNegative({ dialog1, which ->
                    if(selectedIndexCiudades>0){
                        dialog1.dismiss()
                       // Toast.makeText(activity,selectedDepartment?.Nombre+" /"+selectedCity?.Nombre,Toast.LENGTH_LONG).show()
                    }else{
                         selectedIndexCiudades=-1
                         dialog1.dismiss()
                        _viewdialogFilterProducts?.txtCityDepartment?.setText(String.format("%s", "Todos"))
                        //Toast.makeText(activity,selectedDepartment?.Nombre+" /"+selectedCity?.Nombre,Toast.LENGTH_LONG).show()
                    }
                })
                .show()

        adapter.add(
                MaterialSimpleListItem.Builder(activity)
                        .content("Todos")
                        .backgroundColor(Color.WHITE)
                        .build())

        Rx_Bus.listen(EventDepartamentCities::class.java).subscribe({
            val items= it.departamentos as List<Departamento>
            for (item in items){
                listDepartamentos.add(item)
                adapter.add(
                        MaterialSimpleListItem.Builder(activity)
                                .content(item.Nombre)
                                //.icon(R.drawable.ic_account_circle)
                                .backgroundColor(Color.WHITE)
                                .build())

            }
            //listDepartamentos.addAll(items)
            val ciudades = it.cities as List<Ciudad>
            listCiudades.addAll(ciudades)
        })

        presenter?.getListDepartmentCities()
    }



    private fun showDialogCities(departamento:Departamento,dialogDepartment:MaterialDialog,departament:String?,listCiuudad: List<Ciudad>) {

        MaterialDialog.Builder(activity!!)
                .title(R.string.city)
                .items(listCiuudad)
                //.itemsDisabledIndices(1, 3)
                .itemsCallbackSingleChoice(
                        selectedIndexCiudades,
                        { dialog, view, index, text ->
                            val itemSelected= listCiuudad.get(index)

                            selectedDepartment=departamento
                            selectedCity=itemSelected
                            selectedIndexCiudades= index

                            if(_viewdialogFilterProducts!=null){
                                _viewdialogFilterProducts?.txtCityDepartment?.setText(String.format("%s / %s", departament, itemSelected.Nombre))
                            }
                            dialog.dismiss()
                            dialogDepartment.dismiss()
                            //Toast.makeText(activity,selectedDepartment?.Nombre+" /"+selectedCity?.Nombre,Toast.LENGTH_LONG).show()
                            true // allow selection
                        })
                .positiveText(R.string.select)
                .negativeText(android.R.string.cancel)
                .onNegative({ dialog1, which ->
                    if(selectedIndexCiudades>=0){
                        //Toast.makeText(activity,selectedDepartment?.Nombre+" /"+selectedCity?.Nombre,Toast.LENGTH_LONG).show()
                      dialog1.dismiss()
                    }else{
                        dialog1.dismiss()
                    }
                })
                .show()
    }

    private fun revealShow(dialogView: View, b: Boolean, dialog: Dialog?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val view = dialogView.dialogFilter
            val w = view.getWidth()
            val h = view.getHeight()

            val endRadius = Math.hypot(w.toDouble(), h.toDouble()).toInt()
            val cx = (imageViewFilter.getX() + imageViewFilter.getWidth() / 2)
            val cy = imageViewFilter.getY()  + imageViewFilter.getHeight() + 56

            if (b) {
                val revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx.toInt(), cy.toInt(), 0f, endRadius.toFloat())
                view.setVisibility(View.VISIBLE)
                revealAnimator.duration = 700
                revealAnimator.start()
            } else {

                val anim = ViewAnimationUtils.createCircularReveal(view, cx.toInt(), cy.toInt(), endRadius.toFloat(), 0f)
                anim.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        dialog?.dismiss()
                        view.setVisibility(View.INVISIBLE)
                    }
                })

                anim.duration = 700
                anim.start()
            }
        }else{
            if (b) {
                dialog?.show()
            } else {
               dialog?.dismiss()
            }
        }
    }
    //endregion


    //region EVENTS

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> {
                (activity as MenuMainActivity).menuItemSearchGlobal?.isVisible=false
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }

            R.id.imageViewFilter->{
                showAlertDialogFilterProducts()
            }
        }
    }

    override fun navigateDetalleTipoProductoUser(poducto: Producto) {
        val bundle = Bundle()
        bundle.putLong("ProductoId", poducto.ProductoId!!)
        val detalleproductosFragment: DetalleProductoFragment
        detalleproductosFragment = DetalleProductoFragment()
        detalleproductosFragment.arguments = bundle
        (activity as MenuMainActivity).menuItemSearchGlobal?.isVisible=false
        (activity as MenuMainActivity).search_view.closeSearch();
        (activity as MenuMainActivity).replaceFragment(detalleproductosFragment)
    }


    //endregion

    //region OVERRIDES METHODS
    override fun onRefresh() {
        //hideProgress()
        loadFirstPageProducts()
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
