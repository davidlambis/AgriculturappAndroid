package com.interedes.agriculturappv3.productor.modules.accounting_module.reportes


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_reporte.*
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.productor.models.ventas.resports.GenreDataFactory


class ReporteFragment : Fragment() {
    var adapter: GenreAdapter?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reporte, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(activity)


        // RecyclerView has some built in animations to it, using the DefaultItemAnimator.
        // Specifically when you call notifyItemChanged() it does a fade animation for the changing
        // of the data in the ViewHolder. If you would like to disable this you can use the following:
        val animator = recycler_view.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        adapter = GenreAdapter(GenreDataFactory.makeGenres())
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter


        toggle_button.setOnClickListener { adapter!!.toggleGroup(GenreDataFactory.makeClassicGenre()) }
    }

}
