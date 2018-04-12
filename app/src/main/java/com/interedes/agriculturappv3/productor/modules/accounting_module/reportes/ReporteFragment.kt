package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes


import android.content.DialogInterface
import android.content.Intent
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
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.Lote
import com.interedes.agriculturappv3.productor.models.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.models.ventas.Puk
import com.interedes.agriculturappv3.productor.models.ventas.Transaccion
import com.interedes.agriculturappv3.productor.models.ventas.resports.GenreDataFactory
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.adapter.CategoriaPukAdapter
import com.kaopiz.kprogresshud.KProgressHUD
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import java.util.*
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class ReporteFragment : Fragment(), View.OnClickListener , SwipeRefreshLayout.OnRefreshListener, IMainViewReportes.MainView {



    var presenter: IMainViewReportes.Presenter? = null

    //Progress
    private var hud: KProgressHUD?=null

    //Listas
    var cultivoGlobal: Cultivo?=null
    var pukGlobal: Puk?=null
    var unidadProductivaGlobal: UnidadProductiva?=null
    var loteGlobal: Lote?=null
    var transaccionGlobal: Transaccion?=null

    var categoriasReportList:ArrayList<CategoriaPuk>?=ArrayList<CategoriaPuk>()

    //Adapter
    var adapter: CategoriaPukAdapter?=null

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

        initAdapter()

        presenter?.getTotalTransacciones(null)

    }

    //region ADAPTER
    private fun initAdapter() {
        val collection = ArrayList<CategoriaPuk>()
        val layoutManager = LinearLayoutManager(activity)
        adapter = CategoriaPukAdapter(collection)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter


/*
        val layoutManager = LinearLayoutManager(activity)
        adapter = CategoriaPukAdapter(GenreDataFactory.makeGenres())
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter

        toggle_button.setOnClickListener { adapter!!.toggleGroup(GenreDataFactory.makeClassicGenre()) }*/
    }
    //endregion


    //region IMPLEMENTS INTERFACE

    override fun validarCampos(): Boolean {
       return false
    }

    override fun validarListasAddTransaccion(): Boolean {
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

    }

    override fun requestResponseOK() {

    }

    override fun requestResponseError(error: String?) {

    }

    override fun onMessageOk(colorPrimary: Int, msg: String?) {

    }

    override fun onMessageError(colorPrimary: Int, msg: String?) {

    }

    override fun requestResponseItemOK(string1: String?, string2: String?) {

    }


    //region METHODS VIEWS
    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?) {

    }

    override fun setListLotes(listLotes: List<Lote>?) {

    }

    override fun setListCultivos(listCultivos: List<Cultivo>?) {

    }

    override fun setCultivo(cultivo: Cultivo?) {

    }

    override fun setListReportCategoriasPuk(categoriaList: List<CategoriaPuk>?) {
        adapter?.clear()
        categoriasReportList?.clear()
        adapter?.setItems(categoriaList as List<ExpandableGroup<*>>)
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

    override fun showAlertDialogFilterReports(isFilter: Boolean?) {

    }

    //endregion


    //region Events
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabExportPdf -> {

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
