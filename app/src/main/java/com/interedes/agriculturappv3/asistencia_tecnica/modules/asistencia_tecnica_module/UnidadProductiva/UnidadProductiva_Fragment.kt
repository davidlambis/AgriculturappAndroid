package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.UnidadProductiva


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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.up.adapter.UnidadProductivaAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_form_unidad_productiva.view.*
import kotlinx.android.synthetic.main.fragment_unidad_productiva.*
import kotlinx.android.synthetic.main.map_layout.*

/**
 * Created by usuario on 16/03/2018.
 */
class UnidadProductiva_Fragment: Fragment(), View.OnClickListener , SwipeRefreshLayout.OnRefreshListener, IUnidadProductiva.View {


    var presenter: IUnidadProductiva.Presenter? = null
    var adapter: UnidadProductivaAdapter?=null
    var unidadProductivaList:ArrayList<UnidadProductiva>?=ArrayList<UnidadProductiva>()
    var unidadProductivaGlobal:UnidadProductiva?=null
    //Progress
    private var hud: KProgressHUD?=null
    //Dialog
    var viewDialog:View?= null;
    var _dialogRegisterUpdate: AlertDialog? = null


    //Globals
    var unidadMedidaGlobal:Unidad_Medida?=null
    var listUnidadMedidaGlobal:List<Unidad_Medida>?= java.util.ArrayList<Unidad_Medida>()
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0

    companion object {
        var instance:  UnidadProductiva_Fragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UnidadProductiva_Fragment.instance =this
        presenter =  UpPresenter(this);
        presenter?.onCreate();
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_unidad_productiva, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title=getString(R.string.title_up)
        fabAddUnidadProductiva.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        setupInjection()
    }

     fun setupInjection(){
         presenter?.getUps()
     }

