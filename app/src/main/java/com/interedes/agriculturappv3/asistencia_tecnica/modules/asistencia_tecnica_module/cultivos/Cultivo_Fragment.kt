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
import android.view.WindowManager
import android.widget.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.*
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.cultivos.adapters.CultivoAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_form_cultivo.*
import kotlinx.android.synthetic.main.dialog_form_cultivo.view.*
import kotlinx.android.synthetic.main.fragment_cultivo.*
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
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
    var _dialogRegisterUpdate: MaterialDialog? = null

    var viewDialogFilter:View?= null
    var _dialogFilter: MaterialDialog? = null

    //adapter
    var adapter: CultivoAdapter? = null
    var cultivoList: ArrayList<Cultivo>? = ArrayList<Cultivo>()
    var cultivoGlobal: Cultivo? = null

    //Variables Globales
    var Unidad_Medida_Id: Long? = 0
    var Lote_Id: Long? = 0

    var unidadProductivaGlobal:UnidadProductiva?=null
    var unidadMedidaGlobal:Unidad_Medida?=null
    var loteGlobal:Lote?=null
    var tipoProductoGlobal:TipoProducto?=null
    var detalleTipoProductoGlobal:DetalleTipoProducto?=null

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
        ivBackButton.setOnClickListener(this)
        searchFilter.setOnClickListener(this)
        setupInjection()
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = CultivoAdapter(cultivoList!!)
        recyclerView?.adapter = adapter
    }

    fun setupInjection() {
        presenter?.getListCultivos(null)
    }


    //region on Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.fabAddCultivo -> {
                showAlertDialogFilterCultivo(false)
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


            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).replaceCleanFragment(AsistenciaTecnicaFragment())
            }


            R.id.searchFilter -> {
                showAlertDialogFilterCultivo(true)
            }



        }
    }

    //endregion

    //Search

    override fun validarListasFilterLote(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialogFilter?.spinnerUnidadProductiva?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerUnidadProductiva
            cancel = true
        } else if (viewDialogFilter?.spinnerLote?.text.toString().isEmpty()) {
            onMessageError(R.color.grey_luiyi, getString(R.string.error_lotes))
            viewDialogFilter?.spinnerLote?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerLote
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }


    override fun validarCampos(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialog?.edtNombreCultivo?.text.toString().isEmpty()) {
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
        } else if (viewDialog?.spinnerTipoProducto?.text.toString().isEmpty()) {
            focusView = viewDialog?.spinnerTipoProducto
            cancel = true
        } else if (viewDialog?.spinnerDetalleTipoProducto?.text.toString().isEmpty()) {
            focusView = viewDialog?.spinnerDetalleTipoProducto
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
            cultivo.NombreUnidadProductiva=viewDialog?.txtUnidadProductivaSelected?.text.toString()
            cultivo.NombreLote=viewDialog?.txtLoteSelected?.text.toString()
            cultivo.Nombre_Tipo_Producto=tipoProductoGlobal?.Nombre
            cultivo.DetalleTipoProductoId = detalleTipoProductoGlobal?.Id
            cultivo.Nombre_Detalle_Tipo_Producto = detalleTipoProductoGlobal?.Nombre
            cultivo.Id_Tipo_Producto=tipoProductoGlobal?.Id
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
            cultivo.NombreUnidadProductiva=viewDialog?.txtUnidadProductivaSelected?.text.toString()
            cultivo.NombreLote=viewDialog?.txtLoteSelected?.text.toString()
            cultivo.Nombre_Tipo_Producto=tipoProductoGlobal?.Nombre
            cultivo.DetalleTipoProductoId = detalleTipoProductoGlobal?.Id
            cultivo.Nombre_Detalle_Tipo_Producto =  viewDialog?.spinnerDetalleTipoProducto?.text?.toString()
            cultivo.Id_Tipo_Producto=tipoProductoGlobal?.Id
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
        builder.setIcon(R.drawable.ic_cultivos);
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
        onMessageOk(R.color.grey_luiyi, error)
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

    override fun messageErrorDialog(msg: String?): AlertDialog? {
        var builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(msg);
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_admiracion);
        return builder.show();
    }

    //Dialog
    override fun showAlertDialogCultivo(cultivo: Cultivo?): MaterialDialog? {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_cultivo, null)
            val btnCloseDialog = viewDialog?.ivClosetDialogCultivo
            val edtFechaInicio = viewDialog?.edtFechaInicio
            val edtFechaFin = viewDialog?.edtFechaFin
             presenter?.setListSpinnerTipoProducto()
             presenter?.setListSpinnerUnidadMedida()
            //Set Events
            btnCloseDialog?.setOnClickListener(this)
            edtFechaInicio?.setOnClickListener(this)
            edtFechaFin?.setOnClickListener(this)

            //REGISTER
            if (cultivo == null) {
                viewDialog?.txtUnidadProductivaSelected?.text=unidadProductivaGlobal?.Nombre
                viewDialog?.txtLoteSelected?.text=loteGlobal?.Nombre
            }
            //UPDATE
            else {
                presenter?.setListSpinnerDetalleTipoProducto(cultivo.Id_Tipo_Producto)
                viewDialog?.txtUnidadProductivaSelected?.text=cultivo.NombreUnidadProductiva
                viewDialog?.txtLoteSelected?.text=cultivo?.NombreLote
                viewDialog?.spinnerUnidadMedidaCosecha?.setText(cultivo.Nombre_Unidad_Medida)
                viewDialog?.edtNombreCultivo?.setText(cultivo.Nombre)
                viewDialog?.edtDescripcionCultivo?.setText(cultivo.Descripcion)
                viewDialog?.edtEstimadoCosecha?.setText(cultivo.EstimadoCosecha.toString())
                viewDialog?.edtFechaInicio?.setText(cultivo.FechaIncio)
                viewDialog?.edtFechaFin?.setText(cultivo.FechaFin)
                viewDialog?.spinnerTipoProducto?.setText(cultivo.Nombre_Tipo_Producto)
                viewDialog?.spinnerDetalleTipoProducto?.setText(cultivo.Nombre_Detalle_Tipo_Producto)
                tipoProductoGlobal = TipoProducto(cultivo.Id_Tipo_Producto, cultivo.Nombre_Tipo_Producto)
                detalleTipoProductoGlobal=DetalleTipoProducto(cultivo.DetalleTipoProductoId,cultivo.Nombre_Detalle_Tipo_Producto)
            }

            val dialog = MaterialDialog.Builder(activity!!)
                    .title(getString(R.string.title_add_cultivo))
                    .customView(viewDialog!!, true)
                    .positiveText(R.string.btn_save)
                    .negativeText(R.string.close)
                    .titleGravity(GravityEnum.CENTER)
                    .titleColorRes(R.color.light_green_800)
                    .limitIconToDefaultSize()
                    //.maxIconSizeRes(R.dimen.text_size_40)
                    // .positiveColorRes(R.color.material_red_400)
                    .backgroundColorRes(R.color.white_solid)
                    // .negativeColorRes(R.color.material_red_400)
                    .iconRes(R.drawable.ic_cultivos)
                    .dividerColorRes(R.color.colorPrimary)
                    .contentColorRes(android.R.color.white)
                    .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                    .positiveColor(Color.WHITE)
                    .autoDismiss(false)
                    //.negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                    .theme(Theme.DARK)
                    .onNegative({ dialog1, which ->
                        dialog1.dismiss()
                    })
                    .onPositive({ dialog1, which ->
                       if(cultivo==null){
                           registerCultivo()
                       }else{
                           updateCultivo()
                       }
                    })
                    .build()

            val lp = WindowManager.LayoutParams()
            lp.copyFrom(dialog.getWindow().getAttributes())
            lp.width = WindowManager.LayoutParams.MATCH_PARENT
            lp.height = WindowManager.LayoutParams.MATCH_PARENT
            dialog.show()
            dialog.getWindow().setAttributes(lp)
            _dialogRegisterUpdate=dialog

            return _dialogRegisterUpdate

    }


    override fun showAlertDialogFilterCultivo(isFilter:Boolean?) {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()
        var title:String?=null

        viewDialogFilter?.spinnerCultivo?.visibility=View.GONE

        if(isFilter==true){
            title=getString(R.string.tittle_filter)
        }else{
            title=getString(R.string.tittle_select_lote)
        }

        if(unidadProductivaGlobal!=null && loteGlobal!=null){
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.Nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.Nombre)
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
                            if(isFilter==true){
                                if(presenter?.validarListasFilterLote()==true){
                                    dialog1.dismiss()
                                    presenter?.getListCultivos(Lote_Id)
                                }
                            }else{
                                if(presenter?.validarListasFilterLote()==true){
                                    dialog1.dismiss()
                                    showAlertDialogCultivo(null)
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
        _dialogFilter=dialog
    }


    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?) {
        if(viewDialogFilter!=null){
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(null)
            val unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                unidadProductivaGlobal= listUnidadProductiva!![position] as UnidadProductiva
                presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            }
        }
        presenter?.setListSpinnerLote(null)
    }


    override fun setListLotes(listLotes: List<Lote>?) {
        if (viewDialogFilter != null) {
            viewDialogFilter?.spinnerLote!!.setAdapter(null)
            val loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, listLotes)
            viewDialogFilter?.spinnerLote!!.setAdapter(loteArrayAdapter)
            viewDialogFilter?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                loteGlobal= listLotes!![position] as Lote
                Lote_Id= listLotes.get(position).Id
                presenter?.setListSpinnerLote(loteGlobal?.Id)
            }
        }
    }

    override fun setListTipoProducto(listTipoProducto: List<TipoProducto>?) {
        if(viewDialog!=null){
            viewDialog?.spinnerTipoProducto!!.setAdapter(null)
            val tipoProductoArrayAdapter = ArrayAdapter<TipoProducto>(activity, android.R.layout.simple_spinner_dropdown_item, listTipoProducto)
            viewDialog?.spinnerTipoProducto!!.setAdapter(tipoProductoArrayAdapter)
            viewDialog?.spinnerTipoProducto!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                tipoProductoGlobal= listTipoProducto!![position] as TipoProducto
                viewDialog?.spinnerDetalleTipoProducto?.setText("")
                viewDialog?.spinnerDetalleTipoProducto?.setHint(String.format(getString(R.string.spinner_detalle_tipo_producto)))
                presenter?.setListSpinnerDetalleTipoProducto(tipoProductoGlobal?.Id)
            }
            presenter?.setListSpinnerDetalleTipoProducto(null)
        }
    }

    override fun setListDetalleTipoProducto(listDetalleTipoProducto: List<DetalleTipoProducto>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerDetalleTipoProducto!!.setAdapter(null)
            val detalleTipoProductoArrayAdapter = ArrayAdapter<DetalleTipoProducto>(activity, android.R.layout.simple_spinner_dropdown_item, listDetalleTipoProducto)
            viewDialog?.spinnerDetalleTipoProducto!!.setAdapter(detalleTipoProductoArrayAdapter)
            viewDialog?.spinnerDetalleTipoProducto!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                detalleTipoProductoGlobal= listDetalleTipoProducto!![position] as DetalleTipoProducto
            }
        }
    }

    override fun setListUnidadMedidas(listUnidadMedida: List<Unidad_Medida>?) {
        if(viewDialog!=null){
            ///Adapaters
            viewDialog?.spinnerUnidadMedidaCosecha!!.setAdapter(null)
            var uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedida);
            viewDialog?.spinnerUnidadMedidaCosecha!!.setAdapter(uMedidaArrayAdapter);
            viewDialog?.spinnerUnidadMedidaCosecha!!.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, position, l ->
                unidadMedidaGlobal= listUnidadMedida!![position] as Unidad_Medida
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
        presenter?.getListCultivos(Lote_Id)
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
