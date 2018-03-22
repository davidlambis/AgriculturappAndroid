package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos


import android.app.DatePickerDialog
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
import android.widget.*

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Cultivo
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote
import com.interedes.agriculturappv3.asistencia_tecnica.models.Lote_Table.Unidad_Medida_Id
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.adapters.CultivoAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_form_cultivo.view.*
import kotlinx.android.synthetic.main.fragment_cultivo.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class Cultivo_Fragment : Fragment(), View.OnClickListener, ICultivo.View, SwipeRefreshLayout.OnRefreshListener {


    var presenter: ICultivo.Presenter? = null

    //Dialog
    var viewDialog: View? = null
    var _dialogRegisterUpdate: AlertDialog? = null

    //adapter
    var adapter: CultivoAdapter? = null
    var cultivoList: ArrayList<Cultivo>? = ArrayList<Cultivo>()
    var cultivoGlobal: Cultivo? = null

    //Variables Globales
    var Unidad_Medida_Id: Long? = 0
    var Lote_Id: Long? = 0
    var listUnidadProductivaGlobal: List<UnidadProductiva>? = java.util.ArrayList<UnidadProductiva>()
    var listUnidadMedidaGlobal: List<Unidad_Medida>? = java.util.ArrayList<Unidad_Medida>()
    //Fecha
    internal var dateTime = Calendar.getInstance()
    var Date_Selected: String? = null
    var fecha: Boolean? = false

    companion object {
        var instance: Cultivo_Fragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        presenter = CultivoPresenter(this)
        presenter?.onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cultivo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_cultivo)
        fabAddCultivo.setOnClickListener(this)
        swipeRefreshLayout.setOnRefreshListener(this)
        setupInjection()
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = CultivoAdapter(cultivoList!!)
        recyclerView?.adapter = adapter
    }

    fun setupInjection() {
        presenter?.getAllCultivos()
    }


    //region on Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabAddCultivo -> {
                showAlertDialogCultivo(null)
            }
            R.id.ivClosetDialogCultivo -> {
                _dialogRegisterUpdate?.dismiss()
            }
            R.id.edtFechaInicio -> {
                fecha = true
                updateDate()
            }
            R.id.edtFechaFin -> {
                fecha = false
                updateDate()
            }
            R.id.btnRegisterCultivo -> {
                registerCultivo()
            }
            R.id.btnUpdateCultivo -> {
                updateCultivo()
            }
            R.id.btnCancelCultivo -> {
                _dialogRegisterUpdate?.dismiss()
            }
        }
    }
    //endregion

    //region Métodos Interfaz
    override fun validarCampos(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialog?.spinnerUnidadProductiva?.visibility == View.VISIBLE && viewDialog?.spinnerUnidadProductiva?.text.toString().isEmpty()) {
            onMessageDialogError(R.color.grey_luiyi, getString(R.string.snackbar_error_up))
            //focusView = viewDialog?.spinnerUnidadProductiva
            cancel = true
        } else if (viewDialog?.spinnerLote?.visibility == View.VISIBLE && viewDialog?.spinnerLote?.text.toString().isEmpty()) {
            onMessageDialogError(R.color.grey_luiyi, getString(R.string.snackbar_error_lote))
            //focusView = viewDialog?.spinnerLote
            cancel = true
        } else if (viewDialog?.edtNombreCultivo?.text.toString().isEmpty()) {
            viewDialog?.edtNombreCultivo?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtNombreCultivo
            cancel = true

        } else if (viewDialog?.edtDescripcionCultivo?.text.toString().isEmpty()) {
            viewDialog?.edtDescripcionCultivo?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtDescripcionCultivo
            cancel = true
        } else if (viewDialog?.edtEstimadoCosecha?.text.toString().isEmpty()) {
            viewDialog?.edtEstimadoCosecha?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtEstimadoCosecha
            cancel = true
        } else if (viewDialog?.spinnerUnidadMedidaCosecha?.text.toString().isEmpty()) {
            viewDialog?.spinnerUnidadMedidaCosecha?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerUnidadMedidaCosecha
            cancel = true
        } else if (viewDialog?.edtFechaInicio?.text.toString().isEmpty()) {
            viewDialog?.edtFechaInicio?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtFechaInicio
            cancel = true
        } else if (viewDialog?.edtFechaFin?.text.toString().isEmpty()) {
            viewDialog?.edtFechaFin?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtFechaFin
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun disableInputs() {
        setInputs(false)
    }

    override fun enableInputs() {
        setInputs(true)
    }

    private fun setInputs(b: Boolean) {
        if (viewDialog != null) {
            viewDialog?.spinnerUnidadProductiva?.isEnabled = b
            viewDialog?.edtNombreCultivo?.isEnabled = b
            viewDialog?.edtDescripcionCultivo?.isEnabled = b
            viewDialog?.edtEstimadoCosecha?.isEnabled = b
            viewDialog?.spinnerUnidadMedidaCosecha?.isEnabled = b
            viewDialog?.edtFechaInicio?.isEnabled = b
            viewDialog?.edtFechaFin?.isEnabled = b
        }
    }


    override fun showProgress() {
        swipeRefreshLayout.setRefreshing(true)
    }

    override fun hideProgress() {
        swipeRefreshLayout.setRefreshing(false)
    }

    override fun hideLotes() {
        viewDialog?.spinnerLote?.visibility = View.GONE
    }

    override fun registerCultivo() {
        if (presenter?.validarCampos() == true) {
            val cultivo = Cultivo()
            cultivo.Descripcion = viewDialog?.edtDescripcionCultivo?.text?.trim().toString()
            cultivo.EstimadoCosecha = viewDialog?.edtEstimadoCosecha?.text?.trim().toString().toDoubleOrNull()
            cultivo.FechaFin = viewDialog?.edtFechaFin?.text?.trim().toString()
            cultivo.FechaIncio = viewDialog?.edtFechaInicio?.text?.trim().toString()
            cultivo.LoteId = Lote_Id
            cultivo.Nombre = viewDialog?.edtNombreCultivo?.text?.trim()?.toString()
            cultivo.Unidad_Medida_Id = Unidad_Medida_Id
            cultivo.Nombre_Unidad_Medida = viewDialog?.spinnerUnidadMedidaCosecha?.text?.toString()
            presenter?.registerCultivo(cultivo)
        }
    }

    override fun updateCultivo() {
        if (presenter?.validarCampos() == true) {
            val cultivo = Cultivo()
            cultivo.Id = cultivoGlobal?.Id
            cultivo.Descripcion = viewDialog?.edtDescripcionCultivo?.text?.trim().toString()
            cultivo.EstimadoCosecha = viewDialog?.edtEstimadoCosecha?.text?.trim().toString().toDoubleOrNull()
            cultivo.FechaFin = viewDialog?.edtFechaFin?.text?.trim().toString()
            cultivo.FechaIncio = viewDialog?.edtFechaInicio?.text?.trim().toString()
            cultivo.LoteId = Lote_Id
            cultivo.Nombre = viewDialog?.edtNombreCultivo?.text?.trim()?.toString()
            cultivo.Unidad_Medida_Id = Unidad_Medida_Id
            cultivo.Nombre_Unidad_Medida = viewDialog?.spinnerUnidadMedidaCosecha?.text?.toString()
            presenter?.updateCultivo(cultivo)
        }
    }

    override fun deleteCultivo(cultivo: Cultivo): AlertDialog? {
        val builder = android.support.v7.app.AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

        })
        builder.setMessage(getString(R.string.alert_delete_cultivo));
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            cultivoGlobal = cultivo
            presenter?.deleteCultivo(cultivo)
        })
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }

    override fun setListCultivos(listCultivos: List<Cultivo>) {
        adapter?.clear()
        cultivoList?.clear()
        adapter?.setItems(listCultivos)
        hideProgress()
        setResults(listCultivos.size)
    }


    override fun setResults(cultivos: Int) {
        var results = String.format(getString(R.string.results_global_search),
                cultivos);
        txtResults.setText(results);
    }

    override fun requestResponseOk() {
        _dialogRegisterUpdate?.dismiss()
        onMessageOk(R.color.colorPrimary, getString(R.string.request_ok))
    }

    override fun requestResponseError(error: String?) {

    }

    override fun onMessageOk(colorPrimary: Int, msg: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container_fragment, msg!!, Snackbar.LENGTH_LONG)
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

    //Dialog
    override fun requestResponseDialogOK() {
    }

    override fun requestResponseDialogError(error: String?) {
        onMessageDialogError(R.color.grey_luiyi, error)
    }

    override fun onMessageDialogOk(colorPrimary: Int, msg: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(viewDialog?.dialogContainer!!, msg!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(activity!!.applicationContext, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageDialogError(colorPrimary: Int, msg: String?) {
        onMessageDialogOk(colorPrimary, msg)
    }

    override fun showAlertDialogCultivo(cultivo: Cultivo?): AlertDialog? {
        val dialog = AlertDialog.Builder(activity!!)
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_cultivo, null)

        if (viewDialog != null && listUnidadProductivaGlobal?.size!! > 0) {
            val btnCloseDialog = viewDialog?.ivClosetDialogCultivo
            val btnRegister = viewDialog?.btnRegisterCultivo
            val btnUpdateCultivo = viewDialog?.btnUpdateCultivo
            val btnCancelCultivo = viewDialog?.btnCancelCultivo
            val edtFechaInicio = viewDialog?.edtFechaInicio
            val edtFechaFin = viewDialog?.edtFechaFin

            setAdaptersSpinner()
            //Set Events
            btnCloseDialog?.setOnClickListener(this)
            edtFechaInicio?.setOnClickListener(this)
            edtFechaFin?.setOnClickListener(this)
            btnRegister?.setOnClickListener(this)
            btnUpdateCultivo?.setOnClickListener(this)
            btnCancelCultivo?.setOnClickListener(this)

            //REGISTER
            if (cultivo == null) {
                btnRegister?.visibility = View.VISIBLE
                btnUpdateCultivo?.visibility = View.GONE
            }
            //UPDATE
            else {
                btnRegister?.visibility = View.GONE
                btnUpdateCultivo?.visibility = View.VISIBLE
                viewDialog?.spinnerUnidadProductiva?.visibility = View.GONE
                viewDialog?.spinnerLote?.visibility = View.GONE
                viewDialog?.spinnerUnidadMedidaCosecha?.setText(cultivo.Nombre_Unidad_Medida)
                viewDialog?.edtNombreCultivo?.setText(cultivo.Nombre)
                viewDialog?.edtDescripcionCultivo?.setText(cultivo.Descripcion)
                viewDialog?.edtEstimadoCosecha?.setText(cultivo.EstimadoCosecha.toString())
                viewDialog?.edtFechaInicio?.setText(cultivo.FechaIncio)
                viewDialog?.edtFechaFin?.setText(cultivo.FechaFin)
            }

            dialog.setView(viewDialog)
            dialog.setTitle(getString(R.string.title_add_cultivo))
            dialog.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
                /*Snackbar.make(viewDialog?.coordenadas_lote!!, "No se realizaron cambios", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()*/
            })
            // dialog?.setMessage(getString(R.string.message_add_lote))
            dialog.setIcon(R.drawable.ic_cultivos)
            _dialogRegisterUpdate = dialog.show()
            return _dialogRegisterUpdate

        } else {
            onMessageError(R.color.grey_luiyi, getString(R.string.snackbar_error_unidad_productiva))
            return null
        }

    }

    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>) {
        listUnidadProductivaGlobal = listUnidadProductiva

    }

    override fun setListUnidadMedidas(listUnidadMedida: List<Unidad_Medida>) {
        listUnidadMedidaGlobal = listUnidadMedida
    }

    override fun setListLotes(listLotes: List<Lote>) {
        if (viewDialog != null) {
            viewDialog?.spinnerLote?.visibility = View.VISIBLE
            viewDialog?.spinnerLote!!.setAdapter(null)
            val loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, listLotes)
            viewDialog?.spinnerLote!!.setAdapter(loteArrayAdapter)
            viewDialog?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                if (listLotes.get(position).Id > 0) {
                    Lote_Id = listLotes.get(position).Id
                }
            }
        }
    }

    override fun setAdaptersSpinner() {
        viewDialog?.spinnerUnidadProductiva?.visibility = View.VISIBLE
        viewDialog?.spinnerUnidadProductiva!!.setAdapter(null)
        val unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductivaGlobal)
        viewDialog?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
        viewDialog?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            if (listUnidadProductivaGlobal?.get(position)?.Id!! > 0) {
                presenter?.loadLotesSpinner(listUnidadProductivaGlobal?.get(position)?.Id)
            }
        }

        viewDialog?.spinnerUnidadMedidaCosecha!!.setAdapter(null)
        val unidadMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedidaGlobal)
        viewDialog?.spinnerUnidadMedidaCosecha!!.setAdapter(unidadMedidaArrayAdapter)
        viewDialog?.spinnerUnidadMedidaCosecha!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            if (listUnidadMedidaGlobal?.get(position)?.Id!! > 0) {
                Unidad_Medida_Id = listUnidadMedidaGlobal?.get(position)?.Id
            }
        }
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
                if (state_conectivity == true) {

                }
            }
        }
    }
    //endregion

    //region Métodos
    override fun onRefresh() {
        showProgress()
        presenter?.getAllCultivos()
    }

    //Fecha
    private fun updateDate() {
        DatePickerDialog(context, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show()
    }

    internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        dateTime.set(Calendar.YEAR, year)
        dateTime.set(Calendar.MONTH, monthOfYear)
        dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        if (fecha!!) {
            mostrarResultadosFechaInicio()
        } else {
            mostrarResultadosFechaFin()
        }
    }

    private fun mostrarResultadosFechaInicio() {
        val format1 = SimpleDateFormat("MM/dd/yyyy")
        val formatted = format1.format(dateTime.time)
        Date_Selected = formatted
        viewDialog?.edtFechaInicio?.setText(Date_Selected)
    }

    private fun mostrarResultadosFechaFin() {
        val format1 = SimpleDateFormat("MM/dd/yyyy")
        val formatted = format1.format(dateTime.time)
        Date_Selected = formatted
        viewDialog?.edtFechaFin?.setText(Date_Selected)
    }

    //endregion

    //region Ciclo de vida Fragment
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause(activity?.applicationContext!!)
    }

    override fun onResume() {
        presenter?.onResume(activity?.applicationContext!!)
        super.onResume()
    }

    //endregion
}
