package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes


import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_reporte.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.models.ventas.Puk
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.adapter.CategoriaPukAdapter
import com.kaopiz.kprogresshud.KProgressHUD
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import java.util.*
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.interedes.agriculturappv3.productor.models.ventas.resports.BalanceContable
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
import kotlinx.android.synthetic.main.content_range_dates.view.*
import java.text.SimpleDateFormat

class ReporteFragment : Fragment(), View.OnClickListener , SwipeRefreshLayout.OnRefreshListener, IMainViewReportes.MainView {



    var presenter: IMainViewReportes.Presenter? = null

    //Progress
    private var hud: KProgressHUD?=null

    //Listas
    var cultivoGlobal: Cultivo?=null
    var Cultivo_Id: Long? = null
    var pukGlobal: Puk?=null
    var unidadProductivaGlobal: UnidadProductiva?=null
    var loteGlobal: Lote?=null
    var transaccionGlobal: Transaccion?=null

    var categoriasReportList:ArrayList<CategoriaPuk>?=ArrayList<CategoriaPuk>()

    //Adapter
    var adapter: CategoriaPukAdapter?=null

    //Dialog
    var viewDialog:View?= null
    var viewDialogFilter:View?= null
    var _dialogFilter: MaterialDialog? = null

    var viewDialogDates:View?= null
    var _dialogDates: MaterialDialog? = null

    //Range Dates
    var dateTime = Calendar.getInstance()
    var fechaInicio: Date?=null
    var fechaFin: Date?=null
    var confFecha:Boolean?=false

    var isFilterAllCultivos:Boolean?=true

    companion object {
        var instance:  ReporteFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance =this
        presenter = ReportePresenter(this);
        presenter?.onCreate();
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reporte, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        // RecyclerView has some built in animations to it, using the DefaultItemAnimator.
        // Specifically when you call notifyItemChanged() it does a fade animation for the changing
        // of the data in the ViewHolder. If you would like to disable this you can use the following:
        val animator = recycler_view.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        //initAdapter()
        presenter?.getTotalTransacciones(null,null,null)
        ivBackButton.setOnClickListener(this)
        searchFilter.setOnClickListener(this)
        fabChangeDatesReport.setOnClickListener(this)
    }

    //region ADAPTER
    private fun initAdapter() {


        /*val collection = ArrayList<CategoriaPuk>()
        val layoutManager = LinearLayoutManager(activity)
        adapter = CategoriaPukAdapter(collection)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter*/


/*
        val layoutManager = LinearLayoutManager(activity)
        adapter = CategoriaPukAdapter(GenreDataFactory.makeGenres())
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter

        toggle_button.setOnClickListener { adapter!!.toggleGroup(GenreDataFactory.makeClassicGenre()) }*/
    }
    //endregion


    //region IMPLEMENTS INTERFACE

    override fun setBalanceContable(balanceContable: BalanceContable?) {
        txtTotalIngtresosReporte.setText(String.format(context?.getString(R.string.price)!!,balanceContable?.Ingresos))
        txtTotalEgresosReporte.setText(String.format(context?.getString(R.string.price)!!,balanceContable?.Egresos))
        txtTotalBalanceReporte.setText(String.format(context?.getString(R.string.price)!!,balanceContable?.Balance))
    }

    override fun validarCampos(): Boolean {
       return false
    }

