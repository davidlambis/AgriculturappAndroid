package com.interedes.agriculturappv3.productor.modules.accounting_module.transacciones.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.ventas.Estado_Transaccion
import com.interedes.agriculturappv3.productor.modules.accounting_module.transacciones.events.RequestEventTransaccion
import android.view.View;
import android.widget.RadioGroup
import android.widget.Toast
import android.widget.TextView




class EstadoTransaccionAdapter(var lista: ArrayList<Estado_Transaccion>)    : RecyclerView.Adapter<EstadoTransaccionAdapter.ViewHolder>() {

    companion object {
        var lastCheckedRadioGroup: RadioGroup? = null
        var eventBus: EventBus? = null
        fun postEvent(type: Int, estado_Transaccion: Estado_Transaccion?) {
            var transaccionnMutable= estado_Transaccion as Object
            val event = RequestEventTransaccion(type, null, transaccionnMutable, null)
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_radio_button, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Estado_Transaccion>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Estado_Transaccion, pos: Int) = with(itemView) {

            //var txtNameRadioButton: TextView = itemView.findViewById(R.id.txtNameRadioButton)
            var radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroup)

            val packageModel = data
            //txtNameRadioButton.setText(packageModel.nombre)
            var id =data.Id
            val rb = RadioButton(this.context)
            rb.id = id!!.toInt()
            rb.setText(packageModel.Nombre)
            rb.setOnClickListener {
                postEvent(RequestEventTransaccion.ITEM_EVENT_RADIO_TYPE_TRANSACION,data)
            }

            radioGroup.addView(rb)
            radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                //since only one package is allowed to be selected
                //this logic clears previous selection
                //it checks state of last radiogroup and
                // clears it if it meets conditions
                if (lastCheckedRadioGroup != null
                        && lastCheckedRadioGroup!!.checkedRadioButtonId != radioGroup.checkedRadioButtonId
                        && lastCheckedRadioGroup!!.checkedRadioButtonId != -1) {
                    lastCheckedRadioGroup!!.clearCheck()
                    //postEvent(RequestEventTransaccion.ITEM_EVENT_RADIO_TYPE_TRANSACION,data)

                }
                lastCheckedRadioGroup = radioGroup
            }

        }
    }

/*
     class ViewHolderdd(view: View) : RecyclerView.ViewHolder(view) {

        var packageName: TextView
        var priceGroup: RadioGroup

        init {
            packageName = view.findViewById(R.id.package_name) as TextView
            priceGroup = view.findViewById(R.id.price_grp) as RadioGroup

            priceGroup.setOnCheckedChangeListener { radioGroup, i ->
                //since only one package is allowed to be selected
                //this logic clears previous selection
                //it checks state of last radiogroup and
                // clears it if it meets conditions
                if (lastCheckedRadioGroup != null
                        && lastCheckedRadioGroup.getCheckedRadioButtonId() !== radioGroup.checkedRadioButtonId
                        && lastCheckedRadioGroup.getCheckedRadioButtonId() !== -1) {
                    lastCheckedRadioGroup.clearCheck()

                    Toast.makeText(this@PackageRecyclerViewAdapter.context,
                            "Radio button clicked " + radioGroup.checkedRadioButtonId,
                            Toast.LENGTH_SHORT).show()

                }
                lastCheckedRadioGroup = radioGroup
            }
        }
    }*/
}