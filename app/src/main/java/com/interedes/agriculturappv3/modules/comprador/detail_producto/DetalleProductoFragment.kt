package com.interedes.agriculturappv3.modules.comprador.detail_producto


import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.fragment_detalle_producto.*
import kotlinx.android.synthetic.main.dialog_oferta_producto.view.*
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.google.firebase.database.*
import com.interedes.agriculturappv3.modules.comprador.productores.adapter.ProductorMoreAdapter
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.resources.EstadosOfertasResources
import com.interedes.agriculturappv3.services.resources.S3Resources
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.alert_success.view.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*
import kotlinx.android.synthetic.main.dialog_confirm.view.*
import kotlinx.android.synthetic.main.dialog_form_cultivo.*
import java.util.*


class DetalleProductoFragment : Fragment(),IMainViewDetailProducto.MainView,View.OnClickListener {


    var productoIdGlobal:Long=0
    var productoGlobal:Producto?=null
    var presenter: IMainViewDetailProducto.Presenter? = null

    //Progress
    private var hud: KProgressHUD?=null


    var viewDialog:View?= null
    var _dialogOferta: AlertDialog? = null

    var valorTotalGlobal:Double?=null
    var mUsersDBRef: DatabaseReference? = null

    var unidadMedidaPrecioGlobal: Unidad_Medida? = null


    //Permission
    var PERMISSIONS = arrayOf(
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS
    )

