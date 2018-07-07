package com.interedes.agriculturappv3.modules.notification

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.NavUtils
import android.support.v4.app.TaskStackBuilder
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.notification.adapters.Notification_Adapter
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.content_list_recycler_view.*
import kotlinx.android.synthetic.main.custom_message_toast.view.*

class NotificationActivity : AppCompatActivity(),IMainViewNotification.MainView ,SwipeRefreshLayout.OnRefreshListener{



    var presenter: IMainViewNotification.Presenter? = null
    var adapter: Notification_Adapter? = null
    private var hud: KProgressHUD? = null
    private var mLayoutManager: LinearLayoutManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setToolbarInjection()
        swipeRefreshLayout.setOnRefreshListener(this)

        presenter = NotificationPresenter(this)
        presenter?.onCreate()
        initAdapter()

        presenter?.getListNotification()
    }

    private fun setToolbarInjection() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        if (supportActionBar != null)
        // Habilitar Up Button
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.title = getString(R.string.tittle_notification)
    }

    private fun initAdapter() {

        //recyclerView?.setHasFixedSize(true)
        // use a linear layout manager
        //mLayoutManager = LinearLayoutManager(this)
        //mLayoutManager?.setStackFromEnd(false)
        //mLayoutManager?.setReverseLayout(true);
        //mLayoutManager?.setStackFromEnd(false);

       // mLayoutManager?.setOrientation(LinearLayoutManager.VERTICAL);
        //mLayoutManager?.setStackFromEnd(true);
        //mLayoutManager?.setSmoothScrollbarEnabled(true);
        //mLayoutManager?.setReverseLayout(true);

        mLayoutManager = LinearLayoutManager(this)
        recyclerView?.setLayoutManager(mLayoutManager)
        ///recyclerView.getLayoutManager().scrollToPosition(0)



        //recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = Notification_Adapter(ArrayList<NotificationLocal>())
        recyclerView?.adapter = adapter
        //recyclerView.smoothScrollToPosition(0);

        setResults(0)
    }

    //region IMPLEMENTS IMAIN VIEW NOTIFICATION
    override fun confirmDelete(notification: NotificationLocal): AlertDialog? {
        var builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirmation));
        builder.setNegativeButton(getString(R.string.close), DialogInterface.OnClickListener { dialog, which ->

        })
        builder.setMessage(getString(R.string.alert_delete_notification));
        builder?.setPositiveButton(getString(R.string.confirm), DialogInterface.OnClickListener { dialog, which ->
            presenter?.deleteNotification(notification)
        })
        builder.setIcon(R.drawable.ic_ic_notificacion_app);
        return builder.show();
    }


    override fun onNavigationdetailNotification(notification: NotificationLocal) {

    }

    override fun showProgress() {
        swipeRefreshLayout.setRefreshing(true)
    }

    override fun hideProgress() {
        swipeRefreshLayout.setRefreshing(false)
    }

    override fun showProgressHud() {
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setWindowColor(getResources().getColor(R.color.colorPrimary))
                .setLabel("Cargando...", resources.getColor(R.color.white));
        hud?.show()
    }

    override fun hideProgressHud() {
        hud?.dismiss()
    }


    override fun setListNotification(listNotification: List<NotificationLocal>?) {

       setResults(listNotification?.size!!)
    }

    override fun setNewNotification(notification: NotificationLocal) {
        adapter?.add(notification)
        ///recyclerView.smoothScrollToPosition(0);
        recyclerView.smoothScrollToPosition(0);

    }

    override fun onRemoveNotification(notification: NotificationLocal) {
       adapter?.remove(notification)
    }

    override fun onChangeNotification(notification: NotificationLocal) {
        adapter?.update(notification)
    }

    override fun onMessageToas(message:String,color:Int){
        val inflater = this.layoutInflater
        var viewToast = inflater.inflate(R.layout.custom_message_toast, null)
        viewToast.txtMessageToastCustom.setText(message)
        viewToast.contetnToast.setBackgroundColor(ContextCompat.getColor(this, color))
        var mytoast =  Toast(this);
        mytoast.setView(viewToast);
        mytoast.setDuration(Toast.LENGTH_SHORT);
        mytoast.show();
        ///onMessageError(R.color.red_900, getString(R.string.disabledGPS))
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
                .make(container, message!!, Snackbar.LENGTH_LONG)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(applicationContext, colorPrimary))
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
        if(extras!=null){
            if (extras.containsKey("state_conectivity")) {
                var state_conectivity = intent.extras!!.getBoolean("state_conectivity")
            }
        }
    }

    override fun setResults(notifications: Int) {
        var results = String.format(getString(R.string.results_global_search),
                notifications);
        txtResults.setText(results);
    }

    override fun checkConectionInternet() {
        onMessageToas(getString(R.string.verificate_conexion),R.color.red_900)
    }

    //endregion


    //region MENU
    //This method handles user clicks on the menu option buttons.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
        ///Metodo que permite no recargar la pagina al devolverse
            android.R.id.home -> {
                // Obtener intent de la actividad padre
                var upIntent = NavUtils.getParentActivityIntent(this);
                upIntent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Comprobar si DetailActivity no se creó desde CourseActivity
                if (NavUtils.shouldUpRecreateTask(this, upIntent!!)
                        || this.isTaskRoot()) {

                    // Construir de nuevo la tarea para ligar ambas actividades
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        TaskStackBuilder.create(this)
                                .addNextIntentWithParentStack(upIntent)
                                .startActivities();
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    this.finishAfterTransition();
                    return true;
                }

                //Para versiones anterios a 5.x
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    // Terminar con el método correspondiente para Android 5.x
                    onBackPressed();
                    return true;
                }
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //endregion


    //region OVERRIDE METHODS


    override fun onResume() {
        super.onResume()
        presenter?.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        presenter?.onPause(this)

    }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing=true
        presenter?.getListNotification()
    }


    //endregion


}
