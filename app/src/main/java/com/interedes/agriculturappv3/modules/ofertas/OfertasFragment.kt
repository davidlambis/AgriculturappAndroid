package com.interedes.agriculturappv3.modules.ofertas


import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.Theme
import com.bumptech.glide.load.engine.DiskCacheStrategy

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.detail_sms_user.Chat_Sms_Activity
import com.interedes.agriculturappv3.activities.chat.online.messages_chat.ChatMessageActivity
import com.interedes.agriculturappv3.libs.GlideApp
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.Room
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.models.cultivo.Cultivo
import com.interedes.agriculturappv3.modules.models.lote.Lote
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.producto.Producto
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.ofertas.adapters.OfertasAdapter
import com.interedes.agriculturappv3.modules.productor.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.resources.*
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.sql.language.SQLite
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.dialog_confirm.view.*
import kotlinx.android.synthetic.main.dialog_detail_foto.view.*
import kotlinx.android.synthetic.main.dialog_select_spinners.view.*
import kotlinx.android.synthetic.main.fragment_ofertas.*
import java.util.ArrayList


class OfertasFragment : Fragment(), IOfertas.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    //Progress
    private var hud: KProgressHUD?=null

    var presenter: IOfertas.Presenter? = null
    var ofertasList: ArrayList<Oferta>? = ArrayList<Oferta>()
    var adapter: OfertasAdapter? = null
    var viewDialogFilter: View? = null
    var cultivoGlobal: Cultivo? = null
    var unidadProductivaGlobal: Unidad_Productiva? = null
    var loteGlobal: Lote? = null
    var productoGlobal: Producto? = null
    var _dialogFilter: MaterialDialog? = null

    var Producto_Id: Long? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ofertas, container, false)
    }

    companion object {
        var instance: OfertasFragment? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        instance = this
        presenter = OfertasPresenter(this)
        presenter?.onCreate()

       /* OfertasFragment.instance = this
        presenter = OfertasPresenter(this)
        presenter?.onCreate()*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_ofertas)

        swipeRefreshLayout.setOnRefreshListener(this)
        searchFilter.setOnClickListener(this)
        setupInitDesign()
        setupInjection()

        ivBackButton.setOnClickListener(this)
        ivBackButton2.setOnClickListener(this)
    }

    private fun setupInitDesign() {
       // (activity as MenuMainActivity).toolbar.title = getString(R.string.title_module_comercial)
        (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.blue));
        val iconMenu = (activity as MenuMainActivity).menuItemGlobal
        iconMenu?.isVisible = true

        val iconc = iconMenu?.setIcon(ContextCompat.getDrawable((activity as MenuMainActivity), R.drawable.ic_icon_comercial))
        val icon = iconc?.icon?.mutate()
        icon?.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_IN)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = (activity as MenuMainActivity).getWindow()
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.statusBarColor = ContextCompat.getColor((activity as MenuMainActivity), R.color.blue)
            (activity as MenuMainActivity).app_bar_main.elevation = 0f
            (activity as MenuMainActivity).app_bar_main.stateListAnimator = null

        } else {
            (activity as MenuMainActivity).app_bar_main.targetElevation = 0f
        }

    }

    private fun initAdapter() {
        recyclerView?.layoutManager = LinearLayoutManager(activity)
        adapter = OfertasAdapter(ofertasList!!, presenter?.getUserLogued()?.RolNombre)
        recyclerView?.adapter = adapter
    }

    private fun setupInjection() {
        showProgress()
        presenter?.getListOfertas(Producto_Id)
        if(presenter?.getUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)){
            contentFilterProductor.visibility=View.GONE
            contentFilterComprador.visibility=View.VISIBLE
        }else{
            contentFilterProductor.visibility=View.VISIBLE
            contentFilterComprador.visibility=View.GONE
        }
    }

    //region on Click
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.searchFilter -> {
                showAlertDialogFilterOferta()
            }
            R.id.ivBackButton -> {
                ivBackButton?.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
            R.id.ivBackButton2 -> {
                ivBackButton2?.setColorFilter(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))
                (activity as MenuMainActivity).onBackPressed()
            }
            R.id.ivCloseButtonDialogFilter -> {
                _dialogFilter?.dismiss()
            }
        }
    }
    //endregion

    //region Métodos Interfaz
    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }

    override fun setListOfertas(listOfertas: List<Oferta>) {
        adapter?.clear()
        ofertasList?.clear()
        adapter?.setItems(listOfertas)
        setResults(listOfertas.size)
    }

    override fun setResults(ofertas: Int) {
        val results = String.format(getString(R.string.results_global_search),
                ofertas)
        txtResults.setText(results)
    }

    override fun validarListas(): Boolean? {
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
        } else if (viewDialogFilter?.spinnerProducto?.text.toString().isEmpty()) {
            viewDialogFilter?.spinnerProducto?.setError(getString(R.string.error_field_required))
            focusView = viewDialogFilter?.spinnerProducto
            cancel = true
        }
        if (cancel) {
            focusView?.requestFocus()
        } else {
            return true;
        }
        return false
    }

    override fun showAlertDialogFilterOferta() {
        val inflater = this.layoutInflater
        viewDialogFilter = inflater.inflate(R.layout.dialog_select_spinners, null)
        presenter?.setListSpinnerUnidadProductiva()

        val title: String? = getString(R.string.select_product_ofertas)
        if (unidadProductivaGlobal != null && loteGlobal != null && cultivoGlobal != null && productoGlobal != null) {
            presenter?.setListSpinnerLote(unidadProductivaGlobal?.Unidad_Productiva_Id)
            presenter?.setListSpinnerCultivo(loteGlobal?.LoteId)
            presenter?.setListSpinnerProducto(cultivoGlobal?.CultivoId)

            viewDialogFilter?.spinnerUnidadProductiva?.setText(unidadProductivaGlobal?.nombre)
            viewDialogFilter?.spinnerLote?.setText(loteGlobal?.Nombre)
            viewDialogFilter?.spinnerCultivo?.setText(cultivoGlobal?.Nombre)
            viewDialogFilter?.spinnerProducto?.visibility = View.VISIBLE
            viewDialogFilter?.spinnerProducto?.setText(productoGlobal?.NombreDetalleTipoProducto)
        }

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
                            if (presenter?.validarListas() == true) {
                                dialog1.dismiss()
                                presenter?.getListOfertas(Producto_Id)
                                presenter?.getProducto(Producto_Id)
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
                unidadProductivaGlobal = listUnidadProductiva!![position] as Unidad_Productiva
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
            viewDialogFilter?.spinnerProducto?.visibility = View.VISIBLE
            cultivoGlobal = listCultivos!![position]
            viewDialogFilter?.spinnerProducto?.setText("")
            presenter?.setListSpinnerProducto(cultivoGlobal?.CultivoId)
        }
    }

    override fun setListProductos(listProductos: List<Producto>?) {
        viewDialogFilter?.spinnerProducto!!.setAdapter(null)
        //viewDialogFilter?.spinnerCultivo?.setText("")
        //viewDialogFilter?.spinnerCultivo?.setHint(String.format(getString(R.string.spinner_cultivo)))
        val productoArrayAdapter = ArrayAdapter<Producto>(activity, android.R.layout.simple_list_item_activated_1, listProductos)
        viewDialogFilter?.spinnerProducto!!.setAdapter(productoArrayAdapter)
        viewDialogFilter?.spinnerProducto!!.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->

            productoGlobal = listProductos!![position]
            Producto_Id = productoGlobal?.Id_Remote
        }
    }

    override fun verificateConnection(): AlertDialog? {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getString(R.string.alert))
        builder.setMessage(getString(R.string.verificate_conexion))
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_ofertas)
        return builder.show()
        /*
        return  MaterialDialog.Builder(activity!!)
                .title(R.string.alert)
                .content(R.string.verificate_conexion, true)
                .positiveText(R.string.confirm)
                .titleGravity(GravityEnum.CENTER)
                .titleColorRes(R.color.light_green_800)
                .limitIconToDefaultSize()
                .backgroundColorRes(R.color.white_solid)
                // .negativeColorRes(R.color.material_red_400)
                .iconRes(R.drawable.ic_ofertas)
                .dividerColorRes(R.color.colorPrimary)
                .contentColorRes(android.R.color.black)
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .positiveColor(Color.WHITE)
                .autoDismiss(false)
                //.negativeColorAttr(android.R.attr.textColorSecondaryInverse)
                .onPositive(
                        { dialog1, which ->
                            dialog1.dismiss()

                        })
                .show()*/

        /*return   dialog.show()

        return   MaterialDialog.Builder(activity!!)
                .iconRes(R.mipmap.ic_launcher)
                .limitIconToDefaultSize() // limits the displayed icon size to 48dp
                .title(R.string.alert)
                .content(R.string.verificate_conexion, true)
                .positiveText(R.string.confirm)
                .show()
        */
    }

    override fun showProgressHud(){
        hud = KProgressHUD.create(activity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white));
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




    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    override fun confirmResusedOferta(oferta: Oferta): AlertDialog?{
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setMessage(getString(R.string.title_alert_refused_oferta));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            oferta.EstadoOfertaId=EstadosOfertasResources.RECHAZADO
            oferta.Nombre_Estado_Oferta=EstadosOfertasResources.RECHAZADO_STRING
            presenter?.updateOferta(oferta, Producto_Id)
        })
        builder.setIcon(R.drawable.ic_ofertas);
        return builder.show();
    }


    override fun confirmAceptOferta(oferta: Oferta): AlertDialog?{
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setMessage(getString(R.string.title_alert_acept_oferta));
        builder.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            oferta.EstadoOfertaId=EstadosOfertasResources.CONFIRMADO
            oferta.Nombre_Estado_Oferta=EstadosOfertasResources.CONFIRMADO_STRING
            presenter?.updateOferta(oferta, Producto_Id)
        })
        builder.setIcon(R.drawable.ic_ofertas);
        return builder.show();
    }

    override fun showViewDialogImage(ruta: String?) {
        val inflater = this.layoutInflater
        val viewDialogImage = inflater.inflate(R.layout.dialog_detail_foto, null)
        if(ruta!=null){
            GlideApp.with(activity!!)
                    .load(S3Resources.RootImage+"$ruta")
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fotoDetailCenterCrop()
                    .into(viewDialogImage.imgIcon)
        }else{
            viewDialogImage.imgIcon.setImageResource(R.drawable.ic_no_image_icon)
            viewDialogImage.imgIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        }
        ///ImagePlagaGlobal=enfermedad.RutaImagenEnfermedad
        val dialog = AlertDialog.Builder(context!!)
                .setView(viewDialogImage!!)
                .create()
        dialog.getWindow().setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white_transaparent)))
        val closeDialog = View.OnClickListener { dialog.dismiss() }
        viewDialogImage.ivClosetDialogImage?.setOnClickListener(closeDialog)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.getWindow().getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.show()
        dialog.getWindow().setAttributes(lp)

    }

    override fun setProducto(producto: Producto?) {
        if (productoSelectedContainer.visibility == View.GONE) {
            productoSelectedContainer.visibility = View.VISIBLE
        }
        txtNombreCultivo?.setText(producto?.NombreCultivo)
        txtNombreLote?.setText(producto?.NombreDetalleTipoProducto)
        txtPrecio?.setText(getString(R.string.title_adapter_precio_producto, producto?.Precio))
        //txtNombreProducto?.setText(producto?.Nom)
    }



    override fun navigationChat(oferta:Oferta){
        val userLogued= presenter?.getUserLogued()
        var usuario:Usuario?=null
        if(userLogued?.RolNombre.equals(RolResources.COMPRADOR)){
            usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioTo)).querySingle()
        }else{
            usuario= SQLite.select().from(Usuario::class.java).where(Usuario_Table.Id.eq(oferta.UsuarioId)).querySingle()
        }

        (activity as MenuMainActivity).showAlertTypeChat(usuario)
    }

    /*

    override fun navigationChatOnline(room: Room?, userFirebase: UserFirebase?){
        val goToUpdate = Intent(activity, ChatMessageActivity::class.java)
        goToUpdate.putExtra("USER_FIREBASE", userFirebase)
        goToUpdate.putExtra("ROOM", room)
        goToUpdate.putExtra("USER_ID", userFirebase?.User_Id)
        goToUpdate.putExtra("FOTO", userFirebase?.Imagen)
        startActivity(goToUpdate)
    }
    override  fun navigationChatSms(usuario: Usuario?){


            val inflater = this.layoutInflater
            val viewDialogConfirm = inflater.inflate(R.layout.dialog_confirm, null)

            viewDialogConfirm?.txtTitleConfirm?.setText("")
            viewDialogConfirm?.txtTitleConfirm?.setText(usuario?.Nombre+" ${usuario?.Apellidos}")


            val content =String.format(getString(R.string.user_navigation_chat_sms),usuario?.Nombre+" ${usuario?.Apellidos}")
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
                                val sms= Sms(0,
                                        "",
                                        usuario?.PhoneNumber,
                                        "",
                                        "",
                                        System.currentTimeMillis().toString(),
                                        MessageSmsType.MESSAGE_TYPE_SENT,
                                        EmisorType_Message_Resources.MESSAGE_EMISOR_TYPE_SMS,
                                        "Desconocido"
                                )

                                val goToUpdate = Intent(activity, Chat_Sms_Activity::class.java)
                                goToUpdate.putExtra(TagSmsResources.TAG_SMS_SEND, sms)
                                goToUpdate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                //goToUpdate.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(goToUpdate)
                                //Toast.makeText(activity,"Enviar oferta",Toast.LENGTH_SHORT).show()
                                // _dialogOferta?.dismiss()

                            })
                    .onNegative({ dialog1, which ->
                        dialog1.dismiss()
                        //onMessageToas(getString(R.string.content_sms_not_send),R.color.red_900)
                    })
                    .show()

    }
*/
    //endregion

    //region Methods
    override fun onRefresh() {
        showProgress()
        presenter?.getListOfertas(Producto_Id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
