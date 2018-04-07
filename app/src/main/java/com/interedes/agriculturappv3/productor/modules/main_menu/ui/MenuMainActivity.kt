package com.interedes.agriculturappv3.productor.modules.ui.main_menu

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaScannerConnection
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
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


class MenuMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, MainViewMenu {

    // var coordsService: CoordsService? = null
    //var coordsGlobal:Coords?=null
    var presenter: MenuPresenterImpl? = null
    var usuario_logued: Usuario? = null
    var inflaterGlobal: MenuInflater? = null
    var menuItemGlobal: MenuItem? = null


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


    //region EVENTS UI
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_notification) {
            // Handle the camera action
            // val i = Intent(this, SettingsActivity::class.java)
            // startActivity(i)
        } else if (id == R.id.nav_market) {

        } else if (id == R.id.nav_account) {


        }

        drawer_layout.closeDrawer(GravityCompat.START)

        return true
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
        val retIntent = Intent(Const.SERVICE_CONECTIVITY)
        retIntent.putExtra("state_conectivity", true)
        this?.sendBroadcast(retIntent)
        onMessageOk(R.color.colorPrimary, getString(R.string.on_connectividad))
    }

    override fun offConnectivity() {
        val retIntent = Intent(Const.SERVICE_CONECTIVITY)
        retIntent.putExtra("state_conectivity", false)
        this?.sendBroadcast(retIntent)
        onMessageError(R.color.grey_luiyi, getString(R.string.off_connectividad))
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

    //endregion


    override fun onResume() {
        super.onResume()
        presenter?.onResume(this)
    }

    override fun onDestroy() {
        presenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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




