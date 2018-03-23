package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion


import android.app.DatePickerDialog
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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.adapter.ProduccionAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters.TipoProductosAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.listas.Listas
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.dialog_list_general.*
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.content_recyclerview.view.*
import kotlinx.android.synthetic.main.dialog_form_produccion.*
import kotlinx.android.synthetic.main.dialog_form_produccion.view.*
import kotlinx.android.synthetic.main.fragment_produccion.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class ProduccionFragment : Fragment(), View.OnClickListener , SwipeRefreshLayout.OnRefreshListener,  IMainProduccion.MainView {

    var presenter: IMainProduccion.Presenter? = null
    var adapter:ProduccionAdapter?=null

    //Progress
    private var hud: KProgressHUD?=null

    //Dialog
    var viewDialog:View?= null;
    var _dialogRegisterUpdate: AlertDialog? = null

    //Globals
    //var produccionGlobal:Produccion?=null
    var produccionList:ArrayList<Produccion>?=ArrayList<Produccion>()
    var Cultivo_Id: Long? = null
    var unidadMedidaGlobal:Unidad_Medida?=null
    var fechaInicio: Date?=null
    var fechaFin: Date?=null

    var confFecha:Boolean?=false

    //Listas
    var cultivoGlobal:Cultivo?=null
    var unidadProductivaGlobal:UnidadProductiva?=null
    var loteGlobal:Lote?=null

    var dateTime = Calendar.getInstance()

    companion object {
        var instance:  ProduccionFragment? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_produccion, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProduccionFragment.instance =this
        presenter =  ProduccionPresenter(this);
        presenter?.onCreate();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title=getString(R.string.tittle_producccion)
        fabAddProduccion.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        ivBackButton.setOnClickListener(this)
        dialogProductos.setOnClickListener(this)
        setupInjection()
    }

    fun setupInjection(){
        presenter?.getListProduccion(Cultivo_Id)
        presenter?.setListSpinnerUnidadProductiva()
    }

    //region ADAPTER
    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = ProduccionAdapter(produccionList!!,activity)
        recyclerView?.adapter = adapter
    }
    //endregion

    //region IMPLEMENTS MAINVIEW
    override fun validarCampos(): Boolean {
        var cancel = false
        var focusView: View? = null
       if (viewDialog?.txtFechaInicio?.text.toString().isEmpty()) {
            viewDialog?.txtFechaInicio?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtFechaInicio
            cancel = true
        } else if (viewDialog?.txtFechaFin?.text.toString().isEmpty()) {
            viewDialog?.txtFechaFin?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtFechaFin
            cancel = true
        } else if (viewDialog?.txtCantidadProduccionReal?.text.toString().isEmpty()) {
            viewDialog?.txtCantidadProduccionReal?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtCantidadProduccionReal
            cancel = true
        } else  if (viewDialog?.spinnerUnidadMedidaProduccion?.text.toString().isEmpty() && viewDialog?.spinnerUnidadMedidaProduccion?.visibility == View.VISIBLE) {
            viewDialog?.spinnerUnidadMedidaProduccion?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerUnidadMedidaProduccion
            cancel = true
        }  /* else if (!edtCorreo.text.toString().trim().matches(email_pattern.toRegex())) {
            edtCorreo?.setError(getString(R.string.edit_text_error_correo))
            focusView = edtCorreo
            cancel = true
        }*/
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }

    override fun validarListasAddProduccion(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (spinnerUnidadProductiva?.text.toString().isEmpty() ) {
            spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = spinnerUnidadProductiva
            cancel = true
        } else if (spinnerLote?.text.toString().isEmpty()) {
            spinnerLote?.setError(getString(R.string.error_field_required))
            focusView =spinnerLote
            cancel = true
        }
        else if (spinnerCultivo?.text.toString().isEmpty() ) {
            spinnerCultivo?.setError(getString(R.string.error_field_required))
            focusView = spinnerCultivo
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
            viewDialog?.txtFechaInicio?.setText("");
            viewDialog?.txtFechaFin?.setText("");
            viewDialog?.txtCultivoSelected?.setText("");
            viewDialog?.txtUnidadProductivaSelected?.setText("");
            viewDialog?.txtLoteSelected?.setText("");
            viewDialog?.txtCantidadProduccionReal?.setText("");

        }
    }

    override fun showProgress() {
        showProgressHud()
        swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        hideProgressHud()
        swipeRefreshLayout.setRefreshing(false);
    }

    override fun disableInputs() {
        setInputs(false)
    }
    override fun enableInputs() {
        setInputs(true)
    }
    private fun setInputs(b: Boolean) {
        if( viewDialog!=null){
            viewDialog?.txtFechaFin?.isEnabled = b
            viewDialog?.txtFechaFin?.isEnabled = b
            viewDialog?.spinnerUnidadMedidaProduccion?.isEnabled = b
            viewDialog?.txtCantidadProduccionReal?.isEnabled = b
        }
    }
    override fun showProgressHud(){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun registerProduccion() {
        if (presenter?.validarCampos() == true) {
            val producccion =  Produccion()
            producccion.FechaInicio=fechaInicio
            producccion.FechaFin=fechaFin
            producccion.ProduccionReal=viewDialog?.txtCantidadProduccionReal?.text.toString().toDoubleOrNull()
            producccion.CultivoId=Cultivo_Id
            producccion.UnidadMedidaId=unidadMedidaGlobal?.Id
            producccion.NombreUnidadMedida=unidadMedidaGlobal?.Descripcion


            producccion.NombreUnidadProductiva=viewDialog?.txtUnidadProductivaSelected?.text.toString()
            producccion.NombreLote= viewDialog?.txtLoteSelected?.text.toString()
            producccion.NombreCultivo=viewDialog?.txtCultivoSelected?.text.toString()
            presenter?.registerProdcuccion(producccion,Cultivo_Id!! )


        }
    }

    override fun updateProduccion(produccion:Produccion) {
        if (presenter?.validarCampos() == true) {
            val producccion =  Produccion()
            producccion.Id=produccion.Id
            producccion.FechaInicio=fechaInicio
            producccion.FechaFin=fechaFin
            producccion.ProduccionReal=viewDialog?.txtCantidadProduccionReal?.text.toString().toDoubleOrNull()
            producccion.CultivoId=Cultivo_Id
            producccion.UnidadMedidaId=unidadMedidaGlobal?.Id
            producccion.NombreUnidadMedida=unidadMedidaGlobal?.Descripcion


            producccion.NombreUnidadProductiva=viewDialog?.txtUnidadProductivaSelected?.text.toString()
            producccion.NombreLote= viewDialog?.txtLoteSelected?.text.toString()
            producccion.NombreCultivo=viewDialog?.txtCultivoSelected?.text.toString()
            presenter?.registerProdcuccion(producccion,Cultivo_Id!! )
        }
    }

    override fun setListProduccion(listProduccion: List<Produccion>) {
        adapter?.clear()
        produccionList?.clear()
        adapter?.setItems(listProduccion)
        hideProgress()
        setResults(listProduccion.size)
    }

    override fun setResults(listProduccion: Int) {
        var results = String.format(getString(R.string.results_global_search),
                listProduccion);
        txtResults.setText(results);
    }

    override fun requestResponseOK() {
        if(_dialogRegisterUpdate!=null){
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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



    override fun showAlertDialogAddProduccion(produccion: Produccion?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_produccion, null)
        presenter?.setListSpinnerUnidadMedida()


        viewDialog?.ivClosetDialog?.setOnClickListener(this)
        viewDialog?.txtFechaInicio?.setOnClickListener(this)
        viewDialog?.txtFechaFin?.setOnClickListener(this)


        //REGISTER
        if (produccion == null) {
            viewDialog?.txtUnidadProductivaSelected?.setText(unidadProductivaGlobal?.Nombre)
            viewDialog?.txtLoteSelected?.setText(loteGlobal?.Nombre)
            viewDialog?.txtCultivoSelected?.setText(cultivoGlobal?.Nombre)
        }
        //UPDATE
        else {

            viewDialog?.txtUnidadProductivaSelected?.setText(produccion.NombreUnidadProductiva)
            viewDialog?.txtLoteSelected?.setText(produccion.NombreLote)
            viewDialog?.txtCultivoSelected?.setText(produccion.NombreCultivo)

            Cultivo_Id=produccion.CultivoId
            unidadMedidaGlobal = Unidad_Medida(produccion.UnidadMedidaId, produccion.NombreUnidadMedida, null)
            viewDialog?.txtFechaInicio?.setText(produccion.getFechaInicioFormat())
            viewDialog?.txtFechaFin?.setText(produccion.getFechafinFormat())
            viewDialog?.txtCantidadProduccionReal?.setText(produccion.ProduccionReal.toString())
            viewDialog?.spinnerUnidadMedidaProduccion?.setText(produccion.NombreUnidadMedida)

            fechaInicio= produccion.FechaInicio
            fechaFin=produccion.FechaFin
        }
        //Set Events
        ivClosetDialog?.setOnClickListener(this)
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
                if(produccion!=null){
                    updateProduccion(produccion)
                }else{
                    registerProduccion()
                }
                //Dismiss once everything is OK.
                //dialog.dismiss()
            }
        })
        dialog?.show()
        _dialogRegisterUpdate=dialog
    }



     fun showAlertDialogTipoProduccion() {
        val inflater = this.layoutInflater
        var viewDialogTipoProductos = inflater.inflate(R.layout.dialog_list_general, null)

         viewDialogTipoProductos.recyclerView?.layoutManager = GridLayoutManager(activity,2)
         var lista:ArrayList<TipoProducto>?=ArrayList<TipoProducto>()
         var  adapterLocal = TipoProductosAdapter(lista!!)
         viewDialogTipoProductos.recyclerView?.adapter = adapterLocal

         adapterLocal.setItems(Listas.listaTipoProducto())

         viewDialogTipoProductos.swipeRefreshLayout.isRefreshing=false



        //Set Events
        val dialog = AlertDialog.Builder(context!!)
                .setView(viewDialogTipoProductos)
                .setIcon(R.drawable.ic_produccion_cultivo)
                . setTitle(getString(R.string.tittle_add_unidadproductiva))
                .setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
                })
                .create()

        dialog?.show()

    }


    override fun confirmDelete(produccion: Produccion): AlertDialog? {
        var builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

        })
        builder.setMessage(getString(R.string.title_alert_delete_produccion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            presenter?.deleteProduccion(produccion, Cultivo_Id)
        })
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }

    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?) {
        spinnerUnidadProductiva.setAdapter(null)
        var unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductiva)
        spinnerUnidadProductiva.setAdapter(unidadProductivaArrayAdapter)
        spinnerUnidadProductiva.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            spinnerLote.setText("")
            spinnerLote.setHint(String.format(getString(R.string.spinner_lote)))

            spinnerCultivo.setText("")
            spinnerCultivo.setHint(String.format(getString(R.string.spinner_cultivo)))

            unidadProductivaGlobal= listUnidadProductiva!![position] as UnidadProductiva
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
        }
        presenter?.setListSpinnerLote(null)
        presenter?.setListSpinnerCultivo(null)
    }

    override fun setListLotes(listLotes: List<Lote>?) {
        spinnerLote.setAdapter(null)
        spinnerLote.setText("")
        spinnerLote.setHint(String.format(getString(R.string.spinner_lote)))
        var loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, listLotes)
        spinnerLote.setAdapter(loteArrayAdapter)
        spinnerLote.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            spinnerCultivo.setText("")
            spinnerCultivo.setHint(String.format(getString(R.string.spinner_cultivo)))
            loteGlobal= listLotes!![position] as Lote
            presenter?.setListSpinnerCultivo(loteGlobal?.Id)

        }
    }

    override fun setListCultivos(listCultivos: List<Cultivo>?) {
        spinnerCultivo.setAdapter(null)
        spinnerCultivo.setText("")
        spinnerCultivo.setHint(String.format(getString(R.string.spinner_cultivo)))
        val cultivoArrayAdapter = ArrayAdapter<Cultivo>(activity, android.R.layout.simple_spinner_dropdown_item, listCultivos)
        spinnerCultivo.setAdapter(cultivoArrayAdapter)
        spinnerCultivo.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            cultivoGlobal= listCultivos!![position] as Cultivo
            Cultivo_Id=cultivoGlobal?.Id
            presenter?.getListProduccion(Cultivo_Id)
        }
    }

    override fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>?) {
        if(viewDialog!=null){
            ///Adapaters
            viewDialog?.spinnerUnidadMedidaProduccion!!.setAdapter(null)
            var uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedida);
            viewDialog?.spinnerUnidadMedidaProduccion!!.setAdapter(uMedidaArrayAdapter);
            viewDialog?.spinnerUnidadMedidaProduccion!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaGlobal= listUnidadMedida!![position] as Unidad_Medida
                //Toast.makeText(activity,""+ unidadMedidaGlobal!!.Id.toString(),Toast.LENGTH_SHORT).show()
            }
        }
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
    //endregioon

    //region Events
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAddProduccion -> {
                if(presenter?.validarListasAddProduccion()==true){
                    showAlertDialogAddProduccion(null)
                }
            }
            R.id.ivClosetDialogUp->_dialogRegisterUpdate?.dismiss()

            R.id.txtFechaInicio -> {
                confFecha=true
                updateDate()
            }
            R.id.txtFechaFin -> {
                confFecha=false
                updateDate()
            }
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).replaceCleanFragment(AsistenciaTecnicaFragment())
            }

            R.id.dialogProductos -> {
               showAlertDialogTipoProduccion()
            }


        }
    }

    //Fecha
    private fun updateDate() {
        DatePickerDialog(context, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show()
    }

    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateTime.set(Calendar.YEAR, year)
        dateTime.set(Calendar.MONTH, monthOfYear)
        dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        if (confFecha!!) {
            mostrarResultadosFechaInicio()
        } else {
            mostrarResultadosFechaFin()
        }
    }

    private fun mostrarResultadosFechaInicio() {
        val format1 = SimpleDateFormat("MM/dd/yyyy")
        val formatted = format1.format(dateTime.time)
        fechaInicio=dateTime.time
        viewDialog?.txtFechaInicio?.setText(formatted)
    }

    private fun mostrarResultadosFechaFin() {
        val format1 = SimpleDateFormat("MM/dd/yyyy")
        val formatted = format1.format(dateTime.time)
        fechaFin=dateTime.time
        viewDialog?.txtFechaFin?.setText(formatted)
    }

    //Escuchador de eventos
    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
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
        presenter?.getListProduccion(Cultivo_Id)
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

}// Required empty public constructor
