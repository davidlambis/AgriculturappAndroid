package com.interedes.agriculturappv3.modules.productor.comercial_module.ofertas.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.productor.comercial_module.ofertas.events.OfertasEvent
import java.util.ArrayList

class OfertasAdapter(var lista: ArrayList<Oferta>) : RecyclerView.Adapter<OfertasAdapter.ViewHolder>() {

    init {
        eventBus = GreenRobotEventBus()
    }

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, oferta: Oferta?) {
            val ofertaMutable = oferta as Object
            val event = OfertasEvent(type, null, ofertaMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    override fun onBindViewHolder(holder: OfertasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return OfertasAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Oferta>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Oferta, pos: Int) = with(itemView) {
            val contentIcon: ImageView = itemView.findViewById(R.id.contentIcon)
            val txtNombreCliente: TextView = itemView.findViewById(R.id.txtTitle)
            val txtPrecio: TextView = itemView.findViewById(R.id.txtDescription)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val btnAction1: ImageButton = itemView.findViewById(R.id.btnAction1)
            val btnAction2: ImageButton = itemView.findViewById(R.id.btnAction2)
            val btnAction3: ImageButton = itemView.findViewById(R.id.btnAction3)

            btnAction1.visibility = View.GONE
            btnAction2.setImageResource(R.drawable.ic_close_white)
            btnAction2.setBackgroundColor(ContextCompat.getColor(context, R.color.red_900))
            btnAction3.setImageResource(R.drawable.ic_done_white)
            btnAction3.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))


            contentIcon.setImageResource(R.drawable.ic_ofertas)
            txtNombreCliente.text = String.format(context.getString(R.string.title_cliente), data.NombreUsuario)
            txtPrecio.text = String.format(context.getString(R.string.title_precio_oferta), data.Valor_Oferta)
            txtDate.text = String.format(context.getString(R.string.title_fecha_oferta), data.getCreatedOnFormat())


        }
    }
}