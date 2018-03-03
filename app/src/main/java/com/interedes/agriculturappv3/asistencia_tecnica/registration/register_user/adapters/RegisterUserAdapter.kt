package com.interedes.agriculturappv3.asistencia_tecnica.registration.register_user.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.adapters.SingleAdapter
import com.interedes.agriculturappv3.asistencia_tecnica.models.ItemLista
import com.interedes.agriculturappv3.asistencia_tecnica.models.Rol


//Recibe directamente la lista y el listener del click
class RegisterUserAdapter(val lista: ArrayList<Rol>, val listener: (Int) -> Unit) : RecyclerView.Adapter<RegisterUserAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Rol, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            val image: ImageView = itemView.findViewById(R.id.imageView)
            val textNombre: TextView = itemView.findViewById(R.id.textView)
            //image.setImageBitmap(data.Imagen)
            image.setImageResource(data.Imagen)
            textNombre.text = data.Nombre
            //El listener en base a la posición
            itemView.setOnClickListener {
                listener(pos)
            }

        }

    }

}