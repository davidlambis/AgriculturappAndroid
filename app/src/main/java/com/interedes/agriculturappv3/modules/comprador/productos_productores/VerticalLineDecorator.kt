package com.interedes.agriculturappv3.modules.comprador.productos_productores

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class VerticalLineDecorator(spaces: Int): RecyclerView.ItemDecoration() {
    private var space = 0

    init {
        space = spaces
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {

        if (parent.getChildAdapterPosition(view) == 0)
            outRect.top = space

        outRect.bottom = space
    }
}