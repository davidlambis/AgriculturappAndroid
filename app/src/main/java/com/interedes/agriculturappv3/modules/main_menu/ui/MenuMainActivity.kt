package com.interedes.agriculturappv3.modules.productor.ui.main_menu

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.modules.main_menu.fragment.ui.adapter.AdapterFragmetMenu
import com.interedes.agriculturappv3.modules.main_menu.fragment.ui.MainMenuFragment
import kotlinx.android.synthetic.main.activity_menu_main.*
import com.interedes.agriculturappv3.modules.main_menu.ui.MenuPresenterImpl
import com.interedes.agriculturappv3.modules.main_menu.ui.MainViewMenu
import com.interedes.agriculturappv3.services.Const
import com.raizlabs.android.dbflow.sql.language.SQLite
import android.view.MenuInflater
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.interedes.agriculturappv3.activities.chat.chat_sms.UserSmsActivity
import com.interedes.agriculturappv3.activities.chat.online.ChatUsersActivity
import com.interedes.agriculturappv3.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.interedes.agriculturappv3.modules.account.AccountFragment
import com.interedes.agriculturappv3.modules.comprador.productos.ProductosCompradorFragment
import com.interedes.agriculturappv3.modules.models.sincronizacion.QuantitySync
import com.interedes.agriculturappv3.services.resources.RolResources
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.kaopiz.kprogresshud.KProgressHUD
import com.raizlabs.android.dbflow.kotlinextensions.save
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.dialog_sync_data.view.*
import kotlinx.android.synthetic.main.navigation_drawer_header.*


class MenuMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainViewMenu.MainView,View.OnClickListener {



    // var coordsService: CoordsService? = null
    //var coordsGlobal:Coords?=null
    var presenter: MenuPresenterImpl? = null
    var usuario_logued: Usuario? = null
    var inflaterGlobal: MenuInflater? = null
    var menuItemGlobal: MenuItem? = null
    var mPhoneNumber: String? = null


    //Progress
    private var hud: KProgressHUD?=null

    //Firebase
    //FIREBASE
     var mUserDBRef: DatabaseReference? = null
     var mStorageRef: StorageReference? = null
     var mCurrentUserID: String? = null
    var mAuth: FirebaseAuth? = null


    //Alert Dialog Sync
    var viewDialogSync:View?= null
    var _dialogSync: AlertDialog? = null

