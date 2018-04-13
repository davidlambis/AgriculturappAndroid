package com.interedes.agriculturappv3.productor.modules.comercial_module.clientes.adapters

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
import com.interedes.agriculturappv3.productor.models.usuario.Usuario
import com.interedes.agriculturappv3.productor.modules.comercial_module.clientes.events.ClientesEvent
import java.util.ArrayList

class ClientesAdapter(var lista: ArrayList<Usuario>) : RecyclerView.Adapter<ClientesAdapter.ViewHolder>() {

    init {
        eventBus = GreenRobotEventBus()
    }

    companion object {
        var eventBus: EventBus? = null
        fun postEvent(type: Int, cliente: Usuario?) {
            val clienteMutable = cliente as Object
            val event = ClientesEvent(type, null, clienteMutable, null)
            event.eventType = type
            eventBus?.post(event)
        }
    }

    override fun onBindViewHolder(holder: ClientesAdapter.ViewHolder, position: Int) {
        //Llama al método del holder para cargar los items
        holder.bindItems(lista[position], position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientesAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_list_general, parent, false)
        return ClientesAdapter.ViewHolder(v)
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
        fun bindItems(data: Usuario, pos: Int) = with(itemView) {
            val contentIcon: ImageView = itemView.findViewById(R.id.contentIcon)
            val txtNombreCliente: TextView = itemView.findViewById(R.id.txtTitle)
            val txtCorreo: TextView = itemView.findViewById(R.id.txtDescription)
            val txtIdentificacion: TextView = itemView.findViewById(R.id.txtDate)
            val txtTelefono: TextView = itemView.findViewById(R.id.txtQuantity)
            val btnAction1: ImageButton = itemView.findViewById(R.id.btnAction1)
            val btnAction2: ImageButton = itemView.findViewById(R.id.btnAction2)
            val btnAction3: ImageButton = itemView.findViewById(R.id.btnAction3)

            btnAction1.visibility = View.GONE
            btnAction2.visibility = View.GONE
            btnAction3.visibility = View.GONE

            contentIcon.setImageResource(R.drawable.ic_clientes)
            txtNombreCliente.text = String.format(context.getString(R.string.nombre_cliente), data.Nombre, data.Apellidos)
            txtCorreo.text = data.Email
            txtIdentificacion.text = data.Identificacion
            txtTelefono.text = data.PhoneNumber
        }
    }
}