package com.interedes.agriculturappv3.comprador.comercial.productos

import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.producto.Producto
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.adapters.ProductosAdapter
import com.interedes.agriculturappv3.productor.modules.comercial_module.productos.events.ProductosEvent
import java.util.ArrayList


class ProductosCompradorAdapter(var lista: ArrayList<Usuario>, val listener: (Int) -> Unit) : RecyclerView.Adapter<ProductosCompradorAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ProductosCompradorAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosCompradorAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return ProductosCompradorAdapter.ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return lista.size
    }


    fun setItems(newItems: List<Usuario>) {
        lista.addAll(newItems)
        notifyDataSetChanged()

    }

    fun clear() {
        lista.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(data: Usuario, pos: Int, listener: (Int) -> Unit) = with(itemView) {
            val contentIcon: ImageView = itemView.findViewById(R.id.contentIcon)
            val txtNombreProductor: TextView = itemView.findViewById(R.id.txtTitle)
            val txtCorreo: TextView = itemView.findViewById(R.id.txtDescription)
            val txtIdentificacion: TextView = itemView.findViewById(R.id.txtDate)
            val txtTelefono: TextView = itemView.findViewById(R.id.txtQuantity)
            val btnAction1: ImageButton = itemView.findViewById(R.id.btnAction1)
            val btnAction2: ImageButton = itemView.findViewById(R.id.btnAction2)
            val btnAction3: ImageButton = itemView.findViewById(R.id.btnAction3)

            contentIcon.setImageResource(R.drawable.ic_productor)
            txtNombreProductor.text = data.Nombre + " " + data.Apellidos
            txtCorreo.text = data.Email
            txtIdentificacion.text = data.Identificacion
            btnAction1.setImageResource(R.drawable.ic_celular)
            btnAction2.visibility = View.GONE
            btnAction3.visibility = View.GONE



        }
    }
}