    override fun validarListasFilterReports(): Boolean {
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

    override fun validarDatesSelect(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialogDates?.txtDateStarReport?.text.toString().isEmpty() ) {
            viewDialogDates?.txtDateStarReport?.setError(getString(R.string.error_field_required))
            focusView = viewDialogDates?.txtDateStarReport
            cancel = true
        } else if (viewDialogDates?.txtDateEndReport?.text.toString().isEmpty()) {
            viewDialogDates?.txtDateEndReport?.setError(getString(R.string.error_field_required))
            focusView =viewDialogDates?.txtDateEndReport
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

    }

    override fun disableInputs() {

    }

    override fun enableInputs() {

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





    override fun showProgress() {
        showProgressHud()
       // swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        hideProgressHud()
        //swipeRefreshLayout.setRefreshing(false);
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
    override fun setResults(transacciones: Int) {
        var results = String.format(getString(R.string.results_global_search),
                transacciones);
        txtResults.setText(results);
    }

    override fun requestResponseOK() {
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
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

    override fun requestResponseItemOK(string1: String?, string2: String?) {

    }


    //region METHODS VIEWS
    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?) {
        if(viewDialogFilter!=null){
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(null)
            var unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))

                viewDialogFilter?.spinnerCultivo?.setText("")
                viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))

                unidadProductivaGlobal= listUnidadProductiva!![position] as UnidadProductiva
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
        var loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, listLotes)
        viewDialogFilter?.spinnerLote!!.setAdapter(loteArrayAdapter)
        viewDialogFilter?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            viewDialogFilter?.spinnerCultivo?.setText("")
            viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
            loteGlobal= listLotes!![position] as Lote
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
            cultivoGlobal= listCultivos!![position] as Cultivo
            Cultivo_Id=cultivoGlobal?.Id
        }
    }

    override fun setCultivo(cultivo: Cultivo?) {

        if(cultivo!=null){
            if(cultivoSeletedContainer.visibility==View.GONE){
                cultivoSeletedContainer.visibility=View.VISIBLE
            }
            txtNombreCultivo.setText(cultivo?.getNombreCultio())
            txtNombreUnidadProductiva.setText(cultivo?.NombreUnidadProductiva)
        }else{
            if(cultivoSeletedContainer.visibility==View.VISIBLE){
                cultivoSeletedContainer.visibility=View.GONE
            }
        }
    }

    override fun setListReportCategoriasPuk(categoriaList: List<CategoriaPuk>?) {
        adapter?.clear()
        categoriasReportList?.clear()


      //  adapter?.setItems(categoriaList as List<ExpandableGroup<*>>)
        val layoutManager = LinearLayoutManager(activity)
        adapter = CategoriaPukAdapter(categoriaList as List<ExpandableGroup<*>>)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter

        ///toggle_button.setOnClickListener { adapter!!.toggleGroup(GenreDataFactory.makeClassicGenre()) }


        hideProgress()
        setResults(categoriaList!!.size)
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    override fun showAlertDialogFilterReports() {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()

        var title:String?=null

        title=getString(R.string.title_filter_report)



        viewDialogFilter?.content_filter_reports?.visibility=View.VISIBLE
        viewDialogFilter?.content_list_filter?.visibility=View.GONE

        if(isFilterAllCultivos==true){
            viewDialogFilter?.content_list_filter?.visibility=View.GONE
            viewDialogFilter?.radioFiltersByAllCultivo?.isChecked=true
        }else{
            viewDialogFilter?.content_list_filter?.visibility=View.VISIBLE
            viewDialogFilter?.radioFilterByCultivo?.isChecked=true
        }



        if(unidadProductivaGlobal!=null && loteGlobal!=null && cultivoGlobal!=null){
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.Id)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.Nombre)
            viewDialogFilter?.spinnerCultivo?.setText(cultivoGlobal?.getNombreCultio())

        }

        //Set Events
        viewDialogFilter?.ivCloseButtonDialogFilter?.setOnClickListener(this)
        viewDialogFilter?.radioFiltersByAllCultivo?.setOnClickListener(this)
        viewDialogFilter?.radioFilterByCultivo?.setOnClickListener(this)










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
                            if(viewDialogFilter?.content_list_filter?.visibility==View.GONE){
                                dialog1.dismiss()
                            }else{
                                if(presenter?.validarListasFilterReports()==true){
                                    dialog1.dismiss()
                                    presenter?.getTotalTransacciones(Cultivo_Id,fechaInicio,fechaFin)
                                    presenter?.getCultivo(Cultivo_Id)
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

    override fun showAlertDialogSelectDate() {
        val inflater = this.layoutInflater
        viewDialogDates = inflater.inflate(R.layout.content_range_dates, null)

        var title:String?=null
        title=getString(R.string.tittle_filter)

        if(fechaInicio!=null && fechaFin!=null){
            val format = SimpleDateFormat("dd/MM/yyyy")
            viewDialogDates?.txtDateEndReport?.setText( format.format(fechaFin))
            viewDialogDates?.txtDateStarReport?.setText( format.format(fechaInicio))
        }


        viewDialogDates?.txtDateStarReport?.setOnClickListener(this)
        viewDialogDates?.txtDateEndReport?.setOnClickListener(this)

        //Set Events
        viewDialogDates?.ivCloseButtonDialogFilter?.setOnClickListener(this)

        val dialog = MaterialDialog.Builder(activity!!)
                .title(title)
                .customView(viewDialogDates!!, true)
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
                            if (presenter?.validarDatesSelect() == true) {
                               dialog1.dismiss()
                                presenter?.getTotalTransacciones(Cultivo_Id,fechaInicio,fechaFin)
                            }
                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                })
                .build()
         dialog.show()
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _dialogDates=dialog
    }

    //endregion


    //region Events
    override fun onClick(v: View?) {
        when (v?.id) {


            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }


            R.id.ivCloseButtonDialogFilter -> {
                _dialogFilter?.dismiss()
            }

            R.id.searchFilter -> {
               showAlertDialogFilterReports()
            }

            R.id.fabChangeDatesReport -> {
                showAlertDialogSelectDate()
            }

            R.id.txtDateStarReport -> {
                confFecha=true
                updateDate()
            }
            R.id.txtDateEndReport -> {
                confFecha=false
                updateDate()
            }

            R.id.radioFiltersByAllCultivo -> {
                viewDialogFilter?.content_list_filter?.visibility=View.GONE
                presenter?.getTotalTransacciones(null,fechaInicio,fechaFin)
                setCultivo(null)
                isFilterAllCultivos=true
            }
            R.id.radioFilterByCultivo -> {
                isFilterAllCultivos=false
                viewDialogFilter?.content_list_filter?.visibility=View.VISIBLE
            }
        }
    }



    //endregion



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
        val format1 = SimpleDateFormat("dd/MM/yyyy")
        val formatted = format1.format(dateTime.time)
        fechaInicio=dateTime.time
        viewDialogDates?.txtDateStarReport?.setText(formatted)
        txtFechaInicioReporte.setText(formatted)
    }

    private fun mostrarResultadosFechaFin() {
        val format1 = SimpleDateFormat("dd/MM/yyyy")
        val formatted = format1.format(dateTime.time)
        fechaFin=dateTime.time
        viewDialogDates?.txtDateEndReport?.setText(formatted)
        txtFechaFinReporte.setText(formatted)
    }



    //region OVERRIDES METHODS
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }


    override fun onRefresh() {
        showProgress()
        //presenter?.getListTransaccion(Cultivo_Id, typeTransaccion)
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
