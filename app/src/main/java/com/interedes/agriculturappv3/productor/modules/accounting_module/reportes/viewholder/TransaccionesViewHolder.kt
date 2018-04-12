package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes.viewholder

import android.view.View
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class TransaccionesViewHolder(itemView: View): ChildViewHolder(itemView) {
    var transaccionpValorTextView: TextView?
    var transaccionpTextView: TextView?
    init {
        transaccionpTextView =itemView.findViewById(R.id.list_item_transaccion_concep)
        transaccionpValorTextView =itemView.findViewById(R.id.list_item_transaccion_valor)
    }

    fun setArtistName(concep: String,valor: String ) {
        transaccionpTextView?.text = concep
        transaccionpValorTextView?.text = valor
    }
}