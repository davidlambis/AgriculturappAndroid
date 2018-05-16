package com.interedes.agriculturappv3.modules.models.ventas.resports

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class Genre(var nombre: String, items: List<Artist>, val iconResId: Int) : ExpandableGroup<Artist>(nombre, items) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Genre) return false
        val genre = o as Genre?
        return iconResId == genre!!.iconResId
    }

    override fun hashCode(): Int {
        return iconResId
    }
}