package com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.tratamiento


import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.cultivo.Cultivo
import com.interedes.agriculturappv3.productor.models.lote.Lote
import com.interedes.agriculturappv3.productor.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.productor.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.insumos.Insumo
import com.interedes.agriculturappv3.productor.models.insumos.Insumo_Table
import com.interedes.agriculturappv3.productor.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.ControlPlagasFragment
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.dialog_form_control_plaga.view.*
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
import kotlinx.android.synthetic.main.fragment_tratamiento.*
import java.util.*


class TratamientoFragment : Fragment(), ITratamiento.View, View.OnClickListener {


    //Variables globales

    var insumoId: Long? = 0
    var tipoEnfermedadId: Long? = 0
    var enfermedadId: Long? = 0
    var nombreTipoEnfermedad: String? = null
    var nombreTipoProducto: String? = null
    var viewDialogFilter: View? = null
    var unidadProductivaGlobal: Unidad_Productiva? = null
    var loteGlobal: Lote? = null
    var tipoProductoId: Long? = 0
    var cultivoGlobal: Cultivo? = null
    var Cultivo_Id: Long? = null
    var _dialogFilter: MaterialDialog? = null
    var viewDialog: View? = null
    var unidadMedidaGlobal: Unidad_Medida? = null
    var _dialogRegisterControlPlaga: AlertDialog? = null
    var tratamientoId: Long? = null
    var fechaAplicacion: Date? = null

    //Presenter
    var presenter: ITratamiento.Presenter? = null


    //Progress
    private var hud: KProgressHUD? = null

    var dateTime = Calendar.getInstance()


    companion object {
        var instance: TratamientoFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TratamientoFragment.instance = this
        presenter = TratamientoPresenter(this)
        presenter?.onCreate()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tratamiento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val c = this.arguments
        if (c != null) {
            tratamientoId = c.getLong("tratamientoId")
            insumoId = c.getLong("insumoId")
            tipoProductoId = c.getLong("tipoProductoId")
            enfermedadId = c.getLong("enfermedadId")
            nombreTipoEnfermedad = c.getString("nombreTipoEnfermedad")
        }
        setupInjection()
        ivBackButton?.setOnClickListener(this)
        btnControlPlaga?.setOnClickListener(this)
    }

    private fun setupInjection() {
        presenter?.getTratamiento(tratamientoId)
    }

    //region Métodos Interfaz

    override fun showProgress() {
        showProgressHud()
    }

    override fun hideProgress() {

    }

    override fun enableInputs() {
        setInputs(true)
    }

    override fun disableInputs() {
        setInputs(false)
    }

    private fun setInputs(b: Boolean) {
        //viewDialog?.txtFechaAplicacion?.isEnabled = b
        viewDialog?.txtDosis?.isEnabled = b
        viewDialog?.spinnerUnidadMedidaDosis?.isEnabled = b
    }

    override fun showProgressHud() {
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
        hud?.show()
    }

    override fun hideProgressHud() {
        hud?.dismiss()
        _dialogRegisterControlPlaga?.dismiss()
    }

    override fun loadControlPlagas(listControlPlaga: List<ControlPlaga>) {
        hideProgressHud()
        //Toast.makeText(activity, "Se ha registrado el control de plaga", Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.putLong("cultivoId", cultivoGlobal?.Id!!)
        val controlPlagasFragment: ControlPlagasFragment
        controlPlagasFragment = ControlPlagasFragment()
        controlPlagasFragment.arguments = bundle
        (activity as MenuMainActivity).replaceFragment(controlPlagasFragment)
    }

    override fun setTratamiento(tratamiento: Tratamiento?) {
        tratamientoId = tratamiento?.Id
        (activity as MenuMainActivity).toolbar.title = "Tratamiento-" + tratamiento?.Nombre_Comercial
        txtNombreComercial?.setText(getString(R.string.nombre_comercial, tratamiento?.Nombre_Comercial))
        txtIngredienteActivo?.setText(getString(R.string.ingrediente_activo, tratamiento?.IngredienteActivo))
        txtFormulacion?.setText(getString(R.string.formulacion, tratamiento?.Desc_Formulacion))
        txtAplicacion?.setText(getString(R.string.aplicacion, tratamiento?.Desc_Aplicacion))
        txtModoAccion?.setText(getString(R.string.modo_accion, tratamiento?.Modo_Accion))

        var firtsInsumo= SQLite.select().from(Insumo::class.java).where(Insumo_Table.Id.eq(tratamiento?.InsumoId)).querySingle()
        if(firtsInsumo!=null){
            if(firtsInsumo.blobImagen!=null){
                try {
                    val foto = firtsInsumo.blobImagen?.blob
                    //data.blobImagenEnfermedad= Blob(firtsFoto.blobImagen?.blob)
                    val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    imageViewInsumo.setImageBitmap(bitmapBlob)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }
        }
      //  txtPrecioAprox?.setText(String.format(getString(R.string.precio_aproximado, tratamiento?.precioAproximado)))

    }

    override fun validarListasAddControlPlaga(): Boolean {
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
            return true
        }
        return false
    }

    override fun setListUnidadProductiva(listUnidadProductiva: List<Unidad_Productiva>?) {
        if (viewDialogFilter != null) {
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(null)
            val unidadProductivaArrayAdapter = ArrayAdapter<Unidad_Productiva>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))

