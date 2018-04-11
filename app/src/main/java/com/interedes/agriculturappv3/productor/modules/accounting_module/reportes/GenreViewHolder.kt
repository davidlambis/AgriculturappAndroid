package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes

import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.ventas.resports.Genre
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder

class GenreViewHolder(itemView: View): GroupViewHolder(itemView) {

    var genreName: TextView?
    var arrow: ImageView?
    var icon: ImageView?

    init {
        genreName = itemView.findViewById(R.id.list_item_genre_name)
        arrow = itemView.findViewById(R.id.list_item_genre_arrow)
        icon = itemView.findViewById(R.id.list_item_genre_icon)
    }


    fun setGenreTitle(genre: ExpandableGroup<*>) {
        if (genre is Genre) {
            genreName?.setText(genre.title)
            icon?.setBackgroundResource((genre as Genre).iconResId)
        }

    }

    override fun expand() {
        animateExpand()
    }

    override fun collapse() {
        animateCollapse()
    }

    private fun animateExpand() {
        val rotate = RotateAnimation(360f, 180f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        arrow?.setAnimation(rotate)
    }

    private fun animateCollapse() {
        val rotate = RotateAnimation(180f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 300
        rotate.fillAfter = true
        arrow?.setAnimation(rotate)
    }
}