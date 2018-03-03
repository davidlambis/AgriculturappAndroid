package com.interedes.agriculturappv3.asistencia_tecnica.services.listas

import android.graphics.Bitmap
import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.ItemLista
import com.interedes.agriculturappv3.asistencia_tecnica.models.Rol


class Listas {

    companion object {
        fun listaRoles(): ArrayList<Rol> {
            val lista_roles = ArrayList<Rol>()
            lista_roles.add(Rol(Nombre = "Productor", Imagen = R.drawable.ic_productor_big))
            lista_roles.add(Rol(Nombre = "Comprador", Imagen = R.drawable.ic_comprador_big))
            return lista_roles
        }

        fun listaMenuProductor(): ArrayList<ItemLista> {
            val lista_menu_productor = ArrayList<ItemLista>()
            lista_menu_productor.add(ItemLista("Asistencia Técnica", Imagen = R.drawable.ic_asistencia_tecnica_color_500, Identificador = "asistencia_tecnica"))
            lista_menu_productor.add(ItemLista("Comercial", Imagen = R.drawable.ic_comercial_color_500, Identificador = "comercial"))
            lista_menu_productor.add(ItemLista("Contabilidad", Imagen = R.drawable.ic_contabilidad_color_500, Identificador = "contabilidad"))
            lista_menu_productor.add(ItemLista("Salir", Imagen = R.drawable.ic_salir_500, Identificador = "salir"))
            return lista_menu_productor
        }




    }
}
