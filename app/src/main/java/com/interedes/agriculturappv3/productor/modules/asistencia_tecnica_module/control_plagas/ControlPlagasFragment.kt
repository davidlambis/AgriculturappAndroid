package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas


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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.unidad_productiva.UnidadProductiva
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.adapters.ControlPlagasAdapter
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.plagas.PlagaFragment
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.content_recyclerview.*
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
import kotlinx.android.synthetic.main.fragment_control_plagas.*
import java.util.*


class ControlPlagasFragment : Fragment(), IControlPlagas.View, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {


    var presenter: IControlPlagas.Presenter? = null
    var controlPlagaList: ArrayList<ControlPlaga>? = ArrayList<ControlPlaga>()
    var Cultivo_Id: Long? = null
    var adapter: ControlPlagasAdapter? = null
    var viewDialogFilter: View? = null

    var _dialogFilter: MaterialDialog? = null
    var dialogo: AlertDialog? = null

    //Listas
    var cultivoGlobal: Cultivo? = null
    var unidadProductivaGlobal: UnidadProductiva? = null
    var loteGlobal: Lote? = null


    companion object {
        var instance: ControlPlagasFragment? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_control_plagas, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ControlPlagasFragment.instance = this
        presenter = ControlPlagasPresenter(this)
        presenter?.onCreate()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val c = this.arguments
        if (c != null) {
            Cultivo_Id = c.getLong("cultivoId")
        }
        initAdapter()
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_control_plagas)
        ivBackButton?.setOnClickListener(this)
        searchFilter?.setOnClickListener(this)
        swipeRefreshLayout?.setOnRefreshListener(this)
        fabAddControlPlaga?.setOnClickListener(this)
        setupInjection()
    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = ControlPlagasAdapter(controlPlagaList!!)
        recyclerView?.adapter = adapter
    }

    private fun setupInjection() {
        presenter?.getListControlPlaga(Cultivo_Id)

    }


    //region Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
            R.id.searchFilter -> {
                showAlertDialogFilterControlPlaga()
            }
            R.id.ivCloseButtonDialogFilter -> {
                _dialogFilter?.dismiss()
            }
            R.id.fabAddControlPlaga -> {
                (activity as MenuMainActivity).replaceFragment(PlagaFragment())
            }
        }
    }
    //endregion

    //region Métodos Interfaz

    override fun validarListasFilter(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialogFilter?.spinnerUnidadProductiva?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerUnidadProductiva?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerUnidadProductiva
            cancel = true
        } else if (viewDialogFilter?.spinnerLote?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerLote?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerLote
            cancel = true
        } else if (viewDialogFilter?.spinnerCultivo?.text.toString().isEmpty()) {
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

    override fun showProgress() {
        swipeRefreshLayout.setRefreshing(true)
    }

    override fun hideProgress() {
        swipeRefreshLayout.setRefreshing(false)
    }

    override fun setListControlPlagas(listControlPlagas: List<ControlPlaga>) {
        dialogo?.dismiss()
        adapter?.clear()
        controlPlagaList?.clear()
        adapter?.setItems(listControlPlagas)
        hideProgress()
        setResults(listControlPlagas.size)
    }

    override fun setResults(controlPlagas: Int) {
        val results = String.format(getString(R.string.results_global_search),
                controlPlagas)
        txtResults.setText(results)
    }

    override fun setCultivo(cultivo: Cultivo?) {
        if (cultivoSeletedContainer.visibility == View.GONE) {
            cultivoSeletedContainer.visibility = View.VISIBLE
        }
        txtNombreCultivo.setText(cultivo?.Nombre)
        txtNombreDetalleTipoProducto.setText(cultivo?.Nombre_Tipo_Producto)
        txtPrecioProducto.setText(cultivo?.FechaIncio)
        txtFechaDisponibilidadProducto.setText(cultivo?.FechaFin)
    }

    override fun updatePlaga(controlPlaga: ControlPlaga?) {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
        })
        builder.setMessage(getString(R.string.title_alert_erradicar_plaga))
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            controlPlaga?.EstadoErradicacion = true
            /*val format1 = SimpleDateFormat("MM/dd/yyyy")
            val formatted = format1.format(Calendar.getInstance().time)*/
            controlPlaga?.Fecha_Erradicacion = Calendar.getInstance().time
            presenter?.updateControlPlaga(controlPlaga)
            presenter?.getListControlPlaga(Cultivo_Id)
        })
        builder.setIcon(R.drawable.ic_plagas)
        dialogo = builder.show()
    }

    override fun confirmDelete(controlPlaga: ControlPlaga): AlertDialog? {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation))
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

        })
        builder.setMessage(getString(R.string.title_alert_control_plaga))
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            presenter?.deleteControlPlaga(controlPlaga, Cultivo_Id)
        })
        builder.setIcon(R.drawable.ic_plagas)
        dialogo = builder.show()
        return dialogo
    }

    override fun showAlertDialogFilterControlPlaga() {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()

        var title: String? = null
        title = getString(R.string.tittle_filter)

        if (unidadProductivaGlobal != null && loteGlobal != null && cultivoGlobal != null) {
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.Id)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.Nombre)
            viewDialogFilter?.spinnerCultivo?.setText(cultivoGlobal?.Nombre)

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
                .iconRes(R.drawable.ic_plagas)
                .dividerColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .autoDismiss(false)
                //.negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onPositive(
                        { dialog1, which ->
                            /* if (presenter?.validarListasAddProduccion() == true) {
                                 dialog1.dismiss()
                                 //presenter?.getListProduccion(Cultivo_Id)
                                // presenter?.getCultivo(Cultivo_Id)
                             } */
                            if (presenter?.validarListasFilter() == true) {
                                dialog1.dismiss()
                                presenter?.getListControlPlaga(Cultivo_Id)
                                presenter?.getCultivo(Cultivo_Id)
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
        _dialogFilter = dialog
    }

    override fun setListUnidadProductiva(listUnidadProductiva: List<UnidadProductiva>?) {
        if (viewDialogFilter != null) {
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(null)
            val unidadProductivaArrayAdapter = ArrayAdapter<UnidadProductiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))

                viewDialogFilter?.spinnerCultivo?.setText("")
                viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))

                unidadProductivaGlobal = listUnidadProductiva!![position] as UnidadProductiva
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
        val loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_spinner_dropdown_item, listLotes)
        viewDialogFilter?.spinnerLote!!.setAdapter(loteArrayAdapter)
        viewDialogFilter?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            viewDialogFilter?.spinnerCultivo?.setText("")
            viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
            loteGlobal = listLotes!![position]
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
            cultivoGlobal = listCultivos!![position]
            Cultivo_Id = cultivoGlobal?.Id
        }
    }

    //Escuchador de eventos
    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    override fun verificateConnection(): AlertDialog? {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.verificate_conexion))
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_plagas)
        return builder.show()
    }

    override fun requestResponseOK() {
        if (dialogo != null) {
            dialogo?.dismiss()
        }
        onMessageOk(R.color.colorPrimary, getString(R.string.request_ok))
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
    //endregion

    //region ciclo de vida
    override fun onRefresh() {
        showProgress()
        presenter?.getListControlPlaga(Cultivo_Id)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
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
