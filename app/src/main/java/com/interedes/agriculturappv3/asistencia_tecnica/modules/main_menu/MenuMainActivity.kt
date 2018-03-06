package com.interedes.agriculturappv3.asistencia_tecnica.modules.main_menu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Coords
import com.interedes.agriculturappv3.asistencia_tecnica.services.coords.CoordsService
import kotlinx.android.synthetic.main.activity_menu_main.*


class MenuMainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //var coordsService: CoordsService? = null
    //var coordsGlobal:Coords?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)
        setToolbarInjection()
        setNavDrawerInjection()
        setAdapterFragment()
        //this.coordsService = CoordsService(this)
        //fragmentManager.beginTransaction().add(R.id.container, AccountingFragment()).commit()
    }

    fun setAdapterFragment() {
        val fragmentManager = supportFragmentManager
        val MainMenuFragment = MainMenuFragment()
        AdapterFragmetMenu(MainMenuFragment, fragmentManager, R.id.container)
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
            supportFragmentManager.popBackStackImmediate()
        }


    }


    //endregion

    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager

        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, fragment)
        fragmentTransaction?.addToBackStack(fragment::class.java.getName())
        fragmentTransaction?.commit()
    }

    fun replaceCleanFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.container, fragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
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
    private fun MessageCoordsOk(coords:Coords?) {
       // Toast.makeText(this,""+coords?.Latitud,Toast.LENGTH_SHORT).show()
    }
    fun getLocation(): Coords {
        val coords= Coords(coordsGlobal?.Latitud, coordsGlobal?.Longitud,"");
        return coords;
    }

     public override fun onResume() {
        super.onResume()

        registerReceiver(mNotificationReceiver, IntentFilter("LOCATION"))

    }
    */
    /*----------------------------------------------------------------------------------------------------------------------*/


}


