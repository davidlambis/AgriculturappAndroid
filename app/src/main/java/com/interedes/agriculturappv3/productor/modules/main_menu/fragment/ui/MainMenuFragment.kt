package com.interedes.agriculturappv3.productor.modules.main_menu.fragment.ui

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.login.ui.LoginActivity
import com.interedes.agriculturappv3.asistencia_tecnica.modules.accounting_module.AccountingFragment
import com.interedes.agriculturappv3.productor.adapters.SingleAdapter
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.AsistenciaTecnicaFragment
import com.interedes.agriculturappv3.productor.modules.comercial_module.ComercialFragment
import com.interedes.agriculturappv3.productor.modules.main_menu.fragment.presenter.MainMenuFragmentPresenter
import com.interedes.agriculturappv3.productor.modules.main_menu.fragment.presenter.MainMenuFragmentPresenterImpl
import com.interedes.agriculturappv3.productor.modules.ui.main_menu.MenuMainActivity
import com.interedes.agriculturappv3.services.Resources_Menu
import com.interedes.agriculturappv3.services.listas.Listas
import kotlinx.android.synthetic.main.activity_menu_main.*
import kotlinx.android.synthetic.main.fragment_main_menu.*


/**
 * A simple [Fragment] subclass.
 */
class MainMenuFragment : Fragment(), MainMenuFragmentView {


    var presenter: MainMenuFragmentPresenter? = null

    companion object {
        var connectivity_state: Boolean? = false
    }

    init {
        presenter = MainMenuFragmentPresenterImpl(this)
        (presenter as MainMenuFragmentPresenterImpl).onCreate()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getListasIniciales()
        loadItems()
        setupInit()
    }


    private fun setupInit() {
        (activity as MenuMainActivity).toolbar.title = getString(R.string.title_menu)
        var sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.colorPrimary));

        } else {
            (activity as MenuMainActivity).toolbar.setBackgroundColor(ContextCompat.getColor((activity as MenuMainActivity), R.color.colorPrimary));
        }
        (activity as MenuMainActivity).toolbar.setTitleTextColor(resources.getColor(R.color.white))
        var iconMenu = (activity as MenuMainActivity).menuItemGlobal
        iconMenu?.isVisible = false



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = (activity as MenuMainActivity).getWindow()
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            // finally change the color
            window.statusBarColor = ContextCompat.getColor((activity as MenuMainActivity), R.color.colorPrimary)
            (activity as MenuMainActivity).app_bar_main.elevation = 4f

        } else {
            (activity as MenuMainActivity).app_bar_main.targetElevation = 4f
        }
    }

    override fun getListasIniciales() {
        presenter?.getListasIniciales()
    }

    //region Métodos Interfaz
    override fun loadItems() {
        recyclerView?.layoutManager = GridLayoutManager(activity, 2)
        if ((activity as MenuMainActivity).getLastUserLogued()?.RolNombre.equals("Productor")) {
            val lista = Listas.listaMenuProductor()
            val adapter = SingleAdapter(lista, Resources_Menu.MENU_MAIN, activity) { position ->
                if (lista[position].Identificador.equals("asistencia_tecnica")) {
                    (activity as MenuMainActivity).replaceFragment(AsistenciaTecnicaFragment())
                } else if (lista[position].Identificador.equals("comercial")) {
                    (activity as MenuMainActivity).replaceFragment(ComercialFragment())
                } else if (lista[position].Identificador.equals("contabilidad")) {
                    (activity as MenuMainActivity).replaceFragment(AccountingFragment())
                } else if (lista[position].Identificador.equals("salir")) {
                    presenter?.logOut((activity as MenuMainActivity).getLastUserLogued())
                }
            }
            recyclerView?.adapter = adapter
        } else if ((activity as MenuMainActivity).getLastUserLogued()?.RolNombre.equals("Comprador")) {
            val lista = Listas.listaMenuComprador()
            val adapter = SingleAdapter(lista, Resources_Menu.MENU_MAIN, activity) { position ->
                if (lista[position].Identificador.equals("comercial")) {
                    (activity as MenuMainActivity).replaceFragment(ComercialFragment())
                } else if (lista[position].Identificador.equals("salir")) {
                    presenter?.logOut((activity as MenuMainActivity).getLastUserLogued())
                }
            }
            recyclerView?.adapter = adapter
        }

    }

    //Broadcast Receiver
    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if (extras != null) {
            if (extras.containsKey("state_conectivity")) {
                val state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    override fun navigateToLogin() {
        showExit()
    }

    override fun errorLogOut(error: String?) {
        onMessageError(R.color.grey_luiyi, error)
    }

    override fun onMessageOk(colorPrimary: Int, message: String?) {
        val color = Color.WHITE
        val snackbar = Snackbar
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(context!!, colorPrimary))
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

    //region Métodos
    fun showExit(): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Confirmación")
        builder.setNegativeButton("Cancelar") { dialog, which -> Log.i("Dialogos", "Confirmacion Cancelada.") }
        builder.setMessage("¿Cerrar Sesión?")
        builder.setPositiveButton("Aceptar") { dialog, which ->
            startActivity(Intent(activity, LoginActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            activity!!.finish()
        }
        builder.setIcon(R.mipmap.ic_launcher)
        return builder.show()
    }


    //endregion


    //region Ciclo de Vida Fragment
    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause(activity!!.applicationContext)
    }

    override fun onResume() {
        super.onResume()
        presenter?.onResume(activity!!.applicationContext)
    }

    //endregion

}// Required empty public constructor
