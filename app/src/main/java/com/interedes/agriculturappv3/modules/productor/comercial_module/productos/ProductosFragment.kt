package com.interedes.agriculturappv3.modules.productor.comercial_module.productos


import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.producto.CalidadProducto
import com.interedes.agriculturappv3.modules.models.producto.CalidadProducto_Table
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida_Table
import com.interedes.agriculturappv3.modules.productor.comercial_module.productos.adapters.ProductosAdapter
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.resources.RequestAccessPhoneResources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_form_producto.*
import kotlinx.android.synthetic.main.dialog_form_producto.view.*
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
import kotlinx.android.synthetic.main.fragment_productos.*
import pl.aprilapps.easyphotopicker.EasyImage
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class ProductosFragment : Fragment(), IProductos.View, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    var presenter: IProductos.Presenter? = null
    var viewDialogFilter: View? = null
    var _dialogFilter: MaterialDialog? = null

    //Listas
    var cultivoGlobal: Cultivo? = null
    var unidadProductivaGlobal: Unidad_Productiva? = null
    var loteGlobal: Lote? = null

    var Cultivo_Id: Long? = null

    var adapter: ProductosAdapter? = null

    var productosList: ArrayList<Producto>? = ArrayList<Producto>()
    var viewDialog: View? = null


    var calidadProductoGlobal: CalidadProducto? = null
    var productoGlobal: Producto? = null
    var _dialogRegisterUpdate: AlertDialog? = null
    var dateTime = Calendar.getInstance()
    var fechaLimiteDisponibilidad: Date? = null
    var dialogo: AlertDialog? = null

    var unidadMedidaCantidadGlobal: Unidad_Medida? = null
    var unidadMedidaPrecioGlobal: Unidad_Medida? = null

    //CÁMARA
    val IMAGE_DIRECTORY = "/Productos"




    var imageGlobal: ByteArray? = null
    // var imageGlobalRutaFoto: String? = null

    var isFoto: Boolean? = false

    //Progress
    /** These can be lateinit as they are set in onCreate */
    private var hud: KProgressHUD? = null


    //Permission
    private val PERMISSION_REQUEST_CODE = 1
    var PERMISSION_ALL = 3
    var PERMISSIONS = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    companion object {
        var instance: ProductosFragment? = null
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_productos, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProductosFragment.instance = this
        presenter = ProductosPresenter(this)
        presenter?.onCreate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_productos)
        ivBackButton.setOnClickListener(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        searchFilter.setOnClickListener(this)
        fabAddProducto.setOnClickListener(this)
        setupInjection()
        YoYo.with(Techniques.Pulse)
                .repeat(5)
                .playOn(fabAddProducto)
    }

    //region ADAPTER
    private fun initAdapter() {
        recyclerView?.layoutManager = GridLayoutManager(activity,2)
        adapter = ProductosAdapter(productosList!!)
        recyclerView?.adapter = adapter
    }

    private fun setupInjection() {
        presenter?.getListProductos(Cultivo_Id)
    }


    //endregion

    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabAddProducto -> {
                showAlertDialogFilterProducto(false)
            }
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
            R.id.searchFilter -> {
                showAlertDialogFilterProducto(true)
            }
            R.id.ivCloseButtonDialogFilter -> {
                _dialogFilter?.dismiss()
            }
            R.id.ivClosetDialogProduccion -> {
                _dialogRegisterUpdate?.dismiss()
            }
            R.id.product_image -> {

            }
            R.id.product_camera -> {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(activity, *PERMISSIONS)) {
                        requestPermission()
                    } else {
                        val response = doPermissionGrantedStuffs()
                        if (response) {
                            takePictureWithCamera(this)
                            //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                        }
                    }
                } else {
                    val response = doPermissionGrantedStuffs()
                    if (response) {
                        takePictureWithCamera(this)
                        //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                    }
                }
            }
            R.id.product_gallery -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(activity, *PERMISSIONS)) {
                        requestPermission()
                    } else {
                        val response = doPermissionGrantedStuffs()
                        if (response) {
                            choosePhotoFromGallery(this)
                            //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                        }
                    }
                } else {
                    val response = doPermissionGrantedStuffs()
                    if (response) {
                        choosePhotoFromGallery(this)
                        //startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), READ_REQUEST_CODE)
                    }
                }
            }
            R.id.product_cancel -> {
                isFoto = false
                viewDialog?.product_image?.setImageResource(R.drawable.ic_foto_producto_square)
            }
            R.id.txtFechaLimiteDisponibilidad -> {
                updateDate()
            }
            R.id.btnSaveProducto -> {
                if (productoGlobal != null) {
                    updateProducto(productoGlobal!!)
                } else {
                    registerProducto()
                    presenter?.getCultivo(Cultivo_Id)
                }
            }
        }
    }
    //endregion

    //region Métodos Interfaz
    override fun showAlertDialogFilterProducto(isFilter: Boolean?) {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()

        var title: String? = null


        if (isFilter == true) {
            title = getString(R.string.tittle_filter)
        } else {
            title = getString(R.string.title_select_producto)
        }

        if (unidadProductivaGlobal != null && loteGlobal != null && cultivoGlobal != null) {
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Unidad_Productiva_Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.LoteId)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.Nombre)
            viewDialogFilter?.spinnerCultivo?.setText(cultivoGlobal?.Nombre)

        }

        //Set Events
        viewDialogFilter?.ivCloseButtonDialogFilter?.setOnClickListener(this)

        val dialog = MaterialDialog.Builder(activity!!)
                .title(title)
                .customView(viewDialogFilter!!, true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.close)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.light_green_800)
                .limitIconToDefaultSize()
                //.maxIconSizeRes(R.dimen.text_size_40)
                // .positiveColorRes(R.color.material_red_400)
                .backgroundColorRes(R.color.white_solid)
                // .negativeColorRes(R.color.material_red_400)
                .iconRes(R.drawable.ic_lote)
                .dividerColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .autoDismiss(false)
                //.negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onPositive(
                        { dialog1, which ->
                            if (isFilter == true) {
                                if (presenter?.validarListasAddProducto() == true) {
                                    dialog1.dismiss()
                                    presenter?.getListProductos(Cultivo_Id)
                                    presenter?.getCultivo(Cultivo_Id)
                                }
                            } else {
                                if (presenter?.validarListasAddProducto() == true) {
                                    dialog1.dismiss()
                                    showAlertDialogAddProducto(null)
                                }
                            }
                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                })
                .build()


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _dialogFilter = dialog
    }

    override fun showAlertDialogAddProducto(producto: Producto?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_producto, null)
        presenter?.setListSpinnerMoneda()
        presenter?.setListSpinnerCantidades()
        presenter?.setListSpinnerCalidadProducto()
        viewDialog?.product_image?.setOnClickListener(this)
        viewDialog?.product_camera?.setOnClickListener(this)
        viewDialog?.product_gallery?.setOnClickListener(this)
        viewDialog?.product_cancel?.setOnClickListener(this)
        viewDialog?.txtFechaLimiteDisponibilidad?.setOnClickListener(this)
        viewDialog?.ivClosetDialogProduccion?.setOnClickListener(this)
        viewDialog?.btnSaveProducto?.setOnClickListener(this)

        productoGlobal = producto

        //REGISTER
        if (producto == null) {
            viewDialog?.txtTitle?.setText(getString(R.string.title_registrar_producto))
            viewDialog?.txtUnidadProductivaSelected?.setText(unidadProductivaGlobal?.nombre)
            viewDialog?.txtLoteSelected?.setText(loteGlobal?.Nombre)
            viewDialog?.txtCultivoSelected?.setText(cultivoGlobal?.Nombre)
            viewDialog?.txtDetalleTipoProductoSelected?.setText(cultivoGlobal?.Nombre_Detalle_Tipo_Producto)
        }
        //UPDATE
        else {
            isFoto = true
            viewDialog?.txtTitle?.setText(getString(R.string.title_editar_producto))


            if(producto.blobImagen!=null){
                val byte = producto.blobImagen?.getBlob()
                val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte!!.size)
                viewDialog?.product_image?.setImageBitmap(bitmap)
            }else{
                Picasso.get()
                        .load(S3Resources.RootImage+"${producto.Imagen}")
                        .into( viewDialog?.product_image, object : com.squareup.picasso.Callback {
                            override fun onError(e: java.lang.Exception?) {
                                viewDialog?.product_image?.setImageResource(R.drawable.ic_foto_producto)
                                viewDialog?.product_image?.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                //) Toast.makeText(context,"Error foto",Toast.LENGTH_LONG).show()
                            }
                            override fun onSuccess() {
                                // Toast.makeText(context,"Loaded foto",Toast.LENGTH_LONG).show()
                            }
                        })
            }

            viewDialog?.txtUnidadProductivaSelected?.setText(producto.NombreUnidadProductiva)
            viewDialog?.txtLoteSelected?.setText(producto.NombreLote)
            viewDialog?.txtCultivoSelected?.setText(producto.NombreCultivo)
            viewDialog?.txtDetalleTipoProductoSelected?.setText(producto.NombreDetalleTipoProducto)
            viewDialog?.txtDescripcionProducto?.setText(producto.Descripcion)
            viewDialog?.spinnerCalidadProducto?.setText(producto.NombreCalidad)
            viewDialog?.spinnerMonedaPrecio?.setText(producto.NombreUnidadMedidaPrecio)
            viewDialog?.spinnerUnidadMedidaCosecha?.setText(producto.NombreUnidadMedidaCantidad)

            Cultivo_Id=producto.cultivoId
            viewDialog?.txtFechaLimiteDisponibilidad?.setText(producto.getFechaLimiteDisponibilidadFormatApi())

            viewDialog?.txtCantidadProductoDisponible?.setText(String.format(context!!.getString(R.string.price_empty_signe),
                    producto.Stock))


            viewDialog?.txtPrecioProducto?.setText(String.format(context!!.getString(R.string.price_empty_signe),
                    producto.Precio))

            viewDialog?.txtPrecioFormat?.setText(String.format(context!!.getString(R.string.price),
                    producto.Precio))


            calidadProductoGlobal = SQLite.select().from(CalidadProducto::class.java).where(CalidadProducto_Table.Id.eq(producto.CalidadId)).querySingle()
            unidadMedidaPrecioGlobal = SQLite.select().from(Unidad_Medida::class.java).where(Unidad_Medida_Table.Id.eq(producto.Unidad_Medida_Id)).querySingle()
            fechaLimiteDisponibilidad=producto.getFechaLimiteDisponibilidadFormatDate(producto.FechaLimiteDisponibilidad)
        }


        viewDialog?.txtPrecioProducto?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                var precioVenta=  viewDialog?.txtPrecioProducto?.text.toString()?.toDoubleOrNull()

                if(!viewDialog?.txtPrecioProducto?.text.toString().isEmpty()){

                    viewDialog?.txtPrecioFormat?.setText(String.format(context!!.getString(R.string.price),
                            precioVenta))

                }else{
                    viewDialog?.txtPrecioFormat?.setText("")
                }
            }
        })

        val dialog = AlertDialog.Builder(context!!, android.R.style.Theme_Light_NoTitleBar)
                .setView(viewDialog)
                .create()

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        //Hide KeyBoard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show()
        dialog.getWindow().setAttributes(lp)
        _dialogRegisterUpdate = dialog
    }

    override fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?) {
        if (viewDialogFilter != null) {
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(null)
            val unidadProductivaArrayAdapter = ArrayAdapter<Unidad_Productiva>(activity, android.R.layout.simple_list_item_activated_1, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))

                viewDialogFilter?.spinnerCultivo?.setText("")
                viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))

                unidadProductivaGlobal = listUnidadProductiva!![position] as Unidad_Productiva
                presenter?.setListSpinnerLote(unidadProductivaGlobal?.Unidad_Productiva_Id)
            }
            presenter?.setListSpinnerLote(null)
            presenter?.setListSpinnerCultivo(null)
        }
    }

    override fun setListLotes(listLotes: List<Lote>?) {
        viewDialogFilter?.spinnerLote!!.setAdapter(null)
        //viewDialogFilter?.spinnerLote?.setText("")
        //viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))
        val loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_list_item_activated_1, listLotes)
        viewDialogFilter?.spinnerLote!!.setAdapter(loteArrayAdapter)
        viewDialogFilter?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            viewDialogFilter?.spinnerCultivo?.setText("")
            viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
            loteGlobal = listLotes!![position]
            presenter?.setListSpinnerCultivo(loteGlobal?.LoteId)
        }
    }

    override fun setListCultivos(listCultivos: List<Cultivo>?) {
        viewDialogFilter?.spinnerCultivo!!.setAdapter(null)
        //viewDialogFilter?.spinnerCultivo?.setText("")
        //viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
        val cultivoArrayAdapter = ArrayAdapter<Cultivo>(activity, android.R.layout.simple_list_item_activated_1, listCultivos)
        viewDialogFilter?.spinnerCultivo!!.setAdapter(cultivoArrayAdapter)
        viewDialogFilter?.spinnerCultivo!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            cultivoGlobal = listCultivos!![position]
            Cultivo_Id = cultivoGlobal?.CultivoId
        }
    }

    override fun setListMoneda(listMoneda: List<Unidad_Medida>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerMonedaPrecio?.setAdapter(null)
            val monedaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_list_item_activated_1, listMoneda)
            viewDialog?.spinnerMonedaPrecio?.setAdapter(monedaArrayAdapter)
            viewDialog?.spinnerMonedaPrecio?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaPrecioGlobal = listMoneda!![position]
            }
        }
    }


    override fun setListUnidadCantidades(listMoneda: List<Unidad_Medida>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerUnidadMedidaCosecha?.setAdapter(null)
            val monedaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_list_item_activated_1, listMoneda)
            viewDialog?.spinnerUnidadMedidaCosecha?.setAdapter(monedaArrayAdapter)
            viewDialog?.spinnerUnidadMedidaCosecha?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaCantidadGlobal = listMoneda!![position]
            }
        }
    }





    override fun setListCalidad(listCalidadProducto: List<CalidadProducto>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerCalidadProducto?.setAdapter(null)
            val calidadArrayAdapter = ArrayAdapter<CalidadProducto>(activity, android.R.layout.simple_list_item_activated_1, listCalidadProducto)
            viewDialog?.spinnerCalidadProducto?.setAdapter(calidadArrayAdapter)
            viewDialog?.spinnerCalidadProducto?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                calidadProductoGlobal = listCalidadProducto!![position]
            }
        }
    }

    override fun setListProductos(listProductos: List<Producto>) {
        adapter?.clear()
        productosList?.clear()
        adapter?.setItems(listProductos)
        hideProgress()
        setResults(listProductos.size)
    }

    override fun setCultivo(cultivo: Cultivo?) {
        if (cultivoSeletedContainer.visibility == View.GONE) {
            cultivoSeletedContainer.visibility = View.VISIBLE
        }
        txtNombreCultivo.setText(cultivo?.Nombre)
        txtNombreLote.setText(cultivo?.Nombre_Tipo_Producto)



        txtPrecio.setText(cultivo?.getFechaFormat(cultivo?.getFechaDate(cultivo.FechaIncio)))
        txtArea.setText(cultivo?.getFechaFormat(cultivo?.getFechaDate(cultivo.FechaFin)))

    }

    override fun validarCampos(): Boolean {

        var dateNow=Calendar.getInstance().time


        var cancel = false
        var focusView: View? = null

        /*if (isFoto == false) {
            focusView =  viewDialog?.spinnerCalidadProducto
            cancel = true
            onMessageError(R.color.grey_luiyi, getString(R.string.error_image_selected))
        } */
        if (viewDialog?.spinnerCalidadProducto?.text.toString().isEmpty()) {
            viewDialog?.spinnerCalidadProducto?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerCalidadProducto
            cancel = true
        } /*else if (viewDialog?.txtNombreProducto?.text.toString().isEmpty()) {
            viewDialog?.txtNombreProducto?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtNombreProducto
            cancel = true
        } */ else if (viewDialog?.txtFechaLimiteDisponibilidad?.text.toString().isEmpty()) {
            viewDialog?.txtFechaLimiteDisponibilidad?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtFechaLimiteDisponibilidad
            cancel = true
        } else if (viewDialog?.txtPrecioProducto?.text.toString().isEmpty()) {
            viewDialog?.txtPrecioProducto?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtPrecioProducto
            cancel = true
        } else if (viewDialog?.spinnerMonedaPrecio?.text.toString().isEmpty()) {
            viewDialog?.spinnerMonedaPrecio?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerMonedaPrecio
            cancel = true
        } else if (viewDialog?.txtCantidadProductoDisponible?.text.toString().isEmpty()) {
            viewDialog?.txtCantidadProductoDisponible?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtCantidadProductoDisponible
            cancel = true
        } else if (viewDialog?.spinnerUnidadMedidaCosecha?.text.toString().isEmpty()) {
            viewDialog?.spinnerUnidadMedidaCosecha?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerUnidadMedidaCosecha
            cancel = true
        } else if (isFoto == false) {
            onMessageError(R.color.grey_luiyi, getString(R.string.error_image_selected))
            focusView = viewDialog?.spinnerMonedaPrecio
            cancel = true
        }

        else if (fechaLimiteDisponibilidad?.before(dateNow) == true){
            viewDialog?.txtFechaLimiteDisponibilidad?.setError(getString(R.string.error_date))
            focusView = viewDialog?.txtFechaLimiteDisponibilidad
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true
        }
        return false
    }

    override fun validarListasAddProducto(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialogFilter?.spinnerUnidadProductiva?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerUnidadProductiva
            cancel = true
        } else if (viewDialogFilter?.spinnerLote?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerLote?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerLote
            cancel = true
        } else if (viewDialogFilter?.spinnerCultivo?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerCultivo?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerCultivo
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }

    override fun disableInputs() {
        setInputs(false)
    }

    override fun enableInputs() {
        setInputs(true)
    }

    private fun setInputs(b: Boolean) {
        if (viewDialog != null) {
            viewDialog?.product_image?.isEnabled = b
            viewDialog?.product_camera?.isEnabled = b
            viewDialog?.product_gallery?.isEnabled = b
            viewDialog?.product_cancel?.isEnabled = b
            viewDialog?.txtDescripcionProducto?.isEnabled = b
            viewDialog?.spinnerCalidadProducto?.isEnabled = b
            viewDialog?.txtFechaLimiteDisponibilidad?.isEnabled = b
            viewDialog?.txtPrecioProducto?.isEnabled = b
            viewDialog?.spinnerMonedaPrecio?.isEnabled = b
        }
    }

    override fun limpiarCampos() {
        if (viewDialog != null) {
            viewDialog?.product_image?.setImageResource(R.drawable.ic_foto_producto_square)
            viewDialog?.txtDescripcionProducto?.setText("")
            viewDialog?.spinnerCalidadProducto?.setText("")
            viewDialog?.txtFechaLimiteDisponibilidad?.setText("")
            viewDialog?.txtPrecioProducto?.setText("")
            viewDialog?.spinnerMonedaPrecio?.setText("")
        }
    }


    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }


    override fun showProgressHud() {
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white_solid))
                .setDetailsLabel("Guardando Informacion")
        hud?.show()
    }



    override fun hideProgressHud() {
        hud?.dismiss()
    }

    override fun setResults(productos: Int) {
        var results = String.format(getString(R.string.results_global_search),
                productos)
        txtResults.setText(results)
    }

    override fun takePictureWithCamera(fragment: ProductosFragment) {
        //EasyImage.openCamera(fragment, 0)
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, RequestAccessPhoneResources.ACCESS_REQUEST_CAMERA)
    }

    override fun choosePhotoFromGallery(fragment: ProductosFragment) {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, RequestAccessPhoneResources.ACCESS_REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RequestAccessPhoneResources.ACCESS_REQUEST_GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        isFoto = true
                        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                        imageGlobal = convertBitmapToByte(bitmap)
                        //imageGlobalRutaFoto = saveImage(bitmap)
                        //Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                        viewDialog?.product_image?.setImageBitmap(bitmap)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }

            } else if (requestCode == RequestAccessPhoneResources.ACCESS_REQUEST_CAMERA) {
                if (data != null) {
                    isFoto = true
                    val thumbnail = data.extras?.get("data") as Bitmap
                    imageGlobal = convertBitmapToByte(thumbnail)
                    viewDialog?.product_image?.setImageBitmap(thumbnail)
                    //imageGlobalRutaFoto = saveImage(thumbnail)
                    // Toast.makeText(context, thumbnail.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
                Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }

        try {
            val f = File(wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(context,
                    arrayOf(f.path),
                    arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }


    //endregion

    override fun registerProducto() {
        if (presenter?.validarCampos() == true) {
            val producto = Producto()
            producto.ProductoId=0
            producto.CalidadId = calidadProductoGlobal?.Id
            producto.NombreCalidad = calidadProductoGlobal?.Nombre
            //producto.Nombre = viewDialog?.txtNombreProducto?.text.toString()
            producto.Descripcion = viewDialog?.txtDescripcionProducto?.text.toString()
            producto.FechaLimiteDisponibilidad = viewDialog?.txtFechaLimiteDisponibilidad?.text.toString()
            producto.blobImagen = Blob(imageGlobal)
            val stringBuilder = StringBuilder()
            stringBuilder.append("data:image/jpeg;base64,")
            stringBuilder.append(android.util.Base64.encodeToString(imageGlobal, android.util.Base64.NO_WRAP))
            producto.Imagen = stringBuilder.toString()
            producto.Nombre= cultivoGlobal?.Nombre_Detalle_Tipo_Producto
            producto.Precio = viewDialog?.txtPrecioProducto?.text.toString().toDoubleOrNull()
            producto.cultivoId = Cultivo_Id
            producto.Stock=viewDialog?.txtCantidadProductoDisponible?.text.toString().toDoubleOrNull()
            producto.NombreUnidadProductiva = unidadProductivaGlobal?.nombre
            producto.NombreLote = loteGlobal?.Nombre
            producto.NombreCultivo = cultivoGlobal?.Nombre
            producto.NombreDetalleTipoProducto = cultivoGlobal?.Nombre_Detalle_Tipo_Producto
            producto.NombreUnidadMedidaPrecio = viewDialog?.spinnerMonedaPrecio?.text.toString()
            producto.PrecioUnidadMedida = viewDialog?.spinnerMonedaPrecio?.text.toString()
            producto.NombreUnidadMedidaCantidad = viewDialog?.spinnerUnidadMedidaCosecha?.text.toString()
            producto.Unidad_Medida_Id=unidadMedidaCantidadGlobal?.Id

            presenter?.registerProducto(producto, Cultivo_Id!!)
        }
    }

    override fun updateProducto(mProducto: Producto) {
        if (presenter?.validarCampos() == true) {
            //val productoUpdate = Producto()
            //productoUpdate.Id = mProducto.Id
            mProducto.CalidadId = calidadProductoGlobal?.Id
            mProducto.NombreCalidad = calidadProductoGlobal?.Nombre
            //productoUpdate.Nombre = viewDialog?.txtNombreProducto?.text.toString()
            mProducto.Descripcion = viewDialog?.txtDescripcionProducto?.text.toString()
            /*val stringFecha = viewDialog?.txtFechaLimiteDisponibilidad?.text.toString()
            val format = SimpleDateFormat("dd/MM/yyyy")
            val date = format.parse(stringFecha)*/
            mProducto.FechaLimiteDisponibilidad = viewDialog?.txtFechaLimiteDisponibilidad?.text.toString()
            if (imageGlobal != null) {
                mProducto.blobImagen = Blob(imageGlobal)
                val stringBuilder = StringBuilder()
                stringBuilder.append("data:image/jpeg;base64,")
                stringBuilder.append(android.util.Base64.encodeToString(imageGlobal, android.util.Base64.NO_WRAP))
                mProducto.Imagen = stringBuilder.toString()
            } else {
                viewDialog?.product_image?.isDrawingCacheEnabled = true
                val bitmap = viewDialog?.product_image?.getDrawingCache()
                val byte = convertBitmapToByte(bitmap!!)
                mProducto.blobImagen = Blob(byte)
                val stringBuilder = StringBuilder()
                stringBuilder.append("data:image/jpeg;base64,")
                stringBuilder.append(android.util.Base64.encodeToString(byte, android.util.Base64.DEFAULT))
                mProducto.Imagen = stringBuilder.toString()
            }

            //mProducto.NombreCultivo = cultivoGlobal?.Nombre
            mProducto.Stock= viewDialog?.txtCantidadProductoDisponible?.text.toString().toDoubleOrNull()
            mProducto.Precio = viewDialog?.txtPrecioProducto?.text.toString().toDoubleOrNull()
            mProducto.NombreUnidadMedidaPrecio = viewDialog?.spinnerMonedaPrecio?.text.toString()
            mProducto.NombreUnidadMedidaCantidad = viewDialog?.spinnerUnidadMedidaCosecha?.text.toString()
            mProducto.PrecioUnidadMedida = viewDialog?.spinnerMonedaPrecio?.text.toString()
            mProducto.Unidad_Medida_Id=unidadMedidaCantidadGlobal?.Id
            presenter?.updateProducto(mProducto, Cultivo_Id!!)
        }
    }


    override fun requestResponseOK() {
        if (_dialogRegisterUpdate != null) {
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageOk(R.color.colorPrimary, getString(R.string.request_ok));
       /// Snackbar.make(container_fragment, getString(R.string.request_ok), Snackbar.LENGTH_SHORT).show()
    }

    override fun requestResponseError(error: String?) {
        if (_dialogRegisterUpdate != null) {
            _dialogRegisterUpdate?.dismiss()
        }
        Snackbar.make(container_fragment, error.toString(), Snackbar.LENGTH_SHORT).show()
    }

    override fun onMessageOk(colorPrimary: Int, msg: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(viewDialog!!.containerDialogProducto, msg!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, msg: String?) {
        onMessageOk(colorPrimary, msg)
    }


    override fun verificateConnection(): AlertDialog? {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.verificate_conexion))
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.
                OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
        builder.setIcon(R.drawable.ic_productos)
        return builder.show()
    }

    override fun confirmDelete(producto: Producto): AlertDialog? {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation))
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

        })
        builder.setMessage(getString(R.string.title_alert_delete_producto))
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            presenter?.deleteProducto(producto, Cultivo_Id)
        })
        builder.setIcon(R.drawable.ic_plagas)
        dialogo = builder.show()
        return dialogo

    }


    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    //endregion

    //region Métodos
    //Fecha
    private fun updateDate() {
        DatePickerDialog(context, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show()
    }

    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateTime.set(Calendar.YEAR, year)
        dateTime.set(Calendar.MONTH, monthOfYear)
        dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        mostrarResultadosFechaLimiteDisponibilidad()
    }

    private fun mostrarResultadosFechaLimiteDisponibilidad() {
        val format1 = SimpleDateFormat("yyyy-MM-dd")
        val formatted = format1.format(dateTime.time)
        viewDialog?.txtFechaLimiteDisponibilidad?.setText(formatted)


        val format2 = SimpleDateFormat("MM/dd/yyyy")
        val formatted2 = format1.format(dateTime.time)
        fechaLimiteDisponibilidad=dateTime.time


    }


    //endregion


    //region PERMISSIONS

    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun requestPermission() {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_ALL)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doPermissionGrantedStuffs()
            } else {
                Toast.makeText(activity,
                        "Permiso denegado", Toast.LENGTH_LONG).show()

            }
        }
    }


    fun doPermissionGrantedStuffs(): Boolean {
        /// String SIMSerialNumber=tm.getSimSerialNumber();
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(activity!!, permission) != PackageManager.PERMISSION_GRANTED) {
                val response = false
                return response
            }
        }
        val response = true
        return response
    }

    //endregion

    //region OVERRIDE METHODS
    override fun onRefresh() {
        showProgress()
        presenter?.getListProductos(Cultivo_Id)
    }

    override fun onDestroy() {
        EasyImage.clearConfiguration(activity);
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause(activity!!.applicationContext)
    }

    override fun onResume() {
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }

    //endregion
}
