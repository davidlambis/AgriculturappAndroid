package com.interedes.agriculturappv3.services.listas

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.ItemLista
import com.interedes.agriculturappv3.asistencia_tecnica.models.UP
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol

class Listas {

    companion object {

        /*
        fun listaRoles(): ArrayList<Rol> {
            val lista_roles = ArrayList<Rol>()
            lista_roles.add(Rol(Nombre = "Productor", Imagen = R.drawable.ic_productor_big))
            lista_roles.add(Rol(Nombre = "Comprador", Imagen = R.drawable.ic_comprador_big))
            return lista_roles
        }*/


        fun listaRoles(): ArrayList<Rol> {
            val lista_roles = ArrayList<Rol>()
            lista_roles.add(Rol(Id = 1, Nombre = "Productor", Imagen = R.drawable.ic_productor_big))
            lista_roles.add(Rol(Id = 2, Nombre = "Comprador", Imagen = R.drawable.ic_comprador_big))
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

        fun listaAsistenciaTecnicaProductor(): ArrayList<ItemLista> {
            val lista_asistencia_tecnica_productor = ArrayList<ItemLista>()
            lista_asistencia_tecnica_productor.add(ItemLista("Mis Unidades Productivas", Imagen = R.drawable.ic_unidad_productiva, Identificador = "mis_unidades_productivas"))
            lista_asistencia_tecnica_productor.add(ItemLista("Mis Lotes", Imagen = R.drawable.ic_lote, Identificador = "mis_lotes"))
            lista_asistencia_tecnica_productor.add(ItemLista("Mis Cultivos", Imagen = R.drawable.ic_cultivos, Identificador = "mis_cultivos"))
            return lista_asistencia_tecnica_productor
        }


        fun listaUP(): ArrayList<UP> {
            val listaUP = ArrayList<UP>()
            listaUP.add(UP(1, "Unidad Productiva 1", null, null, null))
            listaUP.add(UP(2, "Unidad Productiva 2", null, null, null))
            listaUP.add(UP(3, "Unidad Productiva 3", null, null, null))
            return listaUP
        }

        fun queryGeneral(criterio: String, valor: String): String {
            val queryFilter = "$criterio eq '$valor'"
            return queryFilter
        }
    }
}