                viewDialogFilter?.spinnerCultivo?.setText("")
                viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))

                unidadProductivaGlobal = listUnidadProductiva!![position]
                presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            }
            presenter?.setListSpinnerLote(null)
            presenter?.setListSpinnerCultivo(null, null)
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
            presenter?.setListSpinnerCultivo(loteGlobal?.Id, tipoProductoId)
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

    override fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerUnidadMedidaDosis?.setAdapter(null)
            val uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_spinner_dropdown_item, listUnidadMedida)
            viewDialog?.spinnerUnidadMedidaDosis?.setAdapter(uMedidaArrayAdapter)
            viewDialog?.spinnerUnidadMedidaDosis?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaGlobal = listUnidadMedida!![position]
            }
        }

    }

    override fun validarCampos(): Boolean {
        var cancel = false
        var focusView: View? = null
        /*if (viewDialog?.txtFechaAplicacion?.text.toString().isEmpty()) {
            viewDialog?.txtFechaAplicacion?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtFechaAplicacion
            cancel = true
        } */if (viewDialog?.txtDosis?.text.toString().isEmpty()) {
            viewDialog?.txtDosis?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.txtDosis
            cancel = true
        } else if (viewDialog?.spinnerUnidadMedidaDosis?.text.toString().isEmpty()) {
            viewDialog?.spinnerUnidadMedidaDosis?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerUnidadMedidaDosis
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }

    override fun registerControlPlaga() {
        if (presenter?.validarCampos() == true) {
            val controlPlaga = ControlPlaga()
            controlPlaga.CultivoId = cultivoGlobal?.Id
            controlPlaga.Dosis = viewDialog?.txtDosis?.text.toString()
            controlPlaga.UnidadMedidaId = unidadMedidaGlobal?.Id
            controlPlaga.Fecha_aplicacion = Calendar.getInstance().time
            controlPlaga.TratamientoId = tratamientoId
            controlPlaga.EnfermedadesId = enfermedadId
            controlPlaga.NombrePlaga = nombreTipoEnfermedad
            controlPlaga.EstadoErradicacion = false
            presenter?.registerControlPlaga(controlPlaga, cultivoGlobal?.Id)
        }
    }

    override fun showAlertDialogFilterControlPlaga() {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()

        val title: String? = getString(R.string.tittle_select_cultivo_tratamiento)

        if (unidadProductivaGlobal != null && loteGlobal != null && cultivoGlobal != null) {
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.Id, tipoProductoId)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.Nombre)
            viewDialogFilter?.spinnerCultivo?.setText(cultivoGlobal?.Nombre)
        }

        //Set Events
        viewDialogFilter?.ivCloseButtonDialogFilter?.setOnClickListener(this)

        val dialog = MaterialDialog.Builder(activity!!)
                .title(title!!)
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
                            if (presenter?.validarListasAddControlPlaga() == true) {
                                dialog1.dismiss()
                                showAlertDialogAddControlPlaga(null)
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

    override fun showAlertDialogAddControlPlaga(controlPlaga: ControlPlaga?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_form_control_plaga, null)
        presenter?.setListSpinnerUnidadMedida()
        viewDialog?.ivClosetDialogControlPlaga?.setOnClickListener(this)
        //viewDialog?.txtFechaAplicacion?.setOnClickListener(this)

        viewDialog?.btnSaveControlPlaga?.setOnClickListener(this)
        viewDialog?.txtUnidadProductivaSelected?.setText(unidadProductivaGlobal?.nombre)
        viewDialog?.txtLoteSelected?.setText(loteGlobal?.Nombre)
        viewDialog?.txtCultivoSelected?.setText(cultivoGlobal?.Nombre)

        val dialog = AlertDialog.Builder(context!!, android.R.style.Theme_Light_NoTitleBar_Fullscreen)
                .setView(viewDialog)
                .create()

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)
        _dialogRegisterControlPlaga = dialog
    }

    //Escuchador de eventos
    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }
    //endregion

    //region Método on Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
            R.id.btnControlPlaga -> {
                showAlertDialogFilterControlPlaga()
            }
            R.id.btnSaveControlPlaga -> {
                registerControlPlaga()
            }
        /*R.id.txtFechaAplicacion -> {
            updateDate()
        }*/
            R.id.ivClosetDialogControlPlaga -> {
                _dialogRegisterControlPlaga?.dismiss()
            }
            R.id.ivCloseButtonDialogFilter -> {
                _dialogFilter?.dismiss()
            }

        }

    }
    //endregion

    //region Métodos
    //Fecha
    /* private fun updateDate() {
         DatePickerDialog(context, d, dateTime.get(Calendar.YEAR), dateTime.get(Calendar.MONTH), dateTime.get(Calendar.DAY_OF_MONTH)).show()
     }

     internal var d: DatePickerDialog.OnDateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
         dateTime.set(Calendar.YEAR, year)
         dateTime.set(Calendar.MONTH, monthOfYear)
         dateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth)
         mostrarResultadosFecha()
     }

     private fun mostrarResultadosFecha() {
         val format1 = SimpleDateFormat("MM/dd/yyyy")
         val formatted = format1.format(dateTime.time)
         fechaAplicacion = dateTime.time
         viewDialog?.txtFechaAplicacion?.setText(formatted)
     }*/
    //endregion

    //region ciclo de vida
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
