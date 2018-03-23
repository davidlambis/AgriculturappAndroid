package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.TipoProducto
import com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.events.PlagasEvent
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

/**
 * Created by usuario on 23/03/2018.
 */


class TipoProductosAdapter(val lista: ArrayList<TipoProducto>) : RecyclerView.Adapter<TipoProductosAdapter.ViewHolder>() {

    companion object {
        var instance: TipoProductosAdapter? = null
    }

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        TipoProductosAdapter.instance = this
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipoProductosAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.conten_list_tipo_producto, parent, false)
        return TipoProductosAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun setItems(newItems: List<TipoProducto>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: TipoProducto, pos: Int) = with(itemView) {
            val txtNombreTipoProducto: TextView = itemView.findViewById(R.id.txtNombreTipoProducto)
            val imgTipoProducto: ImageView = itemView.findViewById(R.id.imgTipoProducto)

            if(data.Nombre.equals("Aguacate")){
                imgTipoProducto.setImageResource(R.drawable.aguacate)
            }else if(data.Nombre.equals("Fríjol")){
                imgTipoProducto.setImageResource(R.drawable.frijol)
            }else if(data.Nombre.equals("Plátano")){
                imgTipoProducto.setImageResource(R.drawable.platano)
            }

            txtNombreTipoProducto.text=data.Nombre

            itemView.setOnClickListener {
                TipoProductosAdapter.instance?.postEvent(PlagasEvent.ITEM_EVENT, data)
            }
        }
    }

    fun postEvent(type: Int, tipoProducto: TipoProducto?) {
        val tipoProducto = tipoProducto as Object
        val event = PlagasEvent(type, null, tipoProducto, null)
        event.eventType = type
        eventBus?.post(event)
    }
}