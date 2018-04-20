package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.UnidadProductiva


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.Ciudad
import com.interedes.agriculturappv3.productor.models.Departamento
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.up.adapter.UnidadProductivaAdapter
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_form_unidad_productiva.view.*
import kotlinx.android.synthetic.main.fragment_unidad_productiva.*
import android.support.v7.widget.RecyclerView
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
        } else if (viewDialog?.spinnerDepartamento?.text.toString().isEmpty()) {
            viewDialog?.spinnerDepartamento?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerDepartamento
            cancel = true
        } else if (viewDialog?.spinnerMunicipio?.text.toString().isEmpty()) {
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

    override fun updateUp() {
        if (presenter?.validarCampos() == true) {
            val updateUP = UnidadProductiva()
            val id_user_logued = (activity as MenuMainActivity).getLastUserLogued()?.Id
            updateUP.UsuarioId = id_user_logued
            updateUP.Id = unidadProductivaGlobal!!.Id
            updateUP.CiudadId = municipioGlobal?.Id
            updateUP.Nombre_Ciudad = municipioGlobal?.Nombre
            updateUP.Nombre_Departamento = departamentoGlobal?.Nombre
            updateUP.nombre = viewDialog?.edtNombreUnidadProductiva?.text.toString()
            updateUP.descripcion = viewDialog?.etDescripcionUnidadProductiva?.text.toString()
            updateUP.Area = viewDialog?.edtAreaUnidadProductiva?.text.toString().toDoubleOrNull()
            updateUP.Coordenadas = viewDialog?.edtLocalizacionUnidadProductiva?.text.toString()
            updateUP.UnidadMedidaId = unidadMedidaGlobal?.Id
            updateUP.Nombre_Unidad_Medida = unidadMedidaGlobal?.Descripcion
            updateUP.Configuration_Point = unidadProductivaGlobal!!.Configuration_Point
            updateUP.Configuration_Poligon = unidadProductivaGlobal!!.Configuration_Poligon
            updateUP.Latitud = latitud
            updateUP.Longitud = longitud
            presenter?.updateUP(updateUP)
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
            unidadMedidaGlobal = Unidad_Medida(unidadProductiva.UnidadMedidaId, unidadProductiva.Nombre_Unidad_Medida, null)
            viewDialog?.spinnerDepartamento?.setText(unidadProductiva.Nombre_Departamento)
            viewDialog?.spinnerMunicipio?.visibility = View.VISIBLE
            viewDialog?.spinnerMunicipio?.setText(unidadProductiva.Nombre_Ciudad)
            viewDialog?.edtNombreUnidadProductiva?.setText(unidadProductiva.nombre)
            viewDialog?.etDescripcionUnidadProductiva?.setText(unidadProductiva.descripcion)
            viewDialog?.edtAreaUnidadProductiva?.setText(unidadProductiva.Area.toString())
            viewDialog?.edtLocalizacionUnidadProductiva?.setText(unidadProductiva.Coordenadas)
            viewDialog?.spinnerUnidadMedidaUp?.setText(unidadProductiva.Nombre_Unidad_Medida)
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
                showAlertDialogAddUnidadProductiva(null)
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
                    updateUp()
                } else {
                    registerUp()
                }
            }
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