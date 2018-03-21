package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion


import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
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
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.produccion.Produccion
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.Produccion.adapter.ProduccionAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_form_produccion.*
import kotlinx.android.synthetic.main.dialog_form_produccion.view.*
import kotlinx.android.synthetic.main.fragment_produccion.*
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
    var produccionGlobal:Produccion?=null
    var produccionList:ArrayList<Produccion>?=ArrayList<Produccion>()
    var listUnidadMedidaGlobal:List<Unidad_Medida>?= ArrayList<Unidad_Medida>()
    var Cultivo_Id: Long? = null
    var unidadMedidaGlobal:Unidad_Medida?=null
    var fechaInicio: Date?=null
    var fechaFin: Date?=null

    //Listas
    var listUnidadProductivaGlobal:List<UnidadProductiva>?= ArrayList<UnidadProductiva>()
    var listLoteGlobal:List<Lote>?= ArrayList<Lote>()
    var listCultivosGlobal:List<Cultivo>?= ArrayList<Cultivo>()
    var unidadProductivaGlobal:UnidadProductiva?=null
    var loteGlobal:Lote?=null
    var cultivoGlobal:Cultivo?=null


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
        setupInjection()
    }

    fun setupInjection(){
        presenter?.getListProduccion(Cultivo_Id)
        spinnerUnidadProductiva.setAdapter(null)
        var unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductivaGlobal)
        spinnerUnidadProductiva.setAdapter(unidadProductivaArrayAdapter)
        spinnerUnidadProductiva.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            unidadProductivaGlobal= listUnidadProductivaGlobal!![position] as UnidadProductiva
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
        }

        presenter?.setListSpinnerLote(null)
        presenter?.setListSpinnerCultivo(null)
    }

    //region ADAPTER
    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = ProduccionAdapter(produccionList!!)
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

    override fun limpiarCampos() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


    }

    override fun disableInputs() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun enableInputs() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideElements() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgressHud() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgressHud() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            presenter?.registerProdcuccion(producccion,Cultivo_Id!! )
        }
    }

    override fun updateProduccion() {
        if (presenter?.validarCampos() == true) {
            val producccion =  Produccion()
            producccion.Id=produccionGlobal?.Id
            producccion.FechaInicio=fechaInicio
            producccion.FechaFin=fechaFin
            producccion.ProduccionReal=viewDialog?.txtCantidadProduccionReal?.text.toString().toDoubleOrNull()
            producccion.CultivoId=Cultivo_Id
            producccion.UnidadMedidaId=unidadMedidaGlobal?.Id
            producccion.NombreUnidadMedida=unidadMedidaGlobal?.Descripcion
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

    override fun showAlertDialogAddProduccion(produccion: Produccion?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_produccion, null)
        setListUnidadMedidaAdapterSpinner()

        //REGISTER
        if (produccion == null) {

        }
        //UPDATE
        else {
            unidadMedidaGlobal = Unidad_Medida(produccion.UnidadMedidaId, produccion.NombreUnidadMedida, null)
            viewDialog?.txtFechaInicio?.setText(produccion.getFechaInicioFormat())
            viewDialog?.txtFechaFin?.setText(produccion.getFechafinFormat())
            viewDialog?.txtCantidadProduccionReal?.setText(produccion.ProduccionReal.toString())
            viewDialog?.spinnerUnidadMedidaProduccion?.setText(produccion.NombreUnidadMedida)

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
                    updateProduccion()
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


    override fun setListUnidadMedidaAdapterSpinner(){
        if(viewDialog!=null){
            ///Adapaters
            viewDialog?.spinnerUnidadMedidaProduccion!!.setAdapter(null)
            var uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedidaGlobal);
            viewDialog?.spinnerUnidadMedidaProduccion!!.setAdapter(uMedidaArrayAdapter);
            viewDialog?.spinnerUnidadMedidaProduccion!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaGlobal= listUnidadMedidaGlobal!![position] as Unidad_Medida
                //Toast.makeText(activity,""+ unidadMedidaGlobal!!.Id.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun setListSpinnerLote(unidad_productiva_id: Long?) {
        spinnerLote.setAdapter(null)
        spinnerLote.setText("")
        spinnerLote.setHint(String.format(getString(R.string.spinner_lote)))
        var list= listLoteGlobal?.filter { lote: Lote -> lote.Unidad_Productiva_Id==unidad_productiva_id }
        var loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, list)
        spinnerLote.setAdapter(loteArrayAdapter)
        spinnerLote.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            loteGlobal= list!![position] as Lote
            presenter?.setListSpinnerCultivo(loteGlobal?.Id)
        }
    }

    override fun setListSpinnerCultivo(lote_id: Long?) {
        spinnerCultivo.setAdapter(null)
        spinnerCultivo.setText("")
        spinnerCultivo.setHint(String.format(getString(R.string.spinner_cultivo)))
        var list= listCultivosGlobal?.filter { cultivo: Cultivo -> cultivo.LoteId==lote_id }
        val cultivoArrayAdapter = ArrayAdapter<Cultivo>(activity, android.R.layout.simple_spinner_dropdown_item, list)
        spinnerCultivo.setAdapter(cultivoArrayAdapter)
        spinnerCultivo.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            cultivoGlobal= listCultivosGlobal!![position] as Cultivo
        }
    }

    override fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>) {
        listUnidadMedidaGlobal = listUnidadMedida
    }

    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>) {
        listUnidadProductivaGlobal=listUnidadProductiva
    }

    override fun setListLotes(listLotes: List<Lote>) {
       listLoteGlobal= listLotes
    }

    override fun setListCultivos(listCultivos: List<Cultivo>) {
        listCultivosGlobal= listCultivos
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
                showAlertDialogAddProduccion(null)
            }
            R.id.ivClosetDialogUp->_dialogRegisterUpdate?.dismiss()
        }
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
