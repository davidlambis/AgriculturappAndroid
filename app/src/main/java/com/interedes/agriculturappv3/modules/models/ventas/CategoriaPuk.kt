package com.interedes.agriculturappv3.modules.models.ventas

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.config.DataSource
import com.interedes.agriculturappv3.modules.models.ventas.resports.Artist
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


@Table(database = DataSource::class)
data class CategoriaPuk(@PrimaryKey
                   @SerializedName("Id")
                   @Column(name = "Id")
                   var Id: Long? = 0,

                   @SerializedName("nombre")
                   @Column(name = "nombre")
                   var Nombre: String? = null,

                   @SerializedName("SIGLA")
                   @Column(name = "Sigla")
                   var Sigla: String? = null,


                        var Valor_Total: Double?=null,

                        @SerializedName("Pucs")
                        var Pucs: ArrayList<Puk> ?= null,
                       // var transaccions: List<Transaccion>? = null
                        var transaccions: ArrayList<Transaccion> ?= ArrayList<Transaccion>()

                   ): ExpandableGroup<Transaccion>(Nombre, transaccions) {

    override fun toString(): String {
        return Nombre!!
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is CategoriaPuk) return false
        val categoria = o as CategoriaPuk?
        return Id == categoria!!.Id
    }

    override fun hashCode(): Int {
        return Id!!.toInt()
    }


}