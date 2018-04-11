package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import android.view.LayoutInflater
import android.view.ViewGroup
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.ventas.resports.Genre
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup

class GenreAdapter(groups: List<ExpandableGroup<*>> ): ExpandableRecyclerViewAdapter<GenreViewHolder, ArtistViewHolder>(groups) {

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindChildViewHolder(holder: ArtistViewHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>, childIndex: Int) {

        val artist = (group as Genre).getItems().get(childIndex)
        holder.setArtistName(artist.name!!)
    }

    override fun onBindGroupViewHolder(holder: GenreViewHolder, flatPosition: Int,
                                       group: ExpandableGroup<*>) {

        holder.setGenreTitle(group)
    }
}