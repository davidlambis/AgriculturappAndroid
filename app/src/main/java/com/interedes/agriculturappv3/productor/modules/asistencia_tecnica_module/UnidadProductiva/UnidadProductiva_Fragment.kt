package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva


import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.departments.Ciudad
import com.interedes.agriculturappv3.productor.models.departments.Departamento
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.up.adapter.UnidadProductivaAdapter
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_form_unidad_productiva.view.*
import kotlinx.android.synthetic.main.fragment_unidad_productiva.*
import android.widget.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo


/**
 * Created by usuario on 16/03/2018.
 */
class UnidadProductiva_Fragment : Fragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, IUnidadProductiva.View {


    var presenter: IUnidadProductiva.Presenter? = null
    var adapter: UnidadProductivaAdapter? = null
    var unidadProductivaList: ArrayList<UnidadProductiva>? = ArrayList<UnidadProductiva>()
    var unidadProductivaGlobal: UnidadProductiva? = null
    //Progress
    private var hud: KProgressHUD? = null
    //Dialog
    var viewDialog: View? = null;
    var _dialogRegisterUpdate: AlertDialog? = null
    var departamentoGlobal: Departamento? = null
    var municipioGlobal: Ciudad? = null

    //Globals
    var unidadMedidaGlobal: Unidad_Medida? = null
    var listUnidadMedidaGlobal: List<Unidad_Medida>? = java.util.ArrayList<Unidad_Medida>()
    var listMunicipios: List<Ciudad>? = java.util.ArrayList<Ciudad>()
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    //PERMISOS
    private val PERMISSION_REQUEST_CODE = 1
    internal var PERMISSION_ALL = 1
    internal var PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)


    companion object {
        var instance: UnidadProductiva_Fragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UnidadProductiva_Fragment.instance = this
        presenter = UpPresenter(this);
        presenter?.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_unidad_productiva, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _dialogRegisterUpdate?.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_up)
        fabAddUnidadProductiva.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        ivBackButton.setOnClickListener(this)
        setupInjection()

        YoYo.with(Techniques.Pulse)
                .repeat(5)
                .playOn(fabAddUnidadProductiva)

    }

    fun setupInjection() {
        presenter?.getUps()
    }


    private fun initAdapter() {
        //recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = UnidadProductivaAdapter(unidadProductivaList!!)
        recyclerView?.adapter = adapter
    }


    private fun hideKeyboard() {
        var inputManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(activity?.getCurrentFocus()?.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    //region IMPLEMETS INTERFACE MAIN

    override fun validarCampos(): Boolean {
        var cancel = false
        var focusView: View? = null
        /*if (viewDialog?.spinnerUnidadProductiva?.text.toString().isEmpty() && viewDialog?.spinnerUnidadProductiva?.visibility==View.VISIBLE) {
            viewDialog?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.spinnerUnidadProductiva
            cancel = true
        }*/
        if (viewDialog?.edtNombreUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.edtNombreUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtNombreUnidadProductiva
            cancel = true
        } else if (viewDialog?.spinnerDepartamento?.text.toString().isEmpty() && viewDialog?.spinnerDepartamento?.visibility == View.VISIBLE) {
            viewDialog?.spinnerDepartamento?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerDepartamento
            cancel = true
        } else if (viewDialog?.spinnerMunicipio?.text.toString().isEmpty() && viewDialog?.spinnerMunicipio?.visibility == View.VISIBLE) {
            viewDialog?.spinnerMunicipio?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerMunicipio
            cancel = true
        } else if (viewDialog?.etDescripcionUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.etDescripcionUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.etDescripcionUnidadProductiva
            cancel = true
        } else if (viewDialog?.edtAreaUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.edtAreaUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtAreaUnidadProductiva
            cancel = true
        } else if (viewDialog?.spinnerUnidadMedidaUp?.text.toString().isEmpty()) {
            viewDialog?.spinnerUnidadMedidaUp?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerUnidadMedidaUp
            cancel = true
        } else if (viewDialog?.edtLocalizacionUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.edtLocalizacionUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtLocalizacionUnidadProductiva
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }

    override fun limpiarCampos() {
        if (viewDialog != null) {
            viewDialog?.edtNombreUnidadProductiva?.setText("");
            viewDialog?.etDescripcionUnidadProductiva?.setText("");
            viewDialog?.edtAreaUnidadProductiva?.setText("");
            viewDialog?.edtLocalizacionUnidadProductiva?.setText("");
        }
    }

    override fun disableInputs() {
        setInputs(false)
    }

    override fun enableInputs() {
        setInputs(true)
    }

    private fun setInputs(b: Boolean) {
        if (viewDialog != null) {
            viewDialog?.edtNombreUnidadProductiva?.isEnabled = b
            viewDialog?.etDescripcionUnidadProductiva?.isEnabled = b
            viewDialog?.edtAreaUnidadProductiva?.isEnabled = b
        }
    }

    override fun showProgress() {
        showProgressHud()
        // progressBar?.visibility= View.VISIBLE;
        swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        hideProgressHud()
        //progressBar?.visibility= View.GONE;
        swipeRefreshLayout.setRefreshing(false);
    }

    override fun hideElements() {

    }

    override fun setListUps(listUnidadProductivas: List<UnidadProductiva>) {
        adapter?.clear()
        unidadProductivaList?.clear()
        adapter?.setItems(listUnidadProductivas)
        hideProgress()
        setResults(listUnidadProductivas.size)
    }

    override fun setResults(unidadesProductivas: Int) {
        var results = String.format(getString(R.string.results_global_search),
                unidadesProductivas);
        txtResults.setText(results);
    }

    override fun requestResponseOK() {
        if (_dialogRegisterUpdate != null) {
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageOk(R.color.colorPrimary, getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        if (_dialogRegisterUpdate != null) {
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageError(R.color.grey_luiyi, error)
    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container_unidad_productiva, message!!, Snackbar.LENGTH_LONG)
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

    override fun registerUp() {
        if (presenter?.validarCampos() == true) {
            val unidadProductiva = UnidadProductiva()
            val id_user_logued = (activity as MenuMainActivity).getLastUserLogued()?.Id
            unidadProductiva.UsuarioId = id_user_logued
            unidadProductiva.CiudadId = municipioGlobal?.Id
            unidadProductiva.Nombre_Ciudad = municipioGlobal?.Nombre
            unidadProductiva.Nombre_Departamento = departamentoGlobal?.Nombre
            unidadProductiva.nombre = viewDialog?.edtNombreUnidadProductiva?.text.toString()
            unidadProductiva.descripcion = viewDialog?.etDescripcionUnidadProductiva?.text.toString()
            unidadProductiva.Area = viewDialog?.edtAreaUnidadProductiva?.text.toString().toDoubleOrNull()
            unidadProductiva.Coordenadas = viewDialog?.edtLocalizacionUnidadProductiva?.text.toString()
            unidadProductiva.UnidadMedidaId = unidadMedidaGlobal?.Id
            unidadProductiva.Nombre_Unidad_Medida = unidadMedidaGlobal?.Descripcion
            unidadProductiva.Configuration_Point = true
            unidadProductiva.Configuration_Poligon = false
            unidadProductiva.Latitud = latitud
            unidadProductiva.Longitud = longitud
            presenter?.registerUP(unidadProductiva)
        }
    }

    override fun updateUp(unidadProductiva: UnidadProductiva?) {
        if (presenter?.validarCampos() == true) {
            //val updateUP = UnidadProductiva()
            /* val id_user_logued = (activity as MenuMainActivity).getLastUserLogued()?.Id
             unidadProductiva?.UsuarioId = id_user_logued*/
            unidadProductiva?.Id = unidadProductivaGlobal!!.Id
            /* unidadProductiva?.CiudadId = municipioGlobal?.Id
             unidadProductiva?.Nombre_Ciudad = municipioGlobal?.Nombre
             unidadProductiva?.Nombre_Departamento = departamentoGlobal?.Nombre*/
            unidadProductiva?.nombre = viewDialog?.edtNombreUnidadProductiva?.text.toString()
            unidadProductiva?.descripcion = viewDialog?.etDescripcionUnidadProductiva?.text.toString()
            unidadProductiva?.Area = viewDialog?.edtAreaUnidadProductiva?.text.toString().toDoubleOrNull()
            unidadProductiva?.Coordenadas = viewDialog?.edtLocalizacionUnidadProductiva?.text.toString()
            unidadProductiva?.UnidadMedidaId = unidadMedidaGlobal?.Id
            unidadProductiva?.Nombre_Unidad_Medida = unidadMedidaGlobal?.Descripcion
            unidadProductiva?.Configuration_Point = unidadProductivaGlobal!!.Configuration_Point
            unidadProductiva?.Configuration_Poligon = unidadProductivaGlobal!!.Configuration_Poligon
            unidadProductiva?.Latitud = latitud
            unidadProductiva?.Longitud = longitud
            presenter?.updateUP(unidadProductiva)
        }
    }

    override fun setListSpinnerDepartamentos(listDepartamentos: List<Departamento>) {
        if (viewDialog != null) {
            viewDialog?.spinnerDepartamento?.setAdapter(null)
            val departamentoArrayAdapter = ArrayAdapter<Departamento>(activity, android.R.layout.simple_spinner_dropdown_item, listDepartamentos)
            viewDialog?.spinnerDepartamento?.setAdapter(departamentoArrayAdapter)
            viewDialog?.spinnerDepartamento?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialog?.spinnerMunicipio?.setText("")
                departamentoGlobal = listDepartamentos[position]
                presenter?.setListMunicipios(departamentoGlobal?.Id)
            }
        }
    }

    override fun setListSpinnerMunicipios(listMunicipios: List<Ciudad>) {
        if (viewDialog != null) {
            viewDialog?.spinnerMunicipio?.setAdapter(null)
            val municipioArrayAdapter = ArrayAdapter<Ciudad>(activity, android.R.layout.simple_spinner_dropdown_item, listMunicipios)
            viewDialog?.spinnerMunicipio?.setAdapter(municipioArrayAdapter)
            viewDialog?.spinnerMunicipio?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                municipioGlobal = listMunicipios[position]
            }
        }
    }

    override fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>) {
        listUnidadMedidaGlobal = listUnidadMedida
    }


    override fun setListUnidadMedidaAdapterSpinner() {
        if (viewDialog != null) {
            ///Adapaters
            viewDialog?.spinnerUnidadMedidaUp!!.setAdapter(null)
            val uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedidaGlobal);
            viewDialog?.spinnerUnidadMedidaUp!!.setAdapter(uMedidaArrayAdapter);
            viewDialog?.spinnerUnidadMedidaUp!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaGlobal = listUnidadMedidaGlobal!![position] as Unidad_Medida
                //Toast.makeText(activity,""+ unidadMedidaGlobal!!.Id.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun showProgressHud() {
        var imageView = ImageView(activity);
        imageView.setBackgroundResource(R.drawable.spin_animation);
        var drawable = imageView.getBackground() as AnimationDrawable;
        drawable.start();
        hud = KProgressHUD.create(activity)
                .setCustomView(imageView)
                .setWindowColor(resources.getColor(R.color.white))
                .setLabel("Cargando...", resources.getColor(R.color.grey_luiyi));
        hud?.show()
    }

    override fun hideProgressHud() {
        hud?.dismiss()
    }


    override fun showAlertDialogAddUnidadProductiva(unidadProductiva: UnidadProductiva?) {

        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_unidad_productiva, null)
        setListSpinnerMunicipios(listMunicipios!!)
        presenter?.setListDepartamentos()
        setListUnidadMedidaAdapterSpinner()
        val imageViewLocalizarUnidadProductiva = viewDialog?.imageViewLocalizarUnidadProductiva
        val btnCloseDialog = viewDialog?.ivClosetDialogUp
        unidadProductivaGlobal = unidadProductiva


        //REGISTER
        if (unidadProductiva == null) {
            viewDialog?.txtTitle?.setText(getString(R.string.tittle_add_unidadproductiva))
        }
        //UPDATE
        else {
            viewDialog?.txtTitle?.setText(getString(R.string.tittle_edit_unidadproductiva))
            unidadMedidaGlobal = Unidad_Medida(unidadProductivaGlobal?.UnidadMedidaId, unidadProductivaGlobal?.Nombre_Unidad_Medida, null)
            // viewDialog?.spinnerDepartamento?.setText(unidadProductivaGlobal?.Nombre_Departamento)
            viewDialog?.spinnerDepartamento?.visibility = View.GONE
            viewDialog?.spinnerMunicipio?.visibility = View.GONE
            //viewDialog?.spinnerMunicipio?.setText(unidadProductivaGlobal?.Nombre_Ciudad)
            viewDialog?.edtNombreUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialog?.etDescripcionUnidadProductiva?.setText(unidadProductivaGlobal?.descripcion)
            viewDialog?.edtAreaUnidadProductiva?.setText(unidadProductivaGlobal?.Area.toString())
            viewDialog?.edtLocalizacionUnidadProductiva?.setText(unidadProductivaGlobal?.Coordenadas)
            viewDialog?.spinnerUnidadMedidaUp?.setText(unidadProductivaGlobal?.Nombre_Unidad_Medida)
            latitud = unidadProductiva.Latitud!!
            longitud = unidadProductiva.Longitud!!

        }


        //Set Events
        btnCloseDialog?.setOnClickListener(this)
        imageViewLocalizarUnidadProductiva?.setOnClickListener(this)
        viewDialog?.btnSaveUnidadProductiva?.setOnClickListener(this)


        val dialog = AlertDialog.Builder(context!!, android.R.style.Theme_Light_NoTitleBar)
                .setView(viewDialog)
                .create()


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow()?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.getWindow()?.setAttributes(lp)
        //Hide KeyBoard
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show()
        _dialogRegisterUpdate = dialog

        /*
        val dialog = AlertDialog.Builder(context!!)
                .setView(viewDialog)
                .setIcon(R.drawable.ic_lote)
                . setTitle(getString(R.string.tittle_add_unidadproductiva))
                .setPositiveButton(getString(R.string.btn_save), null) //Set to null. We override the onclick
                .setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
                })
                .create()
        dialog.setOnShowListener(DialogInterface.OnShowListener {
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                // TODO Do something
                if(unidadProductiva!=null){
                    updateUp()
                }else{
                    registerUp()
                }
                //Dismiss once everything is OK.
                //dialog.dismiss()
            }
        })
        dialog?.show()
        _dialogRegisterUpdate=dialog
        */
        //return  _dialogRegisterUpdate
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


    //endregion

    //region Events
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAddUnidadProductiva -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(context, *PERMISSIONS)) {
                        requestPermission()
                    } else {
                        showAlertDialogAddUnidadProductiva(null)
                    }
                } else {
                    showAlertDialogAddUnidadProductiva(null)
                }


            }
            R.id.ivClosetDialogUp -> _dialogRegisterUpdate?.dismiss()

            R.id.imageViewLocalizarUnidadProductiva -> {
                presenter?.startGps(activity as MenuMainActivity)
            }

            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }

            R.id.btnSaveUnidadProductiva -> {
                if (unidadProductivaGlobal != null) {
                    updateUp(unidadProductivaGlobal)
                } else {
                    registerUp()
                }
            }
        }
    }

    //PERMISOS
    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun requestPermission() {

        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        //ContextCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_ALL)
        requestPermissions(PERMISSIONS, PERMISSION_ALL)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showAlertDialogAddUnidadProductiva(null)
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(activity,
                                "Permiso denegado", Toast.LENGTH_LONG).show()
                    } else {
                        if (hasPermissions(context, *PERMISSIONS)) {
                            showAlertDialogAddUnidadProductiva(null)
                        } else {
                            val builder = AlertDialog.Builder(context!!)
                            builder.setMessage(R.string.enable_permissions_gps_settings)
                                    .setPositiveButton(R.string.confirm) { dialog, id ->
                                        val intent = Intent()
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        val uri = Uri.fromParts("package", "com.interedes.agriculturappv3", null)
                                        intent.setData(uri)
                                        startActivity(intent)
                                    }
                            builder.setTitle(R.string.gps_disabled)
                            builder.setIcon(R.drawable.logo_agr_app)
                            // Create the AlertDialog object and return it
                            builder.show()


                        }
                    }
                }


        /*for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                Toast.makeText(activity,
                        "Permiso denegado", Toast.LENGTH_LONG).show()
            } else {
                if (ActivityCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED) {
                    showAlertDialogAddUnidadProductiva(null)
                } else {
                    val builder = AlertDialog.Builder(context!!)
                    builder.setMessage(R.string.please_enable_gps)
                            .setPositiveButton(R.string.confirm) { dialog, id ->
                                val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                                context!!.startActivity(settingsIntent)
                            }
                    builder.setTitle(R.string.gps_disabled)
                    builder.setIcon(R.drawable.logo_agr_app)
                }
            }
        }*/

        /*if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showAlertDialogAddUnidadProductiva(null)
        } else if (!ActivityCompat.checkSelfPermission(context!!, permission) == PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(context!!)
            builder.setMessage(R.string.please_enable_gps)
                    .setPositiveButton(R.string.confirm) { dialog, id ->
                        val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context!!.startActivity(settingsIntent)
                    }
            builder.setTitle(R.string.gps_disabled)
            builder.setIcon(R.drawable.logo_agr_app)

        } else {
            Toast.makeText(activity,
                    "Permiso denegado", Toast.LENGTH_LONG).show()

        }*//* else if ((Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) ||
                        (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[1]))) {
                    //Toast.makeText(MainActivity.this, "Go to Settings and Grant the permission to use this feature.", Toast.LENGTH_SHORT).show();
                    // User selected the Never Ask Again Option
                    Intent i = new Intent();
                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.addCategory(Intent.CATEGORY_DEFAULT);
                    i.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    getApplicationContext().startActivity(i);

                } */
        }
    }

    //Escuchador de eventos
    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
            if (extras.containsKey("latitud") && extras.containsKey("longitud")) {
                latitud = intent.extras!!.getDouble("latitud")
                longitud = intent.extras!!.getDouble("longitud")
                hideProgressHud()
                if (viewDialog != null) {
                    viewDialog?.edtLocalizacionUnidadProductiva?.setText(String.format(getString(R.string.coords), latitud, longitud))
                }
            }
        }
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
        presenter?.getUps()
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