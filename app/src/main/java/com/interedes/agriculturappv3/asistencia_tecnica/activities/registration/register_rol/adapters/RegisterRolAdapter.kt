package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_rol.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol


//Recibe directamente la lista y el listener del click
class RegisterRolAdapter(val lista: MutableList<Rol>?, val listener: (Int) -> Unit) : RecyclerView.Adapter<RegisterRolAdapter.ViewHolder>() {


    fun setItems(newLista: MutableList<Rol>) {
        lista?.addAll(newLista)
        notifyDataSetChanged()
    }

    fun clear() {
        lista?.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista!![position], position, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista!!.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Rol, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            val image: ImageView = itemView.findViewById(R.id.imageView)
            val textNombre: TextView = itemView.findViewById(R.id.textView)
            //image.setImageBitmap(data.Imagen)
            image.setImageResource(data.Imagen!!) //DESCOMENTAR
            textNombre.text = data.Nombre
            //El listener en base a la posición
            itemView.setOnClickListener {
                listener(pos)
            }

        }

    }

}