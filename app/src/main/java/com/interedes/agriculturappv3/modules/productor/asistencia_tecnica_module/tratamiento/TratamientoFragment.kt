package com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.tratamiento


import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
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
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.modules.models.tratamiento.calificacion.Calificacion_Tratamiento
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.productor.asistencia_tecnica_module.control_plagas.ControlPlagasFragment
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import com.robertlevonyan.views.expandable.ExpandingListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.dialog_calificacion_tratamiento.view.*
import kotlinx.android.synthetic.main.dialog_form_control_plaga.view.*
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
import kotlinx.android.synthetic.main.fragment_tratamiento.*
import java.io.File
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

    var _dialogSendCalificacion: MaterialDialog? = null

    //Models
    var calificacionTratamiento:Calificacion_Tratamiento?=null
    var tratamientoGlobal:Tratamiento?=null


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
        btnCalificarTratamiento.setOnClickListener(this)


        expandableDescripcionInsumo.setExpandingListener(object : ExpandingListener {
            override fun onExpanded() {
                txtDescripcionInsumo.visibility = View.VISIBLE
            }
            override fun onCollapsed() {
                txtDescripcionInsumo.visibility = View.GONE
            }
        })

        expandableTratamiento.setExpandingListener(object : ExpandingListener {
            override fun onExpanded() {
                contentTratamiento.visibility = View.VISIBLE
            }
            override fun onCollapsed() {
                contentTratamiento.visibility = View.GONE
            }
        })


    }

    private fun setupInjection() {
        presenter?.getTratamiento(tratamientoId)
    }

    //region Métodos Interfaz
    override fun showAlertSendCalificacion() {


        val inflater = this.layoutInflater
        var viewDialogCalificate = inflater.inflate(R.layout.dialog_calificacion_tratamiento, null)

        viewDialogCalificate.ratingBarCalificate.setOnRatingBarChangeListener(RatingBar.OnRatingBarChangeListener(
                { ratingBarThis, float, boolean ->
                    viewDialogCalificate.ratingBarCalificate.rating=float
                    val calificacionThis = float.toDouble()
                    viewDialogCalificate.txtRatingTratamientoCalificate.setText(String.format(getString(R.string.calificacion_tratamiento),calificacionThis))
                    Toast.makeText(activity,viewDialogCalificate.ratingBarCalificate.rating.toString(), Toast.LENGTH_SHORT).show()
                }
        ))



        var dialog =MaterialDialog.Builder(context!!)
                .iconRes(R.drawable.ic_calification)
                .customView(viewDialogCalificate!!, true)
                .dividerColorRes(R.color.colorPrimary)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.title_calificacion)
                .titleGravity(GravityEnum.CENTER)
                .positiveText(R.string.send)
                .negativeText(R.string.cancel)
                .autoDismiss(false)
                .onPositive(
                        { dialog1, which ->

                            var calificacionRating=viewDialogCalificate.ratingBarCalificate.rating
                            val calificacion = calificacionRating.toDouble()
                            if(calificacion>0){
                                presenter?.sendCalificationTratamietno(tratamientoGlobal,calificacion = calificacion)

                            }else{
                                Toast.makeText(activity,getString(R.string.required_calificacion_tratamiento), Toast.LENGTH_SHORT).show()
                            }
                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                })
                .show()
        _dialogSendCalificacion=dialog
    }



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

    override fun requestResponseOk() {
        if (_dialogSendCalificacion != null) {
            _dialogSendCalificacion?.dismiss()
        }
        onMessageOk(R.color.colorPrimary, getString(R.string.request_ok));
        onMessageOk(R.color.colorPrimary, getString(R.string.request_calificate_ok));
    }

    override fun requestResponseExistCalicated() {
        if (_dialogSendCalificacion != null) {
            _dialogSendCalificacion?.dismiss()
        }
        onMessageOk(R.color.colorPrimary, getString(R.string.request_calificate_exist_ok));
    }


    override fun requestResponseError(error: String?) {
        if (_dialogSendCalificacion != null) {
            _dialogSendCalificacion?.dismiss()
        }
        if (_dialogRegisterControlPlaga != null) {
            _dialogRegisterControlPlaga?.dismiss()
        }

        onMessageError(R.color.grey_luiyi, error)
    }


    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make((activity as MenuMainActivity).container, message!!, Snackbar.LENGTH_LONG)
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

    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
            if (_dialogSendCalificacion != null) {
                _dialogSendCalificacion?.dismiss()
            }
        })
        builder.setIcon(R.drawable.ic_lote);
        return builder.show();
    }

    override fun loadControlPlagas(listControlPlaga: List<ControlPlaga>) {
        hideProgressHud()
        //Toast.makeText(activity, "Se ha registrado el control de plaga", Toast.LENGTH_SHORT).show()
        val bundle = Bundle()
        bundle.putLong("cultivoId", cultivoGlobal?.CultivoId!!)
        val controlPlagasFragment: ControlPlagasFragment
        controlPlagasFragment = ControlPlagasFragment()
        controlPlagasFragment.arguments = bundle
        (activity as MenuMainActivity).replaceFragment(controlPlagasFragment)

    }

    override fun setTratamiento(tratamiento: Tratamiento?) {
        tratamientoGlobal=tratamiento
        tratamientoId = tratamiento?.Id
        txtTitle.setText("Tratamiento- ${tratamiento?.Nombre_Insumo}")

        //(activity as MenuMainActivity).toolbar.title = "Tratamiento-" + tratamiento?.Nombre_Comercial
        txtNombreComercial?.setText(tratamiento?.Nombre_Comercial)
        txtIngredienteActivo?.setText(tratamiento?.IngredienteActivo)
        txtFormulacion?.setText( tratamiento?.Desc_Formulacion)
        txtAplicacion?.setText(tratamiento?.Desc_Aplicacion)
        txtModoAccion?.setText(tratamiento?.Modo_Accion)
        txtDescripcionInsumo.setText(tratamiento?.Descripcion_Insumo)

        txtPrecioAprox.setText(getString(R.string.price,tratamiento?.precioAproximado))
      //  txtPrecioAprox?.setText(String.format(getString(R.string.precio_aproximado, tratamiento?.precioAproximado)))
    }

    override fun setInsumo(insumo: Insumo?) {
        if(insumo!=null){
            if(insumo.ImagenLocal!=null){
                val imgFile =   File(insumo.ImagenLocal);
                if(imgFile.exists()){
                    //var myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    // imgPlaga.setImageBitmap(myBitmap);
                    Picasso.get()
                            .load(imgFile)
                            .fit()
                            .centerInside()
                            .placeholder(R.drawable.empty_insumo)
                            .error(R.drawable.empty_insumo)
                            .into(imageViewInsumo);
                }else{
                    val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.empty_insumo)
                    imageViewInsumo.setImageBitmap(largeIcon)
                }
                // uiHandler.post( Runnable() {
                /// });
            }else{
                val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.empty_insumo)
                imageViewInsumo.setImageBitmap(largeIcon)
                //imgPlaga.setImageBitmap(largeIcon)
            }
            /*
            if(insumo.blobImagen!=null){
                try {
                    val foto = insumo.blobImagen?.blob
                    //data.blobImagenEnfermedad= Blob(firtsFoto.blobImagen?.blob)
                    val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    imageViewInsumo.setImageBitmap(bitmapBlob)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }*/
        }
    }



    override fun setDisabledCalificacion(calificacionTra: Calificacion_Tratamiento?) {

        var calificacionTratamiento:Float?= calificacionTra?.Valor_Promedio?.toFloat()
        ratingBar.rating=calificacionTratamiento!!

        txtRatingTratamiento.setText(String.format(getString(R.string.calificacion_tratamiento),calificacionTra?.Valor_Promedio))
        txtStatusCalificacion.setText(String.format(getString(R.string.tittle_is_calificate_tratamiento),calificacionTra?.Valor))

        btnCalificarTratamiento.visibility=View.GONE

      //  ratingBar.setIsIndicator(true)
      //  ratingBar.isClickable = false

    }

    override fun setEnabledCalificacion(calificacionTra: Calificacion_Tratamiento?) {

        var calificacionTratamiento:Float?= calificacionTra?.Valor_Promedio?.toFloat()

        txtRatingTratamiento.setText(String.format(getString(R.string.calificacion_tratamiento),calificacionTra?.Valor_Promedio))
        txtStatusCalificacion.setText(getString(R.string.tittle_not_calificate_tratamiento))

        ratingBar.rating=calificacionTratamiento!!
        btnCalificarTratamiento.visibility=View.VISIBLE

        //ratingBar.setIsIndicator(false)
        //ratingBar.isClickable = true

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
            val unidadProductivaArrayAdapter = ArrayAdapter<Unidad_Productiva>(activity, android.R.layout.simple_list_item_activated_1, listUnidadProductiva)
            viewDialogFilter?.spinnerUnidadProductiva!!.setAdapter(unidadProductivaArrayAdapter)
            viewDialogFilter?.spinnerUnidadProductiva!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                viewDialogFilter?.spinnerLote?.setText("")
                viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))

                viewDialogFilter?.spinnerCultivo?.setText("")
                viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))

                unidadProductivaGlobal = listUnidadProductiva!![position]
                presenter?.setListSpinnerLote(unidadProductivaGlobal?.Unidad_Productiva_Id)

                presenter?.setListSpinnerCultivo(0)
            }
            presenter?.setListSpinnerLote(null)
            presenter?.setListSpinnerCultivo(null)
        }
    }

    override fun setListLotes(listLotes: List<Lote>?) {
        viewDialogFilter?.spinnerLote!!.setAdapter(null)
        //viewDialogFilter?.spinnerLote?.setText("")
        //viewDialogFilter?.spinnerLote?.setHint(String.format(getString(R.string.spinner_lote)))
        val loteArrayAdapter = ArrayAdapter<Lote>(activity, android.R.layout.simple_list_item_activated_1, listLotes)
        viewDialogFilter?.spinnerLote!!.setAdapter(loteArrayAdapter)
        viewDialogFilter?.spinnerLote!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            viewDialogFilter?.spinnerCultivo?.setText("")
            viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
            loteGlobal = listLotes!![position]
            presenter?.setListSpinnerCultivo(loteGlobal?.LoteId)
        }
    }

    override fun setListCultivos(listCultivos: List<Cultivo>?) {
        viewDialogFilter?.spinnerCultivo!!.setAdapter(null)
        //viewDialogFilter?.spinnerCultivo?.setText("")
        //viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
        val cultivoArrayAdapter = ArrayAdapter<Cultivo>(activity, android.R.layout.simple_list_item_activated_1, listCultivos)
        viewDialogFilter?.spinnerCultivo!!.setAdapter(cultivoArrayAdapter)
        viewDialogFilter?.spinnerCultivo!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            cultivoGlobal = listCultivos!![position]
            Cultivo_Id = cultivoGlobal?.CultivoId
        }
    }

    override fun setListUnidadMedida(listUnidadMedida: List<Unidad_Medida>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerUnidadMedidaDosis?.setAdapter(null)
            val uMedidaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_list_item_activated_1, listUnidadMedida)
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
            controlPlaga.CultivoId = cultivoGlobal?.CultivoId
            controlPlaga.Dosis = viewDialog?.txtDosis?.text.toString().toDoubleOrNull()
            controlPlaga.UnidadMedidaId = unidadMedidaGlobal?.Id
            controlPlaga.Fecha_aplicacion_local = Calendar.getInstance().time
            controlPlaga.TratamientoId = tratamientoId
            controlPlaga.EnfermedadesId = enfermedadId
            controlPlaga.NombrePlaga = nombreTipoEnfermedad
            controlPlaga.EstadoErradicacion = false
            presenter?.registerControlPlaga(controlPlaga, cultivoGlobal?.CultivoId)
        }
    }

    override fun showAlertDialogFilterControlPlaga() {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()

        val title: String? = getString(R.string.tittle_select_cultivo_tratamiento)

        if (unidadProductivaGlobal != null && loteGlobal != null && cultivoGlobal != null) {
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Unidad_Productiva_Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.LoteId)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.toString())
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
            R.id.btnCalificarTratamiento -> {
                showAlertSendCalificacion()
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
