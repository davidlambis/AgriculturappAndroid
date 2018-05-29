package com.interedes.agriculturappv3.modules.comprador.detail_producto


import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast

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
import com.squareup.picasso.Picasso


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

    }

    private fun setupInjection() {

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
                                try {
                                    Picasso.with(context).load(user?.Imagen).placeholder(R.drawable.default_avata).into(contentIcon)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
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
        onMessageError(R.color.grey_luiyi, error)
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

    override fun validarListasAddOferta(): Boolean {
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
        else if (presenter?.verificateCantProducto(productoIdGlobal,viewDialog?.edtCantidadOfertar?.text.toString().toDoubleOrNull())!!) {
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
    override fun showAlertDialogOfertar(producto: Producto?) {
        val inflater = this.layoutInflater
        viewDialog = inflater.inflate(R.layout.dialog_oferta_producto, null)

        viewDialog?.ivClosetDialogOferta?.setOnClickListener(this)
        viewDialog?.btnSendOferta?.setOnClickListener(this)


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
                        viewDialog?.edtCantidadOfertar?.setError(getString(R.string.verifcate_cantidad_oferta))
                    }
                }else{
                    viewDialog?.txtValorSubtotal?.text=""
                    viewDialog?.edtCantidadOfertar?.setText("")
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

                        var subtotal=cantidad!!*precioOferta!!
                        var costo_total_item = String.format(context!!.getString(R.string.price),
                                subtotal)
                        var costo_total_ = String.format(context!!.getString(R.string.price),
                                subtotal)
                        valorTotalGlobal=subtotal
                        viewDialog?.txtValorSubtotal?.text=costo_total_item
                        viewDialog?.txtValortotal?.setText(costo_total_.replace(",", "."))

                }else{
                    viewDialog?.txtValorSubtotal?.text=""
                    viewDialog?.edtPriceOferta?.setText("")
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


    fun showConfirmOferta() {
        MaterialDialog.Builder(activity!!)
                .title(String.format(getString(R.string.content_oferta_tittle),productoGlobal?.NombreProductor))
                .content(R.string.content_oferta_confirm, true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.material_red_400)
                .negativeColorRes(R.color.material_red_400)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.material_red_400)
                .contentColorRes(android.R.color.white)
                .backgroundColorRes(R.color.material_blue_grey_800)
                .dividerColorRes(R.color.accent)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .theme(Theme.DARK)
                .onPositive(
                        { dialog1, which ->
                            Toast.makeText(activity,"Enviar oferta",Toast.LENGTH_SHORT).show()
                        })
                .onNegative({ dialog1, which ->
                    dialog1.dismiss()
                })
                .show()
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
                showAlertDialogOfertar(productoGlobal)
            }

            R.id.btnConatctProductor->{

            }


            R.id.btnSendOferta->{
                if(presenter?.validarCamposAddOferta()!!){
                    showConfirmOferta()
                }
            }
        }
    }
    //endregio

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
