package com.interedes.agriculturappv3.modules.notification

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal

class NotificationActivity : AppCompatActivity(),IMainViewNotification.MainView {


    var presenter: IMainViewNotification.Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        presenter = NotificationPresenter(this)
        presenter?.onCreate()
    }



    //region IMPLEMENTS IMAIN VIEW NOTIFICATION
    override fun showProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgress() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showProgressHud() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hideProgressHud() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun limpiarCampos() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setListNotification(listNotification: List<NotificationLocal>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNewNotification(notification: NotificationLocal) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageToas(message: String, color: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestResponseOK() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestResponseError(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageOk(colorPrimary: Int, msg: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onMessageError(colorPrimary: Int, msg: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun checkConectionInternet() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onEventBroadcastReceiver(extras: Bundle, intent: Intent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    //endregion

}