    //Chat Type
    private var DIALOG_SET_TYPE_CHAT: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)

        if (getLastUserLogued() != null) {
            usuario_logued = getLastUserLogued()
        }
        //Presenter
        presenter = MenuPresenterImpl(this)
        (presenter as MenuPresenterImpl).onCreate()
        setToolbarInjection()
        setNavDrawerInjection()
        setAdapterFragment()
        // this.coordsService = CoordsService(this)
        //fragmentManager.beginTransaction().add(R.id.container, AccountingFragment()).commit()

        //Firebase
        mCurrentUserID = FirebaseAuth.getInstance()?.currentUser?.uid
        //init firebase
        mUserDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")

        populateTheViews()

        //Status Chat
        makeUserOnline()
        getListasIniciales()
    }


    override fun getListasIniciales() {
        presenter?.getListasIniciales()
    }

    private fun populateTheViews() {
        try {
            if(mUserDBRef!=null){
                mUserDBRef?.child(FirebaseAuth.getInstance().currentUser?.uid)?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val currentuser = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                        try {
                            val userPhoto = currentuser?.Imagen
                            val userName = currentuser?.Nombre
                            val userLastName = currentuser?.Apellido
                            val userIdentification = currentuser?.Cedula
                            Picasso.with(applicationContext).load(userPhoto).placeholder(R.drawable.ic_logo_productor).into(circleImageView)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {

                    }
                })
            }
        }catch (ex: Exception){
           // Log.println(ex)

        }


    }

    fun setAdapterFragment() {
        val fragmentManager = supportFragmentManager
        val MainMenuFragment = MainMenuFragment()
        val ProductosCompradorFragment = ProductosCompradorFragment()

        if (getLastUserLogued()?.RolNombre.equals(RolResources.PRODUCTOR)) {
            AdapterFragmetMenu(MainMenuFragment, fragmentManager, R.id.container)
        } else if (getLastUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)) {
            AdapterFragmetMenu(ProductosCompradorFragment, fragmentManager, R.id.container)
        }

    }

    //TODO pasar al repository
    fun getLastUserLogued(): Usuario? {
      return presenter?.getLastUserLogued()
    }

    //region SETUP INJECTION


    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)//devolver
        toolbar.setTitle(getString(R.string.title_menu))
    }

    private fun setNavDrawerInjection() {

        val mActionBarDrawerToggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.nav_open, R.string.nav_close)
        mActionBarDrawerToggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        drawer_layout.addDrawerListener(mActionBarDrawerToggle)
        val header = navigationView.getHeaderView(0)
        val headerViewHolder = HeaderViewHolder(header)
        headerViewHolder.tvNombreUsuario.setText(String.format(getString(R.string.nombre_usuario_nav), usuario_logued?.Nombre, usuario_logued?.Apellidos))
        headerViewHolder.tvIdentificacion.setText(usuario_logued?.Email)

        headerViewHolder.tvNombreUsuario.setOnClickListener(this)
        headerViewHolder.circleImageView.setOnClickListener(this)

        // val header = navigationView.getHeaderView(0)
        // val headerViewHolder = HeaderViewHolder(header)
        // headerViewHolder.tvNombreUsuario.setText(usuarioLogued.getNombre() + " " + usuarioLogued.getApellido())
        // headerViewHolder.tvCCUsuario.setText("C.C " + usuarioLogued.getCedula())
        // headerViewHolder.tvEmpresaUsuario.setText(usuarioController.getEmpresaById(empresaId).getNombre())
        // if (ciudadId != 0L && departamentoId != 0L) {
        //    headerViewHolder.tvCiudadUsuario.setText(usuarioController.getCiudadById(ciudadId).getNombre() + ","
        //           + usuarioController.getDepartamentoById(departamentoId).getNombre())
        // }
    }
    //endregion

    class HeaderViewHolder(view: View) {
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuario)
        val circleImageView: CircleImageView = view.findViewById(R.id.circleImageView)
        val tvIdentificacion: TextView = view.findViewById(R.id.tvIdentificacion)
    }


    //region MENU

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //getMenuInflater().inflate(R.menu.menu_main_fragment, menu)
        inflaterGlobal = menuInflater
        inflaterGlobal?.inflate(R.menu.menu_main_fragment, menu)

        menuItemGlobal = menu?.findItem(R.id.action_menu_icon)
        menuItemGlobal?.isVisible = false

        var itemSync=menu?.findItem(R.id.action_menu_sync)
        if (getLastUserLogued()?.RolNombre.equals(RolResources.PRODUCTOR)) {
            itemSync?.isVisible = true
        } else if (getLastUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)) {
            itemSync?.isVisible = false
        }

        return true
        //return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.action_menu_icon_chat -> {
                ///startActivity(Intent(this, ChatUsersActivity::class.java))
                showAlertTypeChat()
                return true
            }

           /* R.id.action_menu_email -> {
                startActivity(Intent(this, UserSmsActivity::class.java))
                return true
            }*/

            R.id.action_menu_sync -> {
                presenter?.syncQuantityData()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    /*
    override fun showAlertTypeChat(): AlertDialog? {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.select_type_chat))
        Toast.makeText(this, getString(R.string.message_location_lote), Toast.LENGTH_SHORT).show()
        //var options=arrayOf(getString(R.string.location_type_gps), getString(R.string.location_type_manual))
        var options  = resources.getStringArray(R.array.array_type_chat)
        //La lista varia dependiendo del tipo de conectividad
        builder.setSingleChoiceItems(options, DIALOG_SET_TYPE_CHAT, DialogInterface.OnClickListener { dialog, which ->
            when (which) {
            //Position State Location GPS
                0 -> {
                    DIALOG_SET_TYPE_CHAT=0
                    dialog.dismiss()
                    startActivity(Intent(this, ChatUsersActivity::class.java))
                    //scheduleDismiss();
                }
            //Position State Location Manual
                else -> {
                    DIALOG_SET_TYPE_CHAT=1
                    dialog.dismiss()
                    startActivity(Intent(this, UserSmsActivity::class.java))
                    //showAlertDialogSelectUp()
                }
            }
        })
        builder.setIcon(R.drawable.ic_localizacion_finca);
        return builder.show();
    }*/


    override fun showAlertTypeChat(){

        val adapter=MaterialSimpleListAdapter({dialog, index, item ->
            if(index==0){
                dialog.dismiss()
                startActivity(Intent(this, UserSmsActivity::class.java))
            }else{
                dialog.dismiss()
                startActivity(Intent(this, ChatUsersActivity::class.java))
            }
        })

        adapter.add(MaterialSimpleListItem.Builder(this)
                    .content(getString(R.string.tittle_chat_sms))
                    .icon(R.drawable.ic_action_email_green)
                    .backgroundColor(Color.WHITE)
                  //  .backgroundColorRes(R.color.black)
                    .build());
        adapter.add(
                 MaterialSimpleListItem.Builder(this)
                    .content(getString(R.string.tittle_chat_online))
                    .icon(R.drawable.ic_action_sms_green)
                         .backgroundColor(Color.WHITE)
                       //  .backgroundColorRes(R.color.black)
                    .build());

        MaterialDialog.Builder(this)
                .title(R.string.select_type_chat)
                .autoDismiss(true)
                .adapter(adapter, null).show();
    }



    //endregion

    //region EVENTS UI
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_proveedores) {
            // Handle the camera action
            // val i = Intent(this, SettingsActivity::class.java)
            // startActivity(i)
        } else if (id == R.id.nav_ofertas) {

        } else if (id == R.id.nav_compras_realizadas) {


        } else if (id == R.id.nav_productos) {


        } else if (id == R.id.nav_preguntas) {


        }else if (id == R.id.nav_singout_session) {
            showExit()
            showExit()

        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    fun showExit(): Dialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setNegativeButton("Cancelar") { dialog, which -> Log.i("Dialogos", "Confirmacion Cancelada.") }
        builder.setMessage("¿Cerrar Sesión?")
        builder.setPositiveButton("Aceptar") { dialog, which ->

            var userLogued=getLastUserLogued()
            mAuth = FirebaseAuth.getInstance()
            if (mAuth?.currentUser != null) {
                mAuth?.signOut()
            }
            userLogued?.UsuarioRemembered = false
            userLogued?.AccessToken = null
            userLogued?.save()

            makeUserOffline()
            startActivity(Intent(this, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            finish()
        }
        builder.setIcon(R.mipmap.ic_launcher)
        return builder.show()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvNombreUsuario -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                replaceFragment(AccountFragment())
            }
            R.id.circleImageView -> {
                drawer_layout.closeDrawer(GravityCompat.START)
                replaceFragment(AccountFragment())
            }


        }
    }

    override fun onBackPressed() {

        val count = supportFragmentManager.backStackEntryCount
        if (count == 1) {
            super.onBackPressed()

            if (getLastUserLogued()?.RolNombre.equals(RolResources.PRODUCTOR)) {
                replaceCleanFragment(MainMenuFragment())
            } else if (getLastUserLogued()?.RolNombre.equals(RolResources.COMPRADOR)) {
                replaceCleanFragment(ProductosCompradorFragment())
            }

        } else {
            supportFragmentManager.popBackStack()
        }
        /*
        if (count == 1) {
            super.onBackPressed()
            replaceCleanFragment(MainMenuFragment())

        } else {
            supportFragmentManager.popBackStackImmediate()
        }*/
    }


    //endregion

    //region NAVIGATION FRAGMENTS

    fun replaceFragment(fragment: Fragment) {
        /*val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, fragment)
        fragmentTransaction?.addToBackStack(fragment::class.java.getName())
        fragmentTransaction?.commit()*/

        val fragmentManager: FragmentManager
        val fragmentTransaction: FragmentTransaction
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager.beginTransaction()
        val inboxFragment = fragment
        fragmentTransaction.replace(R.id.container, inboxFragment)
        // fragmentTransaction?.addToBackStack(fragment::class.java.getName())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction.commit()
    }
