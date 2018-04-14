package com.interedes.agriculturappv3.productor.modules.comercial_module.ventas.adapters

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
import com.interedes.agriculturappv3.productor.models.compras.Compras
import com.interedes.agriculturappv3.productor.modules.comercial_module.ventas.events.VentasEvent
import java.util.ArrayList


class VentasAdapter(var lista: ArrayList<Compras>) : RecyclerView.Adapter<VentasAdapter.ViewHolder>() {

    init {
        eventBus = GreenRobotEventBus()
    }

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, venta: Compras?) {
            val ventaMutable = venta as Object
            val event = VentasEvent(type, null, ventaMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    override fun onBindViewHolder(holder: VentasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VentasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return VentasAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Compras>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Compras, pos: Int) = with(itemView) {
            val contentIcon: ImageView = itemView.findViewById(R.id.contentIcon)
            //val txtNombreUsuario: TextView = itemView.findViewById(R.id.txtTitle)
            //val txtPrecio: TextView = itemView.findViewById(R.id.txtDescription)
            val txtProducto: TextView = itemView.findViewById(R.id.txtTitle)
            val txtNombreUsuario: TextView = itemView.findViewById(R.id.txtDescription)
            val txtPrecio: TextView = itemView.findViewById(R.id.txtQuantity)
            val txtDate: TextView = itemView.findViewById(R.id.txtDate)
            val btnAction1: ImageButton = itemView.findViewById(R.id.btnAction1)
            val btnAction2: ImageButton = itemView.findViewById(R.id.btnAction2)
            val btnAction3: ImageButton = itemView.findViewById(R.id.btnAction3)

            contentIcon.setImageResource(R.drawable.ic_ventas)
            txtProducto.text = data.NombreProducto
            txtNombreUsuario.text = data.NombreUsuario
            txtPrecio.text = data.TotalCompra.toString()
            txtDate.text = data.getCreatedOnFormat()
            btnAction1.visibility = View.GONE
            btnAction2.visibility = View.GONE
            btnAction3.visibility = View.GONE
        }
    }

}