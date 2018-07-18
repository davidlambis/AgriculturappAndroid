package com.interedes.agriculturappv3.modules.comprador.productores


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
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.kaopiz.kprogresshud.KProgressHUD
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.interedes.agriculturappv3.modules.comprador.detail_producto.DetalleProductoFragment
import com.interedes.agriculturappv3.modules.comprador.productores.adapter.*
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import android.view.ViewAnimationUtils
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.interedes.agriculturappv3.libs.eventbus_rx.Rx_Bus
import com.interedes.agriculturappv3.modules.models.departments.Departamento
import com.interedes.agriculturappv3.modules.comprador.productores.events.EventDepartamentCities
import com.interedes.agriculturappv3.modules.comprador.productores.resources.RequestFilter
import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.dialog_filter_products.view.*
import kotlinx.android.synthetic.main.fragment_productores.*
import java.math.BigDecimal
import java.math.MathContext

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
    var PAGE_SIZE=4
    var pastVisiblesItems: Int? = 0

    //DialogsFilter
    var _viewdialogFilterProducts: View? = null
    var selectedIndexCiudades=-1
    var selectedDepartment:Departamento?=null
    var selectedCity:Ciudad?=null
    var priceFilterMin:Double=0.0
    var priceFilterMax:Double=10000000.0


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
       // swipeRefreshLayout.setOnRefreshListener(this)
        setupInjection()
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
                    var ss= ex.toString()
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

        val priceMaxBig = BigDecimal(priceFilterMax, MathContext.DECIMAL64)
        val priceMinBig = BigDecimal(priceFilterMin, MathContext.DECIMAL64)

        val filter= RequestFilter(
                false,
                tipoProductoIdGlobal,
                ciudadId,
                priceMinBig,
                priceMaxBig,
                false,
                PAGE_SIZE,
                index
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

        val priceMaxBig = BigDecimal(priceFilterMax, MathContext.DECIMAL64)
        val priceMinBig = BigDecimal(priceFilterMin, MathContext.DECIMAL64)


        val filter= RequestFilter(
               false,
                tipoProductoIdGlobal,
                ciudadId,
                priceMinBig,
                priceMaxBig,
          true,
                PAGE_SIZE,
           0
        )

       presenter?.getListProducto(filter)
    }

    //region IMPLEMENTS METHODS INTERFACE

    override fun addNewItem(producto: Producto) {
        //adapter?.add(producto)
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

        //productosList=listProducto
       // setResults(listProducto.size)
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



        val departments = View.OnClickListener { showDialogDepartment() }
        viewDialog.btnFilterDepartment.setOnClickListener(departments)

        if(selectedIndexCiudades>=0){
            viewDialog?.txtCityDepartment?.setText(String.format("%s / %s", selectedDepartment?.Nombre, selectedCity?.Nombre))
        }


        //rangeSeekbar.setMaxValue(10000000F)
        //rangeSeekbar.setMinValue(0F)
        //rangeSeekbar.setSteps(50000F)

        //rangeSeekbar.setMinStartValue(20000F)
        //rangeSeekbar.setMaxStartValue(9000000F)


        // set listener
        rangeSeekbar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
            //tvMin.text = minValue.toString()
            //tvMax.text = maxValue.toString()

            /*if(minValue.toLong()>=1200000L ){
                    rangeSeekbar.setSteps(100000F)
                    rangeSeekbar.setMinValue(0F)
                    rangeSeekbar.setMaxValue(10000000F)
            }else if(minValue.toLong()<1200000L){
                rangeSeekbar.setSteps(50000F)
                rangeSeekbar.setMinValue(0F)
                rangeSeekbar.setMaxValue(1500000F)
            }*/

            tvMin.text=String.format(context!!.getString(R.string.price),
                    priceFilterMin)
            tvMax.text=String.format(context!!.getString(R.string.price),
                    priceFilterMax)

            priceFilterMin=minValue.toDouble()
            priceFilterMax=maxValue.toDouble()
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
