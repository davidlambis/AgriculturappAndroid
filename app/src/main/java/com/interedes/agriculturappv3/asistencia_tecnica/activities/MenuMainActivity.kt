package com.interedes.agriculturappv3.asistencia_tecnica.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.interedes.agriculturappv3.R
import kotlinx.android.synthetic.main.activity_menu_main.*


class MenuMainActivity : AppCompatActivity(),  NavigationView.OnNavigationItemSelectedListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_main)

        setToolbarInjection()
        setNavDrawerInjection()
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



    //endregion

}
