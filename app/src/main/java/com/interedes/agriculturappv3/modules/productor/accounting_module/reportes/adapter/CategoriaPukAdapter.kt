package com.interedes.agriculturappv3.modules.productor.accounting_module.reportes.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.modules.productor.accounting_module.reportes.viewholder.CategoriaPukViewHolder
import com.interedes.agriculturappv3.modules.productor.accounting_module.reportes.viewholder.TransaccionesViewHolder
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class CategoriaPukAdapter(context:Context,groups: List<ExpandableGroup<*>> ): ExpandableRecyclerViewAdapter<CategoriaPukViewHolder, TransaccionesViewHolder>(groups) {


    companion object {
        var contextLocal:Context?=null
    }

    init {
        contextLocal=context
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): CategoriaPukViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_categoria_puk, parent, false)
        return CategoriaPukViewHolder(view)
    }

    fun setItems(newItems: List<ExpandableGroup<*>>) {
        groups.addAll(newItems as Collection<Nothing>)
        notifyDataSetChanged()
    }

    fun clear() {
        groups.clear()
        notifyDataSetChanged()
    }


    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): TransaccionesViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_transaccion, parent, false)
        return TransaccionesViewHolder(view)
    }

    override fun onBindChildViewHolder(holder: TransaccionesViewHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>, childIndex: Int) {

        val transacciones = (group as CategoriaPuk).getItems().get(childIndex)

       // Toast.makeText(contextLocal,"Hola",Toast.LENGTH_SHORT).show()



        holder.setArtistName(contextLocal,transacciones.Concepto!!, transacciones.Valor_Total!!)
    }

    override fun onBindGroupViewHolder(holder: CategoriaPukViewHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>) {
        holder.setGenreTitle(group)
    }
}