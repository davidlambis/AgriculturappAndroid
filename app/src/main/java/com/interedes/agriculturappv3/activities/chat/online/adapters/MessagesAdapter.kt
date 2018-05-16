package com.interedes.agriculturappv3.activities.chat.online.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.online.ChatMessageActivity
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.chat.ChatMessage
import com.interedes.agriculturappv3.modules.models.chat.UserFirebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class MessagesAdapter(var mMessagesList: ArrayList<ChatMessage>) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {


    companion object {

        val ITEM_TYPE_SENT = 0
        val ITEM_TYPE_RECEIVED = 1
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
        var item = mMessagesList[position]


        holder.bindItems(item, position)
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
            var txtHour: TextView = itemView.findViewById(R.id.txtHour)

            try {
                var dateObjStart = Date()
                val sdfStart = SimpleDateFormat("H:mm")
                dateObjStart = sdfStart.parse(data.hour)
                var hora12 = SimpleDateFormat("KK:mm a").format(dateObjStart)
                txtHour.setText(hora12)

            } catch (e: ParseException) {
                e.printStackTrace()
            }







            if(getItemViewType()== ITEM_TYPE_RECEIVED){
                var imageUser: CircleImageView = itemView.findViewById(R.id.imageView2)

                if((context as ChatMessageActivity).mReceiverFoto!=null){
                    try {
                        Picasso.with(context).load((context as ChatMessageActivity).mReceiverFoto).placeholder(R.drawable.default_avata).into(imageUser)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            messageTextView.setText(data.message)
        }
    }
}
