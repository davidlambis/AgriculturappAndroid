package com.interedes.agriculturappv3.productor.modules.comercial_module.productos.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.interedes.agriculturappv3.productor.models.control_plaga.ControlPlaga
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.adapters.ControlPlagasAdapter
import com.interedes.agriculturappv3.productor.modules.asistencia_tecnica_module.control_plagas.events.ControlPlagasEvent
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.events.ProductosEvent
import java.text.SimpleDateFormat
import java.util.ArrayList


class ProductosAdapter(var lista: ArrayList<Producto>) : RecyclerView.Adapter<ProductosAdapter.ViewHolder>() {


    init {
        eventBus = GreenRobotEventBus()
    }

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, producto: Producto?) {
            val productoMutable = producto as Object
            val event = ProductosEvent(type, null, productoMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    override fun onBindViewHolder(holder: ProductosAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_control_plagas, parent, false)
        return ProductosAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Producto>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Producto, pos: Int) = with(itemView) {

        }
    }

}