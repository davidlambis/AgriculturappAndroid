package com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.thunder413.datetimeutils.DateTimeUtils
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.activities.chat.chat_sms.user_sms_ui.events.RequestEventUserSms
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.sms.Sms
import com.interedes.agriculturappv3.services.resources.TagSmsResources
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class SmsUserAdapter(var lista: ArrayList<Sms>) : RecyclerView.Adapter<SmsUserAdapter.ViewHolder>() {

    companion object {
        var eventBus: EventBus? = null
        fun postEventc(type: Int, sms: Sms?) {
            var smsMutable= sms as Object
            val event = RequestEventUserSms(type, null, smsMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
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


    fun setItems(newItems: List<Sms>) {
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

    fun add(position: Int, person: Sms) {
        lista.add(position, person)
        notifyItemInserted(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Sms, pos: Int) = with(itemView) {

            var personNameTxtV: TextView = itemView.findViewById(R.id.txtTitle)
            var txtDate: TextView = itemView.findViewById(R.id.txtDate)
            var txtQuantity: TextView = itemView.findViewById(R.id.txtQuantity)


            var btnAgregar: TextView = itemView.findViewById(R.id.btnAgregar)

            var personImageImgV: ImageView = itemView.findViewById(R.id.contentIcon)
            personImageImgV.visibility= View.GONE

            var txtSmsAddress: TextView = itemView.findViewById(R.id.txtDescription)
            var txtSmsAdditional: TextView = itemView.findViewById(R.id.txtDescripcionAdditional)

            var optionsContent: LinearLayout = itemView.findViewById(R.id.options)
            optionsContent.visibility= View.GONE

            var contentIconUser: CircleImageView = itemView.findViewById(R.id.contentIconUser)
            contentIconUser.visibility= View.VISIBLE


            personNameTxtV.setText(data.ContactName)

            if(data.ContactName.equals(TagSmsResources.CONTACT_DESCONOCIDO)){

                btnAgregar.visibility=View.VISIBLE
                txtQuantity.visibility=View.GONE

            }else{
                btnAgregar.visibility=View.GONE
                txtQuantity.visibility=View.VISIBLE
            }

            txtSmsAddress.setText(data.Address)

            txtSmsAdditional.maxLines=1
            txtSmsAdditional.ellipsize=TextUtils.TruncateAt.END
            txtSmsAdditional.setTypeface(null, Typeface.NORMAL);
            txtSmsAdditional.setText(data.Message)


            try{
                var strGotDate = data.FechaSms;
                var longGotDate = strGotDate?.toLong()

                // Format that date
                //var formattedGotDate =  SimpleDateFormat("MM/dd/yyyy h:mm a").format(longGotDate);
                txtDate.setText(DateTimeUtils.getTimeAgo(context, Date(longGotDate!!)))

            }catch (ex:Exception){
                var error = ex.toString()
            }


            try {
                contentIconUser.setImageResource(R.drawable.default_avata)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            itemView.setOnClickListener {
                //postEventc(RequestEventProduccion.ITEM_EVENT,data)
                postEventc(RequestEventUserSms.ITEM_EVENTS_DETAIL_SMS, data)
            }

            btnAgregar.setOnClickListener {
                //postEventc(RequestEventProduccion.ITEM_EVENT,data)
                postEventc(RequestEventUserSms.ITEM_EVENTS_ADD_CONTAT, data)
            }
        }
    }
}