/*
    fun replaceCleanFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, fragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }*/


    fun replaceCleanFragment(fragment: Fragment) {
        val fragmentManager: FragmentManager
        val fragmentTransaction: FragmentTransaction

        fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        fragmentTransaction = fragmentManager.beginTransaction()
        val inboxFragment = fragment
        fragmentTransaction.replace(R.id.container, inboxFragment)
        fragmentTransaction.commit()

    }



    //endregion

    //region Implemetacion Interfaz ViewMenu


    override fun showProgressHud(){
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white_solid))
                .setDetailsLabel("Sincronizando Informacion")
        hud?.show()
    }

    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun showProgressBar() {
        progressBarSync?.visibility=View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBarSync?.visibility=View.GONE
    }


    override fun requestResponseOK() {
        onMessageOk(R.color.colorPrimary,getString(R.string.request_ok));
    }

    override fun requestResponseError(error: String?) {
        onMessageError(R.color.red_900,error);
    }


    override fun verificateSync(quantitySync: QuantitySync?): AlertDialog? {
        val inflater = this.layoutInflater
        viewDialogSync = inflater.inflate(R.layout.dialog_sync_data , null)

        var message= ""
        if(quantitySync?.CantidadRegistrosSync!!.toInt()==0 && quantitySync?.CantidadUpdatesSync!!.toInt()==0 ){
            message=getString(R.string.tittle_no_sync)
            viewDialogSync?.imageViewSyncNone?.visibility=View.VISIBLE
        }else{
            message=getString(R.string.tiitle_verificate_sync)
            viewDialogSync?.imageViewSyncNone?.visibility=View.GONE
        }

        viewDialogSync?.txtCantidadRegistros?.setText(String.format(getString(R.string.size_register_sync),quantitySync?.CantidadRegistrosSync))
        viewDialogSync?.txtCantidadActulizaciones?.setText(String.format(getString(R.string.size_updates_sync),quantitySync?.CantidadUpdatesSync))


        if(presenter?.checkConnection()!!){
            viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_online_user);
            viewDialogSync?.txtconectividad?.setText(getString(R.string.on_connectividad));
        }else{
            viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_offline_user);
            viewDialogSync?.txtconectividad?.setText(getString(R.string.off_connectividad));
        }

        var builder = AlertDialog.Builder(this)
                .setView(viewDialogSync)
                .setTitle(getString(R.string.alert))
                .setMessage(message)
                .setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
                    presenter?.syncData()
                })
                .setNegativeButton(getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                })
                .setIcon(R.drawable.ic_cloud_sync)
                .create()
        builder.show()

        _dialogSync=builder
        return _dialogSync
    }


    override fun setQuantitySync(quantitySync: QuantitySync?){
        verificateSync(quantitySync)
    }

    override fun verificateConnection(): AlertDialog? {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.alert));
        builder.setMessage(getString(R.string.verificate_conexion));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })
        builder.setIcon(R.drawable.ic_contabilidad_color_500);
        return builder.show();
    }

    override fun onConnectivity() {

       if(viewDialogSync!=null){
           viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_online_user);
           viewDialogSync?.txtconectividad?.setText(getString(R.string.on_connectividad));
       }

        /// mUserDBRef?.database?.goOnline()
        /*
        var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);
        */
        val retIntent = Intent(Const.SERVICE_CONECTIVITY)
        retIntent.putExtra("state_conectivity", true)
        this?.sendBroadcast(retIntent)
        onMessageOk(R.color.colorPrimary, getString(R.string.on_connectividad))
        makeUserOnline()
    }

    override fun offConnectivity() {

        if(viewDialogSync!=null){
            viewDialogSync?.viewEstateConect?.setBackgroundResource(R.drawable.is_offline_user);
            viewDialogSync?.txtconectividad?.setText(getString(R.string.off_connectividad));
        }


      /*  var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);*/

       // mUserDBRef?.database?.goOffline()
        val retIntent = Intent(Const.SERVICE_CONECTIVITY)
        retIntent.putExtra("state_conectivity", false)
        this?.sendBroadcast(retIntent)
        onMessageError(R.color.grey_luiyi, getString(R.string.off_connectividad))
        makeUserOffline()
    }




    fun makeUserOnline()
    {
        /*
        // Firebase Status
        var fbquery = FirebaseDatabase.getInstance().getReference("status/" + user.userId).setValue("online")


        // Adding on disconnect hook
        FirebaseDatabase.getInstance().getReference("/status/" + user.userId)
                .onDisconnect()     // Set up the disconnect hook
                .setValue("offline");

                */
        var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.ONLINE)
        userStatus?.onDisconnect()?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.onDisconnect()?.setValue(ServerValue.TIMESTAMP);

    }

    fun makeUserOffline()
    {
        var userStatus= mUserDBRef?.child(mCurrentUserID+"/status")
        var userLastOnlineRef= mUserDBRef?.child(mCurrentUserID+"/last_Online")
        userStatus?.setValue(Status_Chat.OFFLINE)
        userLastOnlineRef?.setValue(ServerValue.TIMESTAMP);
        // Firebase
        //var fbquery = FirebaseDatabase.getInstance().getReference("status/" + mCurrentUserID).setValue(Status_Chat.OFFLINE)
        //var fbquery2 = FirebaseDatabase.getInstance().getReference("last_Online/" + mCurrentUserID).setValue(ServerValue.TIMESTAMP)
    }


    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, colorPrimary))
        val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.quantum_ic_cast_connected_white_24, 0, 0, 0)
        // textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin));
        textView.setTextColor(color)
        snackbar.show()
    }

    override fun onMessageError(colorPrimary: Int, message: String?) {

        onMessageOk(colorPrimary, message)
    }




    //endregion


    //region OVERRIDES METHODS
    override fun onResume() {
        super.onResume()
        presenter?.onResume(this)
    }

    override fun onDestroy() {

        presenter?.onDestroy(this)
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //endregion
}


//region BroadcastReceiver LOCALIZACION
///Escucha Los valores enviados por Serial Port desde Menu Activity
/*private val mNotificationReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val lat = intent.extras!!.getDouble("latitud");
        val long = intent.extras!!.getDouble("longitud");
        coordsGlobal= Coords(lat, long,"");
        MessageCoordsOk(coordsGlobal);
    }
}

  public override fun onResume() {
    super.onResume()

    registerReceiver(mNotificationReceiver, IntentFilter("LOCATION"))

}

private fun MessageCoordsOk(coords:Coords?) {
   // Toast.makeText(this,""+coords?.Latitud,Toast.LENGTH_SHORT).show()
}
fun getLocation(): Coords {
    val coords= Coords(coordsGlobal?.Latitud, coordsGlobal?.Longitud,"");
    return coords;
}


*/
/*----------------------------------------------------------------------------------------------------------------------*/




