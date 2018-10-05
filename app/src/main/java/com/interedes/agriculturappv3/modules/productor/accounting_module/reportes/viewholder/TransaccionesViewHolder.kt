package com.interedes.agriculturappv3.modules.productor.accounting_module.reportes.viewholder

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.interedes.agriculturappv3.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class TransaccionesViewHolder(itemView: View): ChildViewHolder(itemView) {

    var transaccionpValorTextView: TextView?
    var transaccionpTextView: TextView?
    init {
        transaccionpTextView =itemView.findViewById(R.id.list_item_transaccion_concep)
        transaccionpValorTextView =itemView.findViewById(R.id.list_item_transaccion_valor)
    }

    fun setArtistName(context:Context?,concep: String,valor: Double ) {




        transaccionpTextView?.text = concep
        transaccionpValorTextView?.text = String.format("$ %,.0f",valor)


        transaccionpTextView?.setOnClickListener {
             Toast.makeText(context,"Hola",Toast.LENGTH_SHORT).show()
        }
    }
}