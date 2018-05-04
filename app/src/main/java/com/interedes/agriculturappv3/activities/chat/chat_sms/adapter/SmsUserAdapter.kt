package com.interedes.agriculturappv3.activities.chat.chat_sms.adapter

import android.content.Intent
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.Chat_Sms_Activity
import com.interedes.agriculturappv3.activities.chat.chat_sms.SmsUser
import com.interedes.agriculturappv3.activities.chat.online.ChatMessageActivity
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.chat.UserFirebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat


class SmsUserAdapter(var lista: ArrayList<SmsUser>) : RecyclerView.Adapter<SmsUserAdapter.ViewHolder>() {

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
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<SmsUser>) {
        lista.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        lista.removeAt(position)
        notifyItemRemoved(position)
    }

    fun add(position: Int, person: SmsUser) {
        lista.add(position, person)
        notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: SmsUser, pos: Int) = with(itemView) {

            var personNameTxtV: TextView = itemView.findViewById(R.id.txtTitle)
            var txtDate: TextView = itemView.findViewById(R.id.txtDate)
            var personImageImgV: ImageView = itemView.findViewById(R.id.contentIcon)
            personImageImgV.visibility= View.GONE

            var txtSmsAddress: TextView = itemView.findViewById(R.id.txtDescription)
            var txtSmsAdditional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)

            var optionsContent: LinearLayout = itemView.findViewById(R.id.options)
            optionsContent.visibility= View.GONE

            var contentIconUser: CircleImageView = itemView.findViewById(R.id.contentIconUser)
            contentIconUser.visibility= View.VISIBLE


            personNameTxtV.setText(data._user_name)
            txtSmsAddress.setText(data._address)

            txtSmsAdditional.maxLines=1
            txtSmsAdditional.ellipsize=TextUtils.TruncateAt.END
            txtSmsAdditional.setTypeface(null, Typeface.NORMAL);
            txtSmsAdditional.setText(data._msg)


            var strGotDate = data._time;
            var longGotDate = strGotDate?.toLong()
            // Format that date
            var formattedGotDate =  SimpleDateFormat("MM/dd/yyyy h:mm a").format(longGotDate);
            txtDate.setText(formattedGotDate)
            try {
                contentIconUser.setImageResource(R.drawable.default_avata)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            itemView.setOnClickListener {
                //postEventc(RequestEventProduccion.ITEM_EVENT,data)
                val TAG = "SMSCHATAPP"
                val goToUpdate = Intent(context, Chat_Sms_Activity::class.java)
                goToUpdate.putExtra(TAG, data._address)
                context.startActivity(goToUpdate)

                Toast.makeText(context,"Chat Sms ",Toast.LENGTH_SHORT).show()
            }
        }
    }
}