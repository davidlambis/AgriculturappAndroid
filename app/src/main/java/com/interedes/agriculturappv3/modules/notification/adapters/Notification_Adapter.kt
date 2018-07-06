package com.interedes.agriculturappv3.modules.notification.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.Notification.NotificationLocal
import com.interedes.agriculturappv3.modules.notification.events.RequestEventsNotification
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*




class Notification_Adapter(var list: ArrayList<NotificationLocal>) : RecyclerView.Adapter<Notification_Adapter.ViewHolder>() {


    companion object {

        var eventBus: EventBus? = null
        fun postEventc(type: Int, notificationLocal: NotificationLocal?) {
            var produccionMutable= notificationLocal as Object
            val event = RequestEventsNotification(type,null, produccionMutable,null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    init {
        eventBus = GreenRobotEventBus()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //Llama al método del holder para cargar los items
        var item = list[position]

        holder.bindItems(item, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return ViewHolder(v!!) // view holder for header items
        //val v = LayoutInflater.from(parent.context).inflate(R.layout.user_single_row, parent, false)
        //return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    fun setItems(newItems: List<NotificationLocal>) {
        list.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add( notification: NotificationLocal) {

        if(!list.contains(notification)){
            list.add(notification)
            notifyDataSetChanged()
        }
        //notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: NotificationLocal, pos: Int) = with(itemView) {


        }
    }
}
