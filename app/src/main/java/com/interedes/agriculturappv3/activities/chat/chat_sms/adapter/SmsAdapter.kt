package com.interedes.agriculturappv3.activities.chat.chat_sms.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.models.Sms
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.services.resources.MessageSmsType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*




class SmsAdapter(var mMessagesList: ArrayList<Sms>) : RecyclerView.Adapter<SmsAdapter.ViewHolder>() {


    companion object {
        val ITEM_TYPE_SENT = 0
        val ITEM_TYPE_RECEIVED = 1
        var eventBus: EventBus? = null
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

        return if (mMessagesList[position]._folderName.equals(MessageSmsType.MESSAGE_TYPE_SENT)) {
            ITEM_TYPE_SENT
        } else {
            ITEM_TYPE_RECEIVED
        }
    }

    fun setItems(newItems: List<Sms>) {
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

    fun add(position: Int, person: Sms) {
        mMessagesList.add(position, person)
        notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Sms, pos: Int) = with(itemView) {

            var messageTextView: TextView = itemView.findViewById(R.id.chatMsgTextView)
            var txtHour: TextView = itemView.findViewById(R.id.txtHour)



            try {
                var strGotDate = data._time;
                var longGotDate = strGotDate?.toLong()
                // Format that date
                var formattedGotDate =  SimpleDateFormat("h:mm a").format(longGotDate);
                txtHour.setText(formattedGotDate)

            } catch (e: ParseException) {
                e.printStackTrace()
            }


            /*
            try {
                var dateObjStart = Date()
                val sdfStart = SimpleDateFormat("H:mm")
                dateObjStart = sdfStart.parse(data.hour)
                var hora12 = SimpleDateFormat("KK:mm a").format(dateObjStart)
                txtHour.setText(hora12)

            } catch (e: ParseException) {
                e.printStackTrace()
            }*/
            if(getItemViewType()== ITEM_TYPE_RECEIVED){

            }

            messageTextView.setText(data._msg)
        }
    }
}
