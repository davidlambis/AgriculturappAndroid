package com.interedes.agriculturappv3.productor.modules.ui.main_menu

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.models.usuario.Usuario_Table
import com.interedes.agriculturappv3.productor.modules.main_menu.fragment.ui.adapter.AdapterFragmetMenu
import com.interedes.agriculturappv3.productor.modules.main_menu.fragment.ui.MainMenuFragment
import kotlinx.android.synthetic.main.activity_menu_main.*
import com.interedes.agriculturappv3.productor.modules.main_menu.presenter.MenuPresenterImpl
import com.interedes.agriculturappv3.productor.modules.main_menu.ui.MainViewMenu
import com.interedes.agriculturappv3.services.Const
import com.raizlabs.android.dbflow.sql.language.SQLite
import android.view.MenuInflater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.interedes.agriculturappv3.AgriculturApplication
import com.interedes.agriculturappv3.activities.chat.chat_sms.Chat_Sms_Activity
import com.interedes.agriculturappv3.activities.chat.chat_sms.UserSmsActivity
import com.interedes.agriculturappv3.activities.chat.online.ChatUsersActivity
import com.interedes.agriculturappv3.productor.models.chat.UserFirebase
import com.interedes.agriculturappv3.productor.modules.account.AccountFragment
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import com.interedes.agriculturappv3.services.resources.Status_Chat
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.navigation_drawer_header.*


class MenuMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainViewMenu,View.OnClickListener {


    // var coordsService: CoordsService? = null
    //var coordsGlobal:Coords?=null
    var presenter: MenuPresenterImpl? = null
    var usuario_logued: Usuario? = null
    var inflaterGlobal: MenuInflater? = null
    var menuItemGlobal: MenuItem? = null
    var mPhoneNumber: String? = null


    //Firebase
    //FIREBASE
     var mUserDBRef: DatabaseReference? = null
     var mStorageRef: StorageReference? = null
     var mCurrentUserID: String? = null




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
        mCurrentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        //init firebase
        mUserDBRef = FirebaseDatabase.getInstance().reference.child("Users")
        mStorageRef = FirebaseStorage.getInstance().reference.child("Photos").child("Users")

        populateTheViews()



        //Status Chat
        makeUserOnline()
    }

    private fun populateTheViews() {
        mUserDBRef?.child(FirebaseAuth.getInstance().currentUser!!.uid)?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentuser = dataSnapshot.getValue<UserFirebase>(UserFirebase::class.java)
                try {
                    val userPhoto = currentuser!!.Imagen
                    val userName = currentuser!!.Nombre
                    val userLastName = currentuser!!.Apellido
                    val userIdentification = currentuser!!.Cedula
                    Picasso.with(applicationContext).load(userPhoto).placeholder(R.drawable.ic_logo_productor).into(circleImageView)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    fun setAdapterFragment() {
        val fragmentManager = supportFragmentManager
        val MainMenuFragment = MainMenuFragment()
        AdapterFragmetMenu(MainMenuFragment, fragmentManager, R.id.container)
    }

    //TODO pasar al repository
    fun getLastUserLogued(): Usuario? {
        val usuarioLogued = SQLite.select().from(Usuario::class.java).where(Usuario_Table.UsuarioRemembered.eq(true)).querySingle()
        return usuarioLogued
    }


    //region ADAPTER FRAGMENTS

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


        }

        drawer_layout.closeDrawer(GravityCompat.START)

        return true
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
            replaceCleanFragment(MainMenuFragment())

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


    //region Implemetacion Interfaz ViewMenu
    override fun onConnectivity() {
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //getMenuInflater().inflate(R.menu.menu_main_fragment, menu)

        inflaterGlobal = menuInflater
        inflaterGlobal?.inflate(R.menu.menu_main_fragment, menu)
        menuItemGlobal = menu?.findItem(R.id.action_menu_icon)
        menuItemGlobal?.isVisible = false
        return true
        //return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.action_menu_icon_chat -> {
                startActivity(Intent(this, ChatUsersActivity::class.java))
                return true
            }

            R.id.action_menu_email -> {
                startActivity(Intent(this, UserSmsActivity::class.java))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    //endregion






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