    private fun initAdapter() {
        //recyclerView?.layoutManager = LinearLayoutManager(activity)
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = UnidadProductivaAdapter(unidadProductivaList!!)
        recyclerView?.adapter = adapter
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
            focusView =  viewDialog?.edtNombreUnidadProductiva
            cancel = true
        } else if (viewDialog?.etDescripcionUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.etDescripcionUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.etDescripcionUnidadProductiva
            cancel = true
        } else if (viewDialog?.edtAreaUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.edtAreaUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.edtAreaUnidadProductiva
            cancel = true
        }
        else if (viewDialog?.spinnerUnidadMedidaUp?.text.toString().isEmpty()) {
            viewDialog?.spinnerUnidadMedidaUp?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.spinnerUnidadMedidaUp
            cancel = true
        }
        else if (viewDialog?.edtLocalizacionUnidadProductiva?.text.toString().isEmpty()) {
            viewDialog?.edtLocalizacionUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView =  viewDialog?.edtLocalizacionUnidadProductiva
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
        if(viewDialog!=null){
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
        if( viewDialog!=null){
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
        if(_dialogRegisterUpdate!=null){
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {

    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
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
            val unidadProductiva =  UnidadProductiva()
            unidadProductiva.Nombre=viewDialog?.edtNombreUnidadProductiva?.text.toString()
            unidadProductiva.Descripcion=viewDialog?.etDescripcionUnidadProductiva?.text.toString()
            unidadProductiva.UpArea=viewDialog?.edtAreaUnidadProductiva?.text.toString().toDoubleOrNull()
            unidadProductiva.Coordenadas=viewDialog?.edtLocalizacionUnidadProductiva?.text.toString()
            unidadProductiva.UnidadMedidaId=unidadMedidaGlobal?.Id
            unidadProductiva.Nombre_Unidad_Medida=unidadMedidaGlobal?.Descripcion
            unidadProductiva.Configuration_Point=true
            unidadProductiva.Configuration_Poligon=false
            unidadProductiva.Latitud=latitud
            unidadProductiva.Longitud=longitud
            presenter?.registerUP(unidadProductiva)
        }
    }

    override fun updateUp() {
        if (presenter?.validarCampos() == true) {
            var updateUP =  UnidadProductiva()
            updateUP.Id=unidadProductivaGlobal!!.Id
            updateUP.Nombre=viewDialog?.edtNombreUnidadProductiva?.text.toString()
            updateUP.Descripcion=viewDialog?.etDescripcionUnidadProductiva?.text.toString()
            updateUP.UpArea=viewDialog?.edtAreaUnidadProductiva?.text.toString().toDoubleOrNull()
            updateUP.Coordenadas=viewDialog?.edtLocalizacionUnidadProductiva?.text.toString()
            updateUP.UnidadMedidaId=unidadMedidaGlobal?.Id
            updateUP.Nombre_Unidad_Medida=unidadMedidaGlobal?.Descripcion
            updateUP.Configuration_Point=unidadProductivaGlobal!!.Configuration_Point
            updateUP.Configuration_Poligon=unidadProductivaGlobal!!.Configuration_Poligon
            updateUP.Latitud=latitud
            updateUP.Longitud=longitud
            presenter?.updateUP(updateUP)
        }
    }

    override fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>) {
        listUnidadMedidaGlobal = listUnidadMedida
    }


    override fun setListUnidadMedidaAdapterSpinner(){
        if(viewDialog!=null){
            ///Adapaters
            viewDialog?.spinnerUnidadMedidaUp!!.setAdapter(null)
            var uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedidaGlobal);
            viewDialog?.spinnerUnidadMedidaUp!!.setAdapter(uMedidaArrayAdapter);
            viewDialog?.spinnerUnidadMedidaUp!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaGlobal= listUnidadMedidaGlobal!![position] as Unidad_Medida
                //Toast.makeText(activity,""+ unidadMedidaGlobal!!.Id.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun showProgressHud(){
        var  imageView =  ImageView(activity);
        imageView.setBackgroundResource(R.drawable.spin_animation);
        var drawable =  imageView.getBackground() as AnimationDrawable;
        drawable.start();
        hud = KProgressHUD.create(activity)
                .setCustomView(imageView)
                .setWindowColor(resources.getColor(R.color.white))
                .setLabel("Cargando...",resources.getColor(R.color.grey_luiyi));
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }


    override fun showAlertDialogAddUnidadProductiva(unidadProductiva:UnidadProductiva?) {

        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_unidad_productiva, null)
        setListUnidadMedidaAdapterSpinner()
        val imageViewLocalizarUnidadProductiva= viewDialog?.imageViewLocalizarUnidadProductiva
        val btnCloseDialog= viewDialog?.ivClosetDialogUp
        //REGISTER
        if (unidadProductiva == null) {

        }
        //UPDATE
        else {
            unidadMedidaGlobal = Unidad_Medida(unidadProductiva.UnidadMedidaId, unidadProductiva.Nombre_Unidad_Medida, null)
            viewDialog?.edtNombreUnidadProductiva?.setText(unidadProductiva.Nombre)
            viewDialog?.etDescripcionUnidadProductiva?.setText(unidadProductiva.Descripcion)
            viewDialog?.edtAreaUnidadProductiva?.setText(unidadProductiva.UpArea.toString())
            viewDialog?.edtLocalizacionUnidadProductiva?.setText(unidadProductiva.Coordenadas)
            viewDialog?.spinnerUnidadMedidaUp?.setText(unidadProductiva.Nombre_Unidad_Medida)
            latitud=unidadProductiva.Latitud!!
            longitud= unidadProductiva.Longitud!!

        }
        //Set Events
        btnCloseDialog?.setOnClickListener(this)
        imageViewLocalizarUnidadProductiva?.setOnClickListener(this)
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
            R.id.ivClosetDialogUp->_dialogRegisterUpdate?.dismiss()

            R.id.imageViewLocalizarUnidadProductiva->{
                presenter?.startGps(activity as MenuMainActivity)
            }
        }
    }

    //Escuchador de eventos
    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
            if(extras.containsKey("latitud") && extras.containsKey("longitud") ){
                latitud=intent.extras!!.getDouble("latitud")
                longitud=intent.extras!!.getDouble("longitud")
                 hideProgressHud()
                if(viewDialog!=null){
                    viewDialog?.edtLocalizacionUnidadProductiva?.setText(String.format(getString(R.string.coords),latitud,longitud))
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
        presenter?.onPause( activity!!.applicationContext)
    }

    override fun onResume() {
        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }
    //endregion
}