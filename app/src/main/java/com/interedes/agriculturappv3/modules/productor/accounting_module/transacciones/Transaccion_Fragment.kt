package com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones


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
import android.text.Editable
import android.text.TextWatcher
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
import com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.adapter.TrannsaccionAdapter
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo_Table
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.ventas.*
import com.interedes.agriculturappv3.modules.productor.accounting_module.transacciones.adapter.EstadoTransaccionAdapter
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.resources.CategoriaPukResources
import com.interedes.agriculturappv3.services.resources.EstadoTransaccionResources
import com.interedes.agriculturappv3.services.resources.NaturalezaTransaccionResources
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
import kotlinx.android.synthetic.main.diaog_form_transacciones.view.*
import kotlinx.android.synthetic.main.fragment_transacciones.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Transaccion_Fragment : Fragment(), View.OnClickListener , SwipeRefreshLayout.OnRefreshListener, IMainViewTransacciones.MainView {

    var presenter: IMainViewTransacciones.Presenter? = null
    var adapter: TrannsaccionAdapter?=null
    var adapterEstadotransaccion: EstadoTransaccionAdapter?=null

    //Progress
    private var hud: KProgressHUD?=null

    //Dialog
    var viewDialog:View?= null
    var _dialogRegisterUpdate: AlertDialog? = null
    var viewDialogFilter:View?= null
    var _dialogFilter: MaterialDialog? = null

    //Globals
    //var produccionGlobal:Produccion?=null
    var produccionList:ArrayList<Transaccion>?=ArrayList<Transaccion>()
    var estadoTransaccionList:ArrayList<Estado_Transaccion>?=ArrayList<Estado_Transaccion>()
    var Cultivo_Id: Long? = null
    var changeCultivo:Boolean?=false

    //Listas
    var cultivoGlobal: Cultivo?=null
    var pukGlobal: Puk?=null
    var unidadProductivaGlobal: Unidad_Productiva?=null
    var loteGlobal: Lote?=null
    var transaccionGlobal: Transaccion?=null
    var valorTotalGlobal:Double?=null
    var estadoTransaccionGlobal:Estado_Transaccion?=null




    companion object {
        var instance:  Transaccion_Fragment? = null
        var typeTransaccion:Long?=0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transacciones, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance =this
        presenter = TransaccionPresenter(this);
        presenter?.onCreate();
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        if(CategoriaPukResources.INGRESO== typeTransaccion){
            (activity as MenuMainActivity).toolbar.title=getString(R.string.title_add_venta)
        }else if(CategoriaPukResources.GASTO== typeTransaccion){
            (activity as MenuMainActivity).toolbar.title=getString(R.string.title_add_gasto)
        }

        fabAddTransaccion.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        ivBackButton.setOnClickListener(this)
        searchFilter.setOnClickListener(this)
        setupInjection()
    }

    fun setupInjection(){
        presenter?.getListTransaccion(Cultivo_Id,typeTransaccion)
        /*
        spinnerUnidadProductiva?.setOnClickListener {view->
            spinnerUnidadProductiva?.error=null
            var focusView: View? = null
            focusView = spinnerUnidadProductiva
        }*/

    }

    //region ADAPTER
    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = TrannsaccionAdapter(produccionList!!)
        recyclerView?.adapter = adapter
    }
    //endregion


    //region IMPLEMENTS METHIDS INTERFACE MAINVIEW
    override fun validarCampos(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialog?.changeCultivo?.text.toString().isEmpty()) {
            viewDialog?.changeCultivo?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.changeCultivo
            cancel = true
        } else if (viewDialog?.txtCantidadTransaccion?.text.toString().isEmpty()) {
            viewDialog?.txtCantidadTransaccion?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtCantidadTransaccion
            cancel = true
        } else if (viewDialog?.txtPrecioTransaccion?.text.toString().isEmpty()) {
            viewDialog?.txtPrecioTransaccion?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtPrecioTransaccion
            cancel = true
        }  else if (viewDialog?.txtTotalTransaccion?.text.toString().isEmpty()) {
            viewDialog?.txtTotalTransaccion?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtTotalTransaccion
            cancel = true
        }
        else if (viewDialog?.txtNombreCliente?.text.toString().isEmpty()) {
            viewDialog?.txtNombreCliente?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtNombreCliente
            cancel = true
        }
        else if (viewDialog?.txtConceptoVenta?.text.toString().isEmpty()) {
            viewDialog?.txtConceptoVenta?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtConceptoVenta
            cancel = true
        }

        else if (viewDialog?.spinnerPuk?.text.toString().isEmpty()) {
            viewDialog?.spinnerPuk?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerPuk
            cancel = true
        }
        else if (viewDialog?.radioGroupEstados?.getCheckedRadioButtonId() == -1) {
            cancel = true
            Toast.makeText(activity,getString(R.string.error_field_required_radio_group),Toast.LENGTH_SHORT).show()
        }



        /* else if (!edtCorreo.text.toString().trim().matches(email_pattern.toRegex())) {
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

    override fun validarListasAddTransaccion(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialogFilter?.spinnerUnidadProductiva?.text.toString().isEmpty() ) {
            viewDialogFilter?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerUnidadProductiva
            cancel = true
        } else if (viewDialogFilter?.spinnerLote?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerLote?.setError(getString(R.string.error_field_required))
            focusView =viewDialogFilter?.spinnerLote
            cancel = true
        }
        else if (viewDialogFilter ?.spinnerCultivo?.text.toString().isEmpty() ) {
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

    override fun limpiarCampos() {
        if(viewDialog!=null){
            viewDialog?.txtCantidadTransaccion?.setText("0");
            viewDialog?.txtPrecioTransaccion?.setText("");
            viewDialog?.txtTotalTransaccion?.setText("");
            viewDialog?.txtNombreCliente?.setText("");
            viewDialog?.txtConceptoVenta?.setText("");

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
            viewDialog?.txtCantidadTransaccion?.isEnabled = b
            viewDialog?.txtPrecioTransaccion?.isEnabled = b
            viewDialog?.txtTotalTransaccion?.isEnabled = b
            viewDialog?.txtConceptoVenta?.isEnabled = b
            viewDialog?.fabAddVenta?.isEnabled = b
            viewDialog?.fabAddVenta?.isClickable = b


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


    override fun showProgressHud(){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white))
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun registerTransaccion() {
        if (presenter?.validarCampos() == true) {


            val dateFormatFecha = SimpleDateFormat("MM-dd-yyyy")
            val dateFecha = Date()
            val FechaString = dateFormatFecha.format(Calendar.getInstance().getTime())

            val transaccion = Transaccion()
            transaccion.TransaccionId=0
            transaccion.TerceroId=0
            transaccion.Concepto=viewDialog?.txtConceptoVenta?.text.toString()
            transaccion.EstadoId=estadoTransaccionGlobal?.Id
            transaccion.Fecha_Transaccion= Calendar.getInstance().getTime()
            transaccion.FechaString= FechaString
            transaccion.NaturalezaId=NaturalezaTransaccionResources.CREDITO
            transaccion.PucId=pukGlobal?.Id
            transaccion.Descripcion_Puk=pukGlobal?.Descripcion
            transaccion.Nombre_Tercero=viewDialog?.txtNombreCliente?.text.toString()
            transaccion.Identificacion_Tercero=viewDialog?.txtIdentificacionCliente?.text.toString()
            transaccion.Valor_Unitario=viewDialog?.txtPrecioTransaccion?.text.toString().toDoubleOrNull()
            transaccion.Valor_Total=valorTotalGlobal
            transaccion.Cantidad=viewDialog?.txtCantidadTransaccion?.text.toString().toDoubleOrNull()
            transaccion.Cultivo_Id=cultivoGlobal?.CultivoId
            transaccion.Nombre_Cultivo=cultivoGlobal?.Nombre
            transaccion.Nombre_Detalle_Producto_Cultivo=cultivoGlobal?.Nombre_Detalle_Tipo_Producto
            transaccion.CategoriaPuk_Id= typeTransaccion
            transaccion.Nombre_Estado_Transaccion=estadoTransaccionGlobal?.Nombre
            presenter?.registerTransaccion(transaccion,Cultivo_Id!! )
        }
    }

    override fun updateTransaccion(transaccion: Transaccion) {
        if (presenter?.validarCampos() == true) {

            //val transaccion = Transaccion()

            transaccion.Nombre_Cultivo=cultivoGlobal?.Nombre
            transaccion.Concepto=viewDialog?.txtConceptoVenta?.text.toString()
            transaccion.EstadoId=estadoTransaccionGlobal?.Id
            transaccion.NaturalezaId=NaturalezaTransaccionResources.CREDITO
            transaccion.PucId=pukGlobal?.Id
            transaccion.Descripcion_Puk=pukGlobal?.Descripcion
            transaccion.Nombre_Tercero=viewDialog?.txtNombreCliente?.text.toString()
            transaccion.Identificacion_Tercero=viewDialog?.txtIdentificacionCliente?.text.toString()
            transaccion.Valor_Unitario=viewDialog?.txtPrecioTransaccion?.text.toString().toDoubleOrNull()
            transaccion.Valor_Total=valorTotalGlobal
            transaccion.Cantidad=viewDialog?.txtCantidadTransaccion?.text.toString().toDoubleOrNull()
            transaccion.Cultivo_Id=cultivoGlobal?.CultivoId
            transaccion.Nombre_Detalle_Producto_Cultivo=cultivoGlobal?.Nombre_Detalle_Tipo_Producto
            transaccion.CategoriaPuk_Id= typeTransaccion
            transaccion.Nombre_Estado_Transaccion=estadoTransaccionGlobal?.Nombre
            presenter?.updateTransaccion(transaccion,Cultivo_Id!! )
        }
    }

    override fun setListTransaccion(transaccion: List<Transaccion>) {
        adapter?.clear()
        produccionList?.clear()
        adapter?.setItems(transaccion)
        hideProgress()
        setResults(transaccion.size)
    }

    override fun setResults(transacciones: Int) {
        var results = String.format(getString(R.string.results_global_search),
                transacciones);
        txtResults.setText(results);
    }

    override fun requestResponseOK() {
        if(_dialogRegisterUpdate!=null){
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }



    override fun requestResponseItemOK(string1:String?, string2:String?) {


        Toast.makeText(context,string1+" - "+string2,Toast.LENGTH_SHORT).show()
    }

    override fun requestResponseError(error: String?) {
        if(_dialogRegisterUpdate!=null){
            _dialogRegisterUpdate?.dismiss()
        }
        onMessageError(R.color.red_900,error);
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


    override fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?) {
        if(viewDialogFilter!=null){
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(null)
            var unidadProductivaArrayAdapter = ArrayAdapter<Unidad_Productiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))

                viewDialogFilter?.spinnerCultivo?.setText("")
                viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))

                unidadProductivaGlobal= listUnidadProductiva!![position] as Unidad_Productiva
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
        var loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, listLotes)
        viewDialogFilter?.spinnerLote!!.setAdapter(loteArrayAdapter)
        viewDialogFilter?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            viewDialogFilter?.spinnerCultivo?.setText("")
            viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
            loteGlobal= listLotes!![position] as Lote
            presenter?.setListSpinnerCultivo(loteGlobal?.LoteId)
        }
    }

    override fun setListCultivos(listCultivos: List<Cultivo>?) {
        viewDialogFilter?.spinnerCultivo!!.setAdapter(null)
        //viewDialogFilter?.spinnerCultivo?.setText("")
        //viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
        val cultivoArrayAdapter = ArrayAdapter<Cultivo>(activity, android.R.layout.simple_spinner_dropdown_item, listCultivos)
        viewDialogFilter?.spinnerCultivo!!.setAdapter(cultivoArrayAdapter)
        viewDialogFilter?.spinnerCultivo!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            cultivoGlobal= listCultivos!![position] as Cultivo
            Cultivo_Id=cultivoGlobal?.CultivoId
        }
    }

    override fun setCultivo(cultivo: Cultivo?) {
        if(cultivoSeletedContainer.visibility==View.GONE){
            cultivoSeletedContainer.visibility=View.VISIBLE
        }
        txtNombreCultivo.setText(cultivo?.getNombreCultio())
        txtNombreUnidadProductiva.setText(cultivo?.NombreUnidadProductiva)

    }

    override fun setListPuk(listPuk:List<Puk>?){
        if( viewDialog!=null){
            viewDialog?.spinnerPuk?.setAdapter(null)
            //viewDialogFilter?.spinnerCultivo?.setText("")
            //viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
            val cultivoArrayAdapter = ArrayAdapter<Puk>(activity, android.R.layout.simple_spinner_dropdown_item, listPuk)
            viewDialog?.spinnerPuk?.setAdapter(cultivoArrayAdapter)
            viewDialog?.spinnerPuk?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                pukGlobal= listPuk!![position] as Puk
            }
        }
    }

    override fun showAlertDialogAddTransaccion(transaccion: Transaccion?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.diaog_form_transacciones, null)
        presenter?.setListSpinnerPuk(typeTransaccion)
        viewDialog?.ivClosetDialogVentas?.setOnClickListener(this)
        viewDialog?.changeCultivo?.setOnClickListener(this)
        viewDialog?.fabAddVenta?.setOnClickListener(this)



        //Venta
        viewDialog?.changeCultivo?.setOnClickListener(this)
        viewDialog?.fabCancelVenta?.setOnClickListener(this)


        //radioGroup
       var list:List<Estado_Transaccion>?=ArrayList<Estado_Transaccion>()
        if(CategoriaPukResources.INGRESO== typeTransaccion){
             list= SQLite.select().from(Estado_Transaccion::class.java).where(Estado_Transaccion_Table.Nombre.notEq(EstadoTransaccionResources.POR_PAGAR)).queryList()
        }else{
             list= SQLite.select().from(Estado_Transaccion::class.java).where(Estado_Transaccion_Table.Nombre.notEq(EstadoTransaccionResources.POR_COBRAR)).queryList()
        }

      //  var list= SQLite.select().from(Estado_Transaccion::class.java).queryList()

        for( item in list){
            var id =item.Id
            val rb = RadioButton(this.context)
            rb.id = id!!.toInt()
            rb.setText(item.Nombre)
           // var lpView =  LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1f)
           // rb.layoutParams=lpView
            rb.setOnClickListener {
                var data: Estado_Transaccion = list?.filter { s -> s.Id ==rb.id.toLong()}!!.single()
                estadoTransaccionGlobal=data
            }
            if (transaccion != null) {
                if(item.Nombre?.toLowerCase()?.contains(transaccion.Nombre_Estado_Transaccion!!.toLowerCase())!!){
                    rb.isChecked=true;
                }
            }
            viewDialog?.radioGroupEstados?.addView(rb)
        }
        /*viewDialog?.package_lst?.layoutManager = GridLayoutManager(activity, 3)
        estadoTransaccionList?.clear()
        adapterEstadotransaccion = EstadoTransaccionAdapter(estadoTransaccionList!!)
        viewDialog?.package_lst?.adapter = adapterEstadotransaccion
         adapterEstadotransaccion?.setItems(Listas.listEstadoTransaccion())
        */

       // viewDialog?.txtCantidadTransaccion?.addTextChangedListener(wac:TextWatcher)


        viewDialog?.changeCultivo?.setText(cultivoGlobal?.getNombreCultio())
        transaccionGlobal=transaccion

        var titleDialog:String?=null

        //REGISTER
        if (transaccion == null) {

            if(CategoriaPukResources.INGRESO== typeTransaccion){
                titleDialog=getString(R.string.title_add_venta)
            }else if(CategoriaPukResources.GASTO== typeTransaccion){
                titleDialog=getString(R.string.title_add_gasto)
            }
            viewDialog?.txtTitle?.setText(titleDialog)
        }
        //UPDATE
        else {
            estadoTransaccionGlobal=SQLite.select().from(Estado_Transaccion::class.java).where(Estado_Transaccion_Table.Id.eq(transaccion.EstadoId)).querySingle()
            //cultivoGlobal = Cultivo(transaccion.Cultivo_Id, "", 0, 0.0, null, null, 0, transaccion.Nombre_Cultivo, null, Nombre_Detalle_Tipo_Producto = transaccion.Nombre_Detalle_Producto_Cultivo)
            cultivoGlobal =SQLite.select().from(Cultivo::class.java).where(Cultivo_Table.CultivoId.eq(transaccion.Cultivo_Id)).querySingle()
            pukGlobal = SQLite.select().from(Puk::class.java).where(Puk_Table.Id.eq(transaccion.PucId)).querySingle()

            if(CategoriaPukResources.INGRESO== typeTransaccion){
                titleDialog=getString(R.string.title_edit_venta)
            }else if(CategoriaPukResources.GASTO== typeTransaccion){
                titleDialog=getString(R.string.title_edit_gasto)
            }
            viewDialog?.txtTitle?.setText(titleDialog)

            var cantidad=transaccion.Cantidad?.toLong()

            viewDialog?.txtCantidadTransaccion?.setText(cantidad.toString())
            viewDialog?.txtPrecioTransaccion?.setText(String.format(context!!.getString(R.string.price_empty_signe),
                    transaccion.Valor_Unitario))
            viewDialog?.txtValorSubtotal?.setText(String.format(context!!.getString(R.string.price),
                    transaccion.Valor_Total))

            viewDialog?.changeCultivo?.setText(cultivoGlobal?.getNombreCultio())

            Cultivo_Id=transaccion.Cultivo_Id
            viewDialog?.txtTotalTransaccion?.setText( String.format(context!!.getString(R.string.price),
                    transaccion.Valor_Total))
            viewDialog?.txtNombreCliente?.setText(transaccion.Nombre_Tercero)
            viewDialog?.txtIdentificacionCliente?.setText(transaccion.Identificacion_Tercero)
            viewDialog?.txtConceptoVenta?.setText(transaccion.Concepto)
            viewDialog?.spinnerPuk?.setText(transaccion.Descripcion_Puk)
            valorTotalGlobal= transaccion.Valor_Total

        }

        viewDialog?.txtCantidadTransaccion?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                var cantidad=  viewDialog?.txtCantidadTransaccion?.text.toString()?.toLongOrNull()
                var precioVenta=  viewDialog?.txtPrecioTransaccion?.text.toString()?.toDoubleOrNull()

                if(!viewDialog?.txtCantidadTransaccion?.text.toString().isEmpty()
                        && !viewDialog?.txtPrecioTransaccion?.text.toString().isEmpty()){

                    var subtotal=cantidad!!*precioVenta!!
                    var costo_total_item = String.format(context!!.getString(R.string.price),
                            subtotal)

                    var costo_total_ = String.format(context!!.getString(R.string.price),
                            subtotal)
                    valorTotalGlobal=subtotal
                    viewDialog?.txtValorSubtotal?.text=costo_total_item
                    viewDialog?.txtTotalTransaccion?.setText(costo_total_.replace(",", "."))

                }else{
                    viewDialog?.txtValorSubtotal?.text=""
                    viewDialog?.txtTotalTransaccion?.setText("")
                }
            }
        })

        viewDialog?.txtPrecioTransaccion?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                var cantidad=  viewDialog?.txtCantidadTransaccion?.text.toString()?.toDoubleOrNull()
                var precioVenta=  viewDialog?.txtPrecioTransaccion?.text.toString()?.toDoubleOrNull()


                if(!viewDialog?.txtCantidadTransaccion?.text.toString().isEmpty()
                        && !viewDialog?.txtPrecioTransaccion?.text.toString().isEmpty()){


                    var subtotal=cantidad!!*precioVenta!!
                    var costo_total_item = String.format(context!!.getString(R.string.price),
                            subtotal)

                    var costo_total_ = String.format(context!!.getString(R.string.price),
                            subtotal)

                    valorTotalGlobal=subtotal

                    viewDialog?.txtValorSubtotal?.text=costo_total_item
                    viewDialog?.txtTotalTransaccion?.setText(costo_total_.replace(",", "."))
                }else{
                    viewDialog?.txtValorSubtotal?.text=""
                    viewDialog?.txtTotalTransaccion?.setText("")
                }
            }
        })

        val dialog = AlertDialog.Builder(context!!,android.R.style.Theme_Light_NoTitleBar)
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
        _dialogRegisterUpdate=dialog
    }


    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_contabilidad_color_500);
        return builder.show();
    }

    override fun confirmDelete(transaccion: Transaccion): AlertDialog? {
        var builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
        })
        builder.setMessage(getString(R.string.title_alert_delete_produccion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            presenter?.deleteTransaccion(transaccion, Cultivo_Id)
        })
        builder.setIcon(R.drawable.ic_contabilidad_color_500);
        return builder.show();
    }

    override fun showAlertDialogFilterTransaccion(isFilter: Boolean?) {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()

        var title:String?=null
        if(isFilter==true){
            title=getString(R.string.tittle_filter)
        }else{
            title=getString(R.string.tittle_select_cultivo_transaccion)
        }

        if(unidadProductivaGlobal!=null && loteGlobal!=null && cultivoGlobal!=null){
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Unidad_Productiva_Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.LoteId)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.Nombre)
            viewDialogFilter?.spinnerCultivo?.setText(cultivoGlobal?.getNombreCultio())

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
                .iconRes(R.drawable.ic_contabilidad_color_500)
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
                                if(presenter?.validarListasAddTransaccion()==true){
                                    dialog1.dismiss()
                                    presenter?.getListTransaccion(Cultivo_Id, typeTransaccion)
                                    presenter?.getCultivo(Cultivo_Id)
                                }
                            }else{
                                if(changeCultivo==true){
                                    viewDialog?.changeCultivo?.setText(cultivoGlobal?.getNombreCultio())
                                    dialog1.dismiss()
                                }else{
                                    if(presenter?.validarListasAddTransaccion()==true){
                                        dialog1.dismiss()
                                        showAlertDialogAddTransaccion(null)
                                    }
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
        _dialogFilter=dialog
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    //endregion


    //region EVENTS

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabAddTransaccion -> {
                showAlertDialogFilterTransaccion(false)
            }
            R.id.fabCancelVenta->_dialogRegisterUpdate?.dismiss()

            R.id.ivClosetDialogVentas->_dialogRegisterUpdate?.dismiss()

            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }


            R.id.ivCloseButtonDialogFilter -> {
                _dialogFilter?.dismiss()
            }



            R.id.searchFilter -> {
                changeCultivo=false
                showAlertDialogFilterTransaccion(true)
            }

            R.id.changeCultivo -> {
                changeCultivo=true
                showAlertDialogFilterTransaccion(false)
            }

            R.id.changeCultivo -> {
                showAlertDialogFilterTransaccion(true)
            }

            R.id.fabAddVenta->{
                if(transaccionGlobal!=null){
                    updateTransaccion(transaccionGlobal!!)
                }else{
                    registerTransaccion()
                    presenter?.getCultivo(Cultivo_Id)
                }
            }

        }
    }


    //endregion


    //region OVERRIDES METHODS
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }


    override fun onRefresh() {
        showProgress()
        presenter?.getListTransaccion(Cultivo_Id, typeTransaccion)
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
