package com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.ofertas.Oferta
import com.interedes.agriculturappv3.productor.modules.comercial_module.ofertas.events.OfertasEvent
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


        }
    }
}