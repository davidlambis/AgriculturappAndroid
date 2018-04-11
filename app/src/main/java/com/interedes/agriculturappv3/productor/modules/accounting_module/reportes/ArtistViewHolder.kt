package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import android.view.View
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder

class ArtistViewHolder(itemView: View): ChildViewHolder(itemView) {
    var childTextView: TextView?
    init {
        childTextView =itemView.findViewById(R.id.list_item_artist_name)
    }

    fun setArtistName(name: String) {
        childTextView?.text = name
    }
}