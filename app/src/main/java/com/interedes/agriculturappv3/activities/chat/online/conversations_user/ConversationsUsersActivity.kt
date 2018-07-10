package com.interedes.agriculturappv3.activities.chat.online.conversations_user

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.online.conversations_user.adapter.UsersAdapter
import kotlinx.android.synthetic.main.activity_chat_users.*
import java.util.ArrayList

import com.interedes.agriculturappv3.modules.models.chat.RoomConversation
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.custom_message_toast.view.*


class ConversationsUsersActivity : AppCompatActivity(), IMainViewConversacion.MainView{
    private var adapter: UsersAdapter? = null
    //Progress
    private var hud: KProgressHUD?=null
    var presenter: IMainViewConversacion.Presenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_users)
        presenter = Conversacion_Presenter(this)
        presenter?.onCreate()
        setToolbarInjection()
        initAdapter()
        //presenter?.getListRoom()
    }

    private fun initAdapter() {
        usersRecyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = UsersAdapter(ArrayList<RoomConversation>())
        usersRecyclerView?.adapter = adapter
    }

    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (supportActionBar != null)
        // Habilitar Up Button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.title = getString(R.string.title_usuario)
    }

    //region IMPLMENTS METHODS INTERFACE
    override fun setListRoom(sms: List<RoomConversation>) {
        adapter?.clear()
        adapter?.setItems(sms)
    }

    override fun showProgress() {
        //  swipeRefreshLayout.setRefreshing(true);
    }
    override fun hideProgress() {
        // swipeRefreshLayout.setRefreshing(false);
    }
    override fun showProgressHud(){
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white))
        hud?.show()
    }
    override fun hideProgressHud(){
        hud?.dismiss()
    }

    override fun onMessageToas(message: String, color: Int) {
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(message)
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(this, color))
        var mytoast =  Toast(this);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }

        }
    }
    //endregion

    //region OVERRIDES METHODS
    override fun onStart() {
        super.onStart()
        checkIfUserIsSignIn()
        presenter?.getListRoom()
        /**query usrs and add them to a list */
        ///queryUsersAndAddthemToList()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.onDestroy()
    }

    //endregion


    //region METHODS

    private fun checkIfUserIsSignIn() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in
        } else {
            // No user is signed in
            /**go to login user first */
            goToSignIn()
        }
    }
    private fun goToSignIn() {
        //startActivity(Intent(this, LoginActivity::class.java))
    }

    //endregion

    //region MENU
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
        ///Metodo que permite no recargar la pagina al devolverse
            android.R.id.home -> {
                // Obtener intent de la actividad padre
                val upIntent = NavUtils.getParentActivityIntent(this)
                upIntent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                // Comprobar si DetailActivity no se creó desde CourseActivity
                if (NavUtils.shouldUpRecreateTask(this, upIntent) || this.isTaskRoot) {
                    // Construir de nuevo la tarea para ligar ambas actividades
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities()
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    this.finishAfterTransition()
                    return true
                }
                //Para versiones anterios a 5.x
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    onBackPressed()
                    return true
                }
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion
}
