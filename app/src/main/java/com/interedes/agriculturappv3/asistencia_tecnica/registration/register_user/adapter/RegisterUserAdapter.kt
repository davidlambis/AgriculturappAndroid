package com.interedes.agriculturappv3.asistencia_tecnica.registration.register_user.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.Rol
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView


class RegisterUserAdapter(val listRoles: ArrayList<Rol>, val listener: (Int) -> Unit) : RecyclerView.Adapter<RegisterUserAdapter.ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(listRoles[position], position,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_register_user, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listRoles.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Rol, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            val image: ImageView = itemView.findViewById(R.id.imageViewRol)
            val textNombre: TextView = itemView.findViewById(R.id.textViewRol)
            image.setImageBitmap(data.Imagen)
            textNombre.text = data.Nombre
            itemView.setOnClickListener {
                listener(pos)
            }

        }

    }

}