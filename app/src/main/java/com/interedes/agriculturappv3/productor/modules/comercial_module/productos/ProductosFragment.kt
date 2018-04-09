package com.interedes.agriculturappv3.productor.modules.comercial_module.productos


import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.Lote
import com.interedes.agriculturappv3.productor.models.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.producto.CalidadProducto
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.adapters.ProductosAdapter
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.raizlabs.android.dbflow.data.Blob
import com.raizlabs.android.dbflow.kotlinextensions.delete
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
    var unidadProductivaGlobal: UnidadProductiva? = null
    var loteGlobal: Lote? = null

    var Cultivo_Id: Long? = null

    var adapter: ProductosAdapter? = null

    var productosList: ArrayList<Producto>? = ArrayList<Producto>()
    var viewDialog: View? = null

    var monedaGlobal: Unidad_Medida? = null
    var calidadProductoGlobal: CalidadProducto? = null
    var productoGlobal: Producto? = null
    var _dialogRegisterUpdate: AlertDialog? = null
    var dateTime = Calendar.getInstance()
    var fechaLimiteDisponibilidad: Date? = null
    var dialogo: AlertDialog? = null

    //CÁMARA
    val IMAGE_DIRECTORY = "/Productos"
    val REQUEST_GALLERY = 1
    val REQUEST_CAMERA = 2

    var PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    var imageGlobal: ByteArray? = null

    var isFoto: Boolean? = false

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

    }

    //region ADAPTER
    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
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
                if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, PERMISSIONS, 1000)
                    return
                }
                takePictureWithCamera(this)


            }
            R.id.product_gallery -> {
                if (ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, PERMISSIONS, 1000)
                    return
                }
                choosePhotoFromGallery(this)
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
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.Id)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.Nombre)
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
            viewDialog?.txtUnidadProductivaSelected?.setText(unidadProductivaGlobal?.Nombre)
            viewDialog?.txtLoteSelected?.setText(loteGlobal?.Nombre)
            viewDialog?.txtCultivoSelected?.setText(cultivoGlobal?.Nombre)
            viewDialog?.txtDetalleTipoProductoSelected?.setText(cultivoGlobal?.Nombre_Detalle_Tipo_Producto)
        }
        //UPDATE
        else {
            viewDialog?.txtTitle?.setText(getString(R.string.title_editar_producto))
            val byte = producto.Imagen?.getBlob()
            val bitmap = BitmapFactory.decodeByteArray(byte, 0, byte!!.size)
            viewDialog?.product_image?.setImageBitmap(bitmap)
            viewDialog?.txtUnidadProductivaSelected?.setText(producto.NombreUnidadProductiva)
            viewDialog?.txtLoteSelected?.setText(producto.NombreLote)
            viewDialog?.txtCultivoSelected?.setText(producto.NombreCultivo)
            viewDialog?.txtDetalleTipoProductoSelected?.setText(producto.NombreDetalleTipoProducto)
            viewDialog?.txtDescripcionProducto?.setText(producto.Descripcion)
            viewDialog?.spinnerCalidadProducto?.setText(producto.NombreCalidad)
            viewDialog?.spinnerMonedaPrecio?.setText(producto.NombreUnidadMedida)
            viewDialog?.txtFechaLimiteDisponibilidad?.setText(producto.getFechaLimiteDisponibilidadFormat())
            viewDialog?.txtPrecioProducto?.setText(producto.Precio.toString())

        }

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

    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?) {
        if (viewDialogFilter != null) {
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(null)
            val unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))

                viewDialogFilter?.spinnerCultivo?.setText("")
                viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))

                unidadProductivaGlobal = listUnidadProductiva!![position] as UnidadProductiva
                presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            }
            presenter?.setListSpinnerLote(null)
            presenter?.setListSpinnerCultivo(null)
        }
    }

    override fun setListLotes(listLotes: List<Lote>?) {
        viewDialogFilter?.spinnerLote!!.setAdapter(null)
        //viewDialogFilter?.spinnerLote?.setText("")
        //viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))
        val loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, listLotes)
        viewDialogFilter?.spinnerLote!!.setAdapter(loteArrayAdapter)
        viewDialogFilter?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            viewDialogFilter?.spinnerCultivo?.setText("")
            viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
            loteGlobal = listLotes!![position]
            presenter?.setListSpinnerCultivo(loteGlobal?.Id)
        }
    }

    override fun setListCultivos(listCultivos: List<Cultivo>?) {
        viewDialogFilter?.spinnerCultivo!!.setAdapter(null)
        //viewDialogFilter?.spinnerCultivo?.setText("")
        //viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
        val cultivoArrayAdapter = ArrayAdapter<Cultivo>(activity, android.R.layout.simple_spinner_dropdown_item, listCultivos)
        viewDialogFilter?.spinnerCultivo!!.setAdapter(cultivoArrayAdapter)
        viewDialogFilter?.spinnerCultivo!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            cultivoGlobal = listCultivos!![position]
            Cultivo_Id = cultivoGlobal?.Id
        }
    }

    override fun setListMoneda(listMoneda: List<Unidad_Medida>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerMonedaPrecio?.setAdapter(null)
            val monedaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listMoneda)
            viewDialog?.spinnerMonedaPrecio?.setAdapter(monedaArrayAdapter)
            viewDialog?.spinnerMonedaPrecio?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                monedaGlobal = listMoneda!![position]
            }
        }
    }

    override fun setListCalidad(listCalidadProducto: List<CalidadProducto>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerCalidadProducto?.setAdapter(null)
            val calidadArrayAdapter = ArrayAdapter<CalidadProducto>(activity, android.R.layout.simple_spinner_dropdown_item, listCalidadProducto)
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
        txtNombreTipoProducto.setText(cultivo?.Nombre_Tipo_Producto)
        txtFechaInicioCultivo.setText(cultivo?.FechaIncio)
        txtFechaFinCultivo.setText(cultivo?.FechaFin)

    }

    override fun validarCampos(): Boolean {
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
        } else if (viewDialog?.txtFechaLimiteDisponibilidad?.text.toString().isEmpty()) {
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
        } else if (isFoto == false) {
            onMessageError(R.color.grey_luiyi, getString(R.string.error_image_selected))
            focusView = viewDialog?.spinnerMonedaPrecio
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

    override fun showDialogProgress() {
        if (viewDialog != null) {
            viewDialog?.progressBarProducto?.visibility = View.VISIBLE
        }
    }

    override fun hideDialogProgress() {
        if (viewDialog != null) {
            viewDialog?.progressBarProducto?.visibility = View.GONE
        }
    }

    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun setResults(productos: Int) {
        var results = String.format(getString(R.string.results_global_search),
                productos)
        txtResults.setText(results)
    }


    /*
    fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
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
        ActivityCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_REQUEST_CODE)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (TAKE_CAMERA == true) {
                    takePictureWithCamera(this)
                } else if (CHOOSE_FROM_GALLERY == true) {
                    choosePhotoFromGallery(this)
                }
            } else {
                Toast.makeText(activity,
                        "Permiso denegado", Toast.LENGTH_LONG).show()
            }
        }
    }*/

    override fun takePictureWithCamera(fragment: ProductosFragment) {
        //EasyImage.openCamera(fragment, 0)
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun choosePhotoFromGallery(fragment: ProductosFragment) {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        isFoto = true
                        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, contentURI)
                        imageGlobal = convertBitmapToByte(bitmap)
                        //saveImage(bitmap)
                        //Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show()
                        viewDialog?.product_image?.setImageBitmap(bitmap)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }

            } else if (requestCode == REQUEST_CAMERA) {
                if (data != null) {
                    isFoto = true
                    val thumbnail = data.extras?.get("data") as Bitmap
                    imageGlobal = convertBitmapToByte(thumbnail)
                    viewDialog?.product_image?.setImageBitmap(thumbnail)
                    // saveImage(thumbnail)
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

    /*
     private void tomarFoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = Uri.fromFile(getOutputMediaFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(intent, 100);
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Datatakeh");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Datatakeh", "failed to create directory");
                return null;
            }
        }
        if (foto1 == true) {
            return new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_POSTE_" + "Lat:" + latitud + "_Lng:" + longitud + "_FOTO_1" + ".jpg");
        }
        if (foto2 == true) {
            return new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_POSTE_" + "Lat:" + latitud + "_Lng:" + longitud + "_FOTO_2" + ".jpg");
        } else {
            return null;
        }
    }
    */


    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CAMERA ->{
                    val image : Bitmap = data?.extras?.get("data") as Bitmap
                    product_image.setImageBitmap(image)
                }
            }
        }else{
            return
        }



        /*EasyImage.handleActivityResult(requestCode, resultCode, data, activity, object : DefaultCallback() {
            override fun onImagePicked(imageFile: File?, source: EasyImage.ImageSource?, type: Int) {
                val extras = data?.extras
                val image: Bitmap = extras?.get("data") as Bitmap
                product_image?.setImageBitmap(image)
                /*try {
                    /*Log.d("Path",imageFile?.absolutePath)
                    //val image: Bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath)
                    val os: OutputStream = BufferedOutputStream(FileOutputStream(imageFile))
                    //image.compress(Bitmap.CompressFormat.JPEG, 100, os)*/
                    val uri = Uri.fromFile(imageFile)

                    /* val from = imageFile
                     val to = File(Environment.getExternalStorageDirectory().absolutePath + "/imagen.jpg")
                     from?.renameTo(to)
                     val options = BitmapFactory.Options()
                     options.inPreferredConfig = Bitmap.Config.ARGB_8888
                     val image: Bitmap = BitmapFactory.decodeFile(to.absolutePath, options)
                     var compressedImage: Bitmap = Compressor(activity)
                             .setMaxHeight(400)
                             .setQuality(100)
                             .compressToBitmap(imageFile)*/

                     //compressedImage = convertBitmapToByte(compressedImage)
                     product_image.setImageURI(uri)

                } catch (e: IOException) {
                    e.printStackTrace()
                }*/
            }

            override fun onImagePickerError(e: Exception?, source: EasyImage.ImageSource?, type: Int) {
                super.onImagePickerError(e, source, type)
                onMessageError(R.color.grey_luiyi, getString(R.string.error_take_picture_camera))
            }

            override fun onCanceled(source: EasyImage.ImageSource?, type: Int) {
                super.onCanceled(source, type)
                if (source === EasyImage.ImageSource.CAMERA) {
                    val photoFile = EasyImage.lastlyTakenButCanceledPhoto(activity)
                    photoFile?.delete()
                }
            }

        })*/


    }*/

    fun convertBitmapToByte(bitmapCompressed: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        return stream.toByteArray()
        //return BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.size)
    }


    //endregion

    override fun registerProducto() {
        if (presenter?.validarCampos() == true) {
            val producto = Producto()
            producto.CalidadId = calidadProductoGlobal?.Id
            producto.NombreCalidad = calidadProductoGlobal?.Nombre
            producto.Descripcion = viewDialog?.txtDescripcionProducto?.text.toString()
            producto.FechaLimiteDisponibilidad = fechaLimiteDisponibilidad
            producto.Imagen = Blob(imageGlobal)
            producto.Precio = viewDialog?.txtPrecioProducto?.text.toString().toDoubleOrNull()
            producto.CultivoId = Cultivo_Id
            producto.NombreUnidadProductiva = unidadProductivaGlobal?.Nombre
            producto.NombreLote = loteGlobal?.Nombre
            producto.NombreCultivo = cultivoGlobal?.Nombre
            producto.NombreDetalleTipoProducto = cultivoGlobal?.Nombre_Detalle_Tipo_Producto
            producto.NombreUnidadMedida = viewDialog?.spinnerMonedaPrecio?.text.toString()
            presenter?.registerProducto(producto, Cultivo_Id!!)
        }
    }

    override fun updateProducto(producto: Producto) {
        if (presenter?.validarCampos() == true) {
            val productoUpdate = Producto()
            productoUpdate.Id = producto.Id
            productoUpdate.Descripcion = viewDialog?.txtDescripcionProducto?.text.toString()
            productoUpdate.FechaLimiteDisponibilidad = fechaLimiteDisponibilidad
            productoUpdate.Imagen = Blob(imageGlobal)
            productoUpdate.Precio = viewDialog?.txtPrecioProducto?.text.toString().toDoubleOrNull()
            productoUpdate.CultivoId = Cultivo_Id
            productoUpdate.NombreUnidadProductiva = unidadProductivaGlobal?.Nombre
            productoUpdate.NombreLote = loteGlobal?.Nombre
            productoUpdate.NombreCultivo = cultivoGlobal?.Nombre
            productoUpdate.NombreDetalleTipoProducto = cultivoGlobal?.Nombre_Detalle_Tipo_Producto
            presenter?.registerProducto(productoUpdate, Cultivo_Id!!)
        }
    }


    override fun requestResponseOK() {
        if (_dialogRegisterUpdate != null) {
            _dialogRegisterUpdate?.dismiss()
        }
        Snackbar.make(container_fragment, getString(R.string.request_ok), Snackbar.LENGTH_SHORT).show()
    }

    override fun requestResponseError(error: String?) {

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
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
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
        val format1 = SimpleDateFormat("dd/MM/yyyy")
        val formatted = format1.format(dateTime.time)
        fechaLimiteDisponibilidad = dateTime.time
        viewDialog?.txtFechaLimiteDisponibilidad?.setText(formatted)
    }
    //endregion

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
}
