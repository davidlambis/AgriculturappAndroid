package com.interedes.agriculturappv3.activities.chat.util

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.ChatMessageActivity
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.chat.ChatMessage
import com.interedes.agriculturappv3.productor.models.chat.UserFirebase
import com.squareup.picasso.Picasso


class MessagesAdapter(var mMessagesList: ArrayList<ChatMessage>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    val ITEM_TYPE_SENT = 0
    val ITEM_TYPE_RECEIVED = 1

    companion object {
        var eventBus: EventBus? = null
        /*fun postEventc(type: Int, produccion: Produccion?) {
            var produccionMutable= produccion as Object
            val event = RequestEventProduccion(type,null, produccionMutable,null)
            event.eventType = type
            eventBus?.post(event)
        }*/
    }

    init {
        eventBus = GreenRobotEventBus()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(mMessagesList[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        var v: View? = null
        if (viewType == ITEM_TYPE_SENT) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.sent_msg_row, null)
        } else if (viewType == ITEM_TYPE_RECEIVED) {
            v = LayoutInflater.from(parent.context).inflate(R.layout.received_msg_row, null)
        }
        return ViewHolder(v!!) // view holder for header items
        //val v = LayoutInflater.from(parent.context).inflate(R.layout.user_single_row, parent, false)
        //return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return mMessagesList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (mMessagesList[position].senderId.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
            ITEM_TYPE_SENT
        } else {
            ITEM_TYPE_RECEIVED
        }
    }

    fun setItems(newItems: List<ChatMessage>) {
        mMessagesList.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        mMessagesList.clear()
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        mMessagesList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(position: Int, person: ChatMessage) {
        mMessagesList.add(position, person)
        notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: ChatMessage, pos: Int) = with(itemView) {

            var messageTextView: TextView = itemView.findViewById(R.id.chatMsgTextView)

            if(getItemViewType()==1){
               // var imageUser: TextView = itemView.findViewById(R.id.imageView2)
               
            }

            messageTextView.setText(data.message)



        }
    }
}
