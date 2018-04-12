package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.viewholder.CategoriaPukViewHolder
import com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.viewholder.TransaccionesViewHolder
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class CategoriaPukAdapter(groups: List<ExpandableGroup<*>> ): ExpandableRecyclerViewAdapter<CategoriaPukViewHolder, TransaccionesViewHolder>(groups) {

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
        holder.setArtistName(transacciones.Concepto!!, transacciones.Valor_Total.toString())
    }

    override fun onBindGroupViewHolder(holder: CategoriaPukViewHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>) {
        holder.setGenreTitle(group)
    }
}