    val PERMISSION_REQUEST_CODE = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detalle_producto, container, false)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter =  DetailProductoPresenter(this)
        presenter?.onCreate()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUsersDBRef = FirebaseDatabase.getInstance().reference

        val b = this.arguments
        if (b != null) {
            productoIdGlobal = b.getLong("ProductoId")
        }
        (activity as MenuMainActivity).toolbar.title=getString(R.string.tittle_detail_productos)
        // swipeRefreshLayout.setOnRefreshListener(this)
        setupInjection()
        ivBackButton.setOnClickListener(this)
        btnConatctProductor.setOnClickListener(this)
        btnOfertar.setOnClickListener(this)

    }

    private fun setupInjection() {
        presenter?.getListas()
        productoGlobal= presenter?.getProducto(productoIdGlobal)
        var tipoProducto= presenter?.getTipoProducto(productoGlobal?.TipoProductoId!!)
        if(tipoProducto!=null){
            txtTipoProducto.setText(tipoProducto.Nombre)
            if(tipoProducto.Imagen!=null){
                // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                // imgTipoProducto.setImageBitmap(bitmap)
                try {
                    val foto = tipoProducto.Imagen?.blob
                    val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    logoTipoProducto.setImageBitmap(bitmapBlob)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }
        }


        if(productoGlobal!=null){
            txtNombreProductor.setText(productoGlobal?.NombreProductor)
            txtTelefono.setText(productoGlobal?.TelefonoProductor)
            txtEmail.setText(productoGlobal?.EmailProductor)
            txtNombreProducto.setText(productoGlobal?.Nombre)


            var disponibilidad = ""

            if (productoGlobal?.Stock.toString().contains(".0")) {
                disponibilidad = String.format(getString(R.string.price_empty_signe)!!,
                        productoGlobal?.Stock)
            } else {
                disponibilidad = productoGlobal?.Stock.toString()
            }

            ratingBar?.rating = 3.5f

            txtNombreProductor?.setText(productoGlobal?.NombreProductor)
            txtDisponibilidad?.setText(String.format("%s: %s %s", productoGlobal?.NombreCalidad, disponibilidad, productoGlobal?.NombreUnidadMedidaCantidad))
            txtFechaDisponibilidad?.setText(productoGlobal?.getFechaLimiteDisponibilidadFormat())

            txtPrecio?.setText(String.format(getString(R.string.price_producto),
                    productoGlobal?.Precio, productoGlobal?.PrecioUnidadMedida))

            txtUbicacion?.setText(String.format("%s / %s", productoGlobal?.Ciudad, productoGlobal?.Departamento))
            txtFechaDisponibilidad?.setText(productoGlobal?.getFechaLimiteDisponibilidadFormat())


            if(productoGlobal?.blobImagen!=null){
                // val bitmap = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                // imgTipoProducto.setImageBitmap(bitmap)
                try {
                    val foto = productoGlobal?.blobImagen?.blob
                    val bitmapBlob = BitmapFactory.decodeByteArray(foto, 0, foto!!.size)
                    contenIconProducto.setImageBitmap(bitmapBlob)
                }catch (ex:Exception){
                    var ss= ex.toString()
                    Log.d("Convert Image", "defaultValue = " + ss);
                }
            }else{
               /* Picasso.get()
                        .load(S3Resources.RootImage+"${productoGlobal?.Imagen}")
                        .placeholder(R.drawable.ic_account_box_green)
                        .error(R.drawable.ic_account_box_green)
                        .into(contenIconProducto, object : com.squareup.picasso.Callback {
                            override fun onError(e: java.lang.Exception?) {
                                contenIconProducto?.setImageResource(R.drawable.ic_foto_producto)
                                contenIconProducto?.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                // Toast.makeText(context,"Error foto",Toast.LENGTH_LONG).show()
                            }
                            override fun onSuccess() {
                                // Toast.makeText(context,"Loaded foto",Toast.LENGTH_LONG).show()
                            }
                        })*/

                Picasso.get()
                        .load(S3Resources.RootImage+"${productoGlobal?.Imagen}")
                        .fit()
                        .placeholder(R.drawable.ic_foto_producto)
                        .error(R.drawable.ic_foto_producto)
                        .into(contenIconProducto, object : com.squareup.picasso.Callback {
                            override fun onError(e: java.lang.Exception?) {
                                contenIconProducto?.setImageResource(R.drawable.ic_foto_producto)
                                contenIconProducto?.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                // Toast.makeText(context,"Error foto",Toast.LENGTH_LONG).show()
                            }
                            override fun onSuccess() {
                                // Toast.makeText(context,"Loaded foto",Toast.LENGTH_LONG).show()
                            }
                        })
               // Picasso.setSingletonInstance(picassoss) //apply to default singleton instance
                /*Picasso.get()
                        .load(S3Resources.RootImage+"${productoGlobal?.Imagen}")
                        .placeholder(R.drawable.ic_foto_producto)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.ic_foto_producto)
                        .into(contenIconProducto)*/

            }

            val query = mUsersDBRef?.child("Users")?.orderByChild("correo")?.equalTo(productoGlobal?.EmailProductor)
            query?.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        for (issue in dataSnapshot.children) {
                            // do something with the individual "issues"
                            var user = issue.getValue<UserFirebase>(UserFirebase::class.java)
                            //if not current user, as we do not want to show ourselves then chat with ourselves lol
                            try {

                                Picasso.get()
                                        .load(user?.Imagen)
                                        .fit()
                                        .centerCrop()
                                        .placeholder(R.drawable.ic_account_box_green)
                                        .error(R.drawable.ic_account_box_green)
                                        .into(contentIcon);

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }
    }



    //region IMPLEMENTS METHODS

    override fun showProgress() {
        //  swipeRefreshLayout.setRefreshing(true);
    }

    override fun hideProgress() {
        // swipeRefreshLayout.setRefreshing(false);
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

    override fun requestResponseOK() {
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.red_900, error)
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


    override fun onMessageToas(message:String,color:Int){
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(message)
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(activity!!, color))
        var mytoast =  Toast(activity);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_LONG);
        mytoast.show();
        ///onMessageError(R.color.red_900, getString(R.string.disabledGPS))
    }

    override fun validarAddOferta(): Boolean {
        var cancel = false
        var focusView: View? = null
        if (viewDialog?.edtCantidadOfertar?.text.toString().isEmpty() ) {
            viewDialog?.edtCantidadOfertar?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.edtCantidadOfertar
            cancel = true
        } else if (viewDialog?.edtPriceOferta?.text.toString().isEmpty()) {
            viewDialog?.edtPriceOferta?.setError(getString(R.string.error_field_required))
            focusView =viewDialog?.edtPriceOferta
            cancel = true
        }
        else if (viewDialog?.spinnerMonedaPrecio?.text.toString().isEmpty()) {
            viewDialog?.spinnerMonedaPrecio?.setError(getString(R.string.error_field_required))
            focusView = viewDialog?.spinnerMonedaPrecio
            cancel = true
        }
        else if (!presenter?.verificateCantProducto(productoIdGlobal,viewDialog?.edtCantidadOfertar?.text.toString().toDoubleOrNull())!!) {
            viewDialog?.edtCantidadOfertar?.setError(getString(R.string.verifcate_cantidad_oferta))
            focusView = viewDialog?.edtCantidadOfertar
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }
    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_produccion_cultivo);
        return builder.show();
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    override fun setListMoneda(listMoneda: List<Unidad_Medida>?) {
        if (viewDialog != null) {
            viewDialog?.spinnerMonedaPrecio?.setAdapter(null)
            val monedaArrayAdapter = ArrayAdapter<Unidad_Medida>(activity, android.R.layout.simple_list_item_activated_1, listMoneda)
            viewDialog?.spinnerMonedaPrecio?.setAdapter(monedaArrayAdapter)
            viewDialog?.spinnerMonedaPrecio?.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
                unidadMedidaPrecioGlobal = listMoneda!![position]
            }
        }
    }

    override fun showAlertDialogOfertar(producto: Producto?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_oferta_producto, null)

        viewDialog?.ivClosetDialogOferta?.setOnClickListener(this)
        viewDialog?.btnSendOferta?.setOnClickListener(this)


        viewDialog?.txtUnidadMedida?.setText(producto?.NombreUnidadMedidaCantidad)

        presenter?.setListSpinnerMoneda()

        viewDialog?.edtCantidadOfertar?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                var cantidad=  viewDialog?.edtCantidadOfertar?.text.toString()?.toDoubleOrNull()
                var precioOferta=  viewDialog?.edtPriceOferta?.text.toString()?.toDoubleOrNull()

                if(!viewDialog?.edtCantidadOfertar?.text.toString().isEmpty()
                        && !viewDialog?.edtPriceOferta?.text.toString().isEmpty()){

                    if(presenter?.verificateCantProducto(producto?.ProductoId,cantidad)!!){
                        var subtotal=cantidad!!*precioOferta!!
                        var costo_total_item = String.format(context!!.getString(R.string.price),
                                subtotal)
                        var costo_total_ = String.format(context!!.getString(R.string.price),
                                subtotal)
                        valorTotalGlobal=subtotal
                        viewDialog?.txtValorSubtotal?.text=costo_total_item
                        viewDialog?.txtValortotal?.setText(costo_total_.replace(",", "."))

                    }else{
                        var focusView:View? =null
                        viewDialog?.edtCantidadOfertar?.setError(getString(R.string.verifcate_cantidad_oferta))
                        focusView=viewDialog?.edtCantidadOfertar
                        focusView?.requestFocus()
                    }
                }else{
                    viewDialog?.txtValorSubtotal?.text=""
                    viewDialog?.txtValortotal?.setText("")
                }
            }
        })


        viewDialog?.edtPriceOferta?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                var cantidad=  viewDialog?.edtCantidadOfertar?.text.toString()?.toDoubleOrNull()
                var precioOferta=  viewDialog?.edtPriceOferta?.text.toString()?.toDoubleOrNull()

                if(!viewDialog?.edtCantidadOfertar?.text.toString().isEmpty()
                        && !viewDialog?.edtPriceOferta?.text.toString().isEmpty()){

                    if(presenter?.verificateCantProducto(producto?.ProductoId,cantidad)!!){

                        var subtotal=cantidad!!*precioOferta!!
                        var costo_total_item = String.format(context!!.getString(R.string.price),
                                subtotal)
                        var costo_total_ = String.format(context!!.getString(R.string.price),
                                subtotal)
                        valorTotalGlobal=subtotal
                        viewDialog?.txtValorSubtotal?.text=costo_total_item
                        viewDialog?.txtValortotal?.setText(costo_total_.replace(",", "."))
                    }else{
                        var focusView:View? =null
                        viewDialog?.edtCantidadOfertar?.setError(getString(R.string.verifcate_cantidad_oferta))
                        focusView=viewDialog?.edtCantidadOfertar
                        focusView?.requestFocus()
                    }

                }else{
                    viewDialog?.txtValorSubtotal?.text=""
                    viewDialog?.txtValortotal?.setText("")
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
        _dialogOferta=dialog
    }


    override fun showConfirmOferta() {

        val inflater = this.layoutInflater
        var viewDialogConfirm = inflater.inflate(R.layout.dialog_confirm, null)



        viewDialogConfirm?.txtTitleConfirm?.setText("")

        var costo_total_ = String.format(context!!.getString(R.string.price),
                valorTotalGlobal)
        viewDialogConfirm?.txtTitleConfirm?.setText(costo_total_+" " + viewDialog?.spinnerMonedaPrecio?.text.toString())


        var content =String.format(getString(R.string.content_oferta_confirm),viewDialog?.edtCantidadOfertar?.text.toString(),viewDialog?.txtUnidadMedida?.text.toString(),productoGlobal?.Nombre)
        viewDialogConfirm?.txtDescripcionConfirm?.setText(content)

        MaterialDialog.Builder(activity!!)
                .title(String.format(getString(R.string.content_oferta_tittle),productoGlobal?.NombreProductor))
                .customView(viewDialogConfirm!!, true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.light_green_800)
                .negativeColorRes(R.color.light_green_800)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.light_green_800)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onPositive(
                        { dialog1, which ->
                            //Toast.makeText(activity,"Enviar oferta",Toast.LENGTH_SHORT).show()
                           // _dialogOferta?.dismiss()
                            postOferta()
                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                })
                .show()
    }


    override fun showConfirmSendSmsOferta(oferta:Oferta){
        val inflater = this.layoutInflater
        var viewDialogConfirm = inflater.inflate(R.layout.dialog_confirm, null)

        viewDialogConfirm?.txtTitleConfirm?.setText("")
        viewDialogConfirm?.txtTitleConfirm?.setText(productoGlobal?.NombreProductor)


        var content =String.format(getString(R.string.content_sms_message),productoGlobal?.NombreProductor)
        viewDialogConfirm?.txtDescripcionConfirm?.setText(content)

        MaterialDialog.Builder(activity!!)
                .title(getString(R.string.content_sms_tittle))
                .customView(viewDialogConfirm!!, true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.light_green_800)
                .negativeColorRes(R.color.light_green_800)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.light_green_800)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onPositive(
                        { dialog1, which ->
                            dialog1.dismiss()

                            val usuarioTo= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
                            if(usuarioTo!=null){
                                presenter?.sendSmsOferta(usuarioTo,oferta,activity!!)
                            }

                            //Toast.makeText(activity,"Enviar oferta",Toast.LENGTH_SHORT).show()
                            // _dialogOferta?.dismiss()

                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                    sucessResponseOferta()
                    onMessageToas("Mensage no enviado",R.color.red_900)
                })
                .show()
    }


    override fun sucessResponseOferta() {

        val inflater = this.layoutInflater
        var viewDialogSuccessOferta = inflater.inflate(R.layout.alert_success, null)

        viewDialogSuccessOferta?.tittle_sucess?.setText(getString(R.string.oferta_sucess))
        viewDialogSuccessOferta?.content_sucess?.setText(getString(R.string.content_oferta_success))

        val dialog = MaterialDialog.Builder(activity!!)
                .customView(viewDialogSuccessOferta!!, true)
                .positiveText(R.string.confirm)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.light_green_800)
                .limitIconToDefaultSize()
                .backgroundColorRes(R.color.white_solid)
                // .negativeColorRes(R.color.material_red_400)
                .iconRes(R.drawable.ic_registro)
                .dividerColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.white)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .autoDismiss(false)
                //.negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .onPositive(
                        { dialog1, which ->
                           dialog1.dismiss()
                            if(_dialogOferta!=null){
                                _dialogOferta?.dismiss()
                            }
                        })
                .build()
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)
    }



    override fun postOferta(){

        if(presenter?.validarCamposAddOferta()!!){

            var oferta= Oferta()
            var date= Calendar.getInstance().time

            oferta.UpdatedOn=oferta.getDateFormatApi(date)
            oferta.CreatedOn=oferta.getDateFormatApi(date)

            oferta.CreatedOnLocal=date
            oferta.UpdatedOnLocal=date

            oferta.UsuarioTo=productoGlobal?.userId
            oferta.NombreUnidadMedidaPrecio=viewDialog?.spinnerMonedaPrecio?.text.toString()
            oferta.CalidadId=productoGlobal?.CalidadId
            oferta.EstadoOfertaId=EstadosOfertasResources.VIGENTE
            oferta.ProductoId=productoGlobal?.Id_Remote
            oferta.UnidadMedidaId=unidadMedidaPrecioGlobal?.Id
            oferta.Valor_Oferta=valorTotalGlobal

            oferta.Cantidad=viewDialog?.edtCantidadOfertar?.text.toString()?.toDoubleOrNull()

            presenter?.postOferta(oferta)

        }
    }





    //endregion




    //region EVENTS
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackButton -> {
                ivBackButton.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }

            R.id.btnOfertar -> {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!hasPermissions(activity, *PERMISSIONS)) {
                        requestPermission()
                    } else {
                        showAlertDialogOfertar(productoGlobal)
                    }
                } else {
                    showAlertDialogOfertar(productoGlobal)
                }
            }
            R.id.btnConatctProductor->{

            }

            R.id.ivClosetDialogOferta->{
                _dialogOferta?.dismiss()
            }

            R.id.btnSendOferta->{
                if(presenter?.validarCamposAddOferta()!!){
                    showConfirmOferta()
                }
            }
        }
    }
    //endregio

    //region PERMISSION
    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_REQUEST_CODE);
        //ContextCompat.requestPermissions(activity!!, PERMISSIONS, PERMISSION_ALL)
        // requestPermissions(PERMISSIONS, PERMISSION_ALL)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE ->
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // Toast.makeText(activity, "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show()
                    showAlertDialogOfertar(productoGlobal)
                    ///presenter.sendSmsOferta()
                    //sendSMS()
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {


                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS) || shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) || shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS)) {
                            showAlertDialogOfertar(productoGlobal)
                            ///Toast.makeText(activity, "Permiso denegado", Toast.LENGTH_LONG).show()
                        }
                        else{
                            if (hasPermissions(activity, *PERMISSIONS)) {
                                //sendSMS()
                            } else {
                                val builder = AlertDialog.Builder(activity!!)
                                builder.setMessage("Permission Granted, Now you can access sms")
                                        .setPositiveButton("Aceptar") { dialog, id ->
                                            val intent = Intent()
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            val uri = Uri.fromParts("package", "com.interedes.agriculturappv3.modules.comprador.detail_producto", null)
                                            intent.setData(uri)
                                            startActivity(intent)
                                        }
                                builder.setTitle("Permiso")
                                builder.setIcon(R.mipmap.ic_launcher)
                                // Create the AlertDialog object and return it
                                builder.show()

                            }
                        }
                    }
                }
        //else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    //endregion



    //region OVERRIDES METHODS
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onPause() {
        super.onPause()

        presenter?.onPause( activity!!.applicationContext)
    }

    override fun onResume() {


        presenter?.onResume(activity!!.applicationContext)
        super.onResume()
    }








    //end region

}
