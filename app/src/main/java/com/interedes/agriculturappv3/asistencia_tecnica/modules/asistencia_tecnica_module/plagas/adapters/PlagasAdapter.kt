package com.interedes.agriculturappv3.asistencia_tecnica.modules.asistencia_tecnica_module.plagas.adapters


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.libs.EventBus
import com.interedes.agriculturappv3.libs.GreenRobotEventBus

class PlagasAdapter(val lista: ArrayList<TipoEnfermedad>) : RecyclerView.Adapter<PlagasAdapter.ViewHolder>() {

    companion object {
        var instance: PlagasAdapter? = null
    }

    var eventBus: EventBus? = null

    init {
        eventBus = GreenRobotEventBus()
        PlagasAdapter.instance = this
    }

    override fun onBindViewHolder(holder: PlagasAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlagasAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_plagas, parent, false)
        return PlagasAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun setItems(newItems: List<TipoEnfermedad>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: TipoEnfermedad, pos: Int) = with(itemView) {
            val txt_nombre_enfermedad: TextView = itemView.findViewById(R.id.txtNombreEnfermedad)
            val txt_nombre_cientifico: TextView = itemView.findViewById(R.id.txtNombreCientifico)
            val image_enfermedad: ImageView = itemView.findViewById(R.id.imageViewEnfermedad)
            val txt_descripcion_enfermedad: TextView = itemView.findViewById(R.id.txtDescripcionEnfermedad)
            val btn_aplicar_tratamiento: Button = itemView.findViewById(R.id.btnVerInsumos)

            txt_nombre_enfermedad.text = data.Nombre
            txt_nombre_cientifico.text = data.NombreCientifico
            image_enfermedad.setImageResource(data.Imagen!!)
            txt_descripcion_enfermedad.text = data.Descripcion

            /*txt_nombre_cultivo.text = data.Nombre
            txt_detalle_tipo_producto.text = data.Nombre_Detalle_Tipo_Producto
            txt_descripcion_cultivo.text = data.Descripcion
            txt_cosecha_estimada.text = data.EstimadoCosecha.toString()
            txt_fecha_inicio.text = data.FechaIncio
            txt_fecha_fin.text = data.FechaFin*/
        }
    }


}