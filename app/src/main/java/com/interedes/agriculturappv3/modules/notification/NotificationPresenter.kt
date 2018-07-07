package com.interedes.agriculturappv3.modules.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.notification.events.RequestEventsNotification
import com.interedes.agriculturappv3.services.Const
import com.interedes.agriculturappv3.services.internet_connection.ConnectivityReceiver
import org.greenrobot.eventbus.Subscribe

class NotificationPresenter(var mainView:IMainViewNotification.MainView?):IMainViewNotification.Presenter {


    var interactor: IMainViewNotification.Interactor? = null
    var eventBus: EventBus? = null

    companion object {
        var instance: NotificationPresenter? = null
    }

    init {
        instance = this
        interactor = NotificationInteractor()
        eventBus = GreenRobotEventBus()
    }

    override fun onCreate() {
        eventBus?.register(this)

    }

    override fun onDestroy() {
        mainView = null
        eventBus?.unregister(this)
    }

    //region Conectividad
    private val mNotificationReceiverApp = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var extras = intent.extras
            if (extras != null) {
                mainView?.onEventBroadcastReceiver(extras, intent)
            }
        }
    }

    override fun checkConnection(): Boolean {
        return ConnectivityReceiver.isConnected
    }

    override fun onResume(context: Context) {
        context.registerReceiver(mNotificationReceiverApp, IntentFilter(Const.SERVICE_CONECTIVITY))
    }

    override fun onPause(context: Context) {
        context.unregisterReceiver(this.mNotificationReceiverApp);
    }

    //endregion

    //region Suscribe Events
    @Subscribe
    override fun onEventMainThread(event: RequestEventsNotification?) {
        when (event?.eventType) {

            RequestEventsNotification.UPDATE_EVENT -> {
                val notification = event.objectMutable as NotificationLocal
                mainView?.onChangeNotification(notification)
                //onMessageOk()
            }

            RequestEventsNotification.DELETE_EVENT -> {
                val notification = event.objectMutable as NotificationLocal
               mainView?.onRemoveNotification(notification)
            }

            RequestEventsNotification.ITEM_NEW_EVENT -> {
                val notification = event.objectMutable as NotificationLocal
                mainView?.setNewNotification(notification)
            }

            RequestEventsNotification.ERROR_EVENT -> {
                onMessageError(event.mensajeError)
            }

            RequestEventsNotification.ERROR_VERIFICATE_CONECTION -> {
                mainView?.checkConectionInternet()
            }

            RequestEventsNotification.LIST_EVENT_NOTIFICATION -> {
                val notification = event.mutableList as MutableList<NotificationLocal>
                mainView?.hideProgress()
                mainView?.setListNotification(notification)
            }

            //ON ITEM CLICK
            RequestEventsNotification.ITEM_READ_EVENT -> {
                val notification = event.objectMutable as NotificationLocal
                mainView?.onNavigationdetailNotification(notification)
            }

            RequestEventsNotification.ITEM_DELETE_EVENT -> {
                val notification = event.objectMutable as NotificationLocal
                mainView?.confirmDelete(notification)
            }

            RequestEventsNotification.RELOAD_LIST_NOTIFICATION -> {
                getListNotification()
            }
        }
    }
    //endregion



    //region METHODs

    override fun getListNotification() {
        interactor?.getListNotification()
    }

    override fun updateNotifications(notification: NotificationLocal) {
      interactor?.updateNotifications(notification)
    }

    override fun deleteNotification(notification: NotificationLocal) {
       interactor?.deleteNotification(notification)
    }


    //endregion

    //endregion
    //
    // region Acciones de Respuesta a Post de Eventos
    private fun onSaveOk() {
        onMessageOk()
    }

    private fun onUpdateOk() {
        onMessageOk()
    }

    private fun onDeleteOk() {
        mainView?.hideProgress()
        mainView?.requestResponseOK()
    }
    //endregion

    //region Messages/Notificaciones
    private fun onMessageOk() {
        mainView?.hideProgress()
        mainView?.requestResponseOK()
    }

    private fun onMessageError(error: String?) {
        mainView?.hideProgress()
        mainView?.requestResponseError(error)
    }

    private fun onMessageConectionError() {
        mainView?.hideProgress()
    }
}