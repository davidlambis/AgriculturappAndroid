package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.insumos.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.insumos.Insumo
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus
import com.robertlevonyan.views.expandable.Expandable
import com.robertlevonyan.views.expandable.ExpandingListener

class InsumosAdapter(val lista: ArrayList<Insumo>) : RecyclerView.Adapter<InsumosAdapter.ViewHolder>() {

    companion object {
        var instance: InsumosAdapter? = null
    }

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        InsumosAdapter.instance = this
    }

    override fun onBindViewHolder(holder: InsumosAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsumosAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_insumos, parent, false)
        return InsumosAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun setItems(newItems: List<Insumo>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Insumo, pos: Int) = with(itemView) {
            val txt_nombre_insumo: TextView = itemView.findViewById(R.id.txtNombreInsumo)
            val image_view_insumo: ImageView = itemView.findViewById(R.id.imageViewInsumo)
            val text_descripcion_insumo: TextView = itemView.findViewById(R.id.txtDescripcionInsumo)
            val expandable: Expandable = itemView.findViewById(R.id.expandable)
            val txt_descripcion_insumo: TextView = itemView.findViewById(R.id.txtDescripcionInsumo)

            txt_nombre_insumo.text = data.Nombre
            image_view_insumo.setImageResource(data.Imagen!!)
            text_descripcion_insumo.text = data.Descripcion

            expandable.setExpandingListener(object : ExpandingListener {
                override fun onExpanded() {
                    txt_descripcion_insumo.visibility = View.VISIBLE
                }

                override fun onCollapsed() {
                    txt_descripcion_insumo.visibility = View.GONE
                }
            })
        }
    }
}