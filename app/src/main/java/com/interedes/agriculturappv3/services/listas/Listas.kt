package com.interedes.agriculturappv3.services.listas

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.ItemLista
import com.interedes.agriculturappv3.asistencia_tecnica.models.UnidadProductiva
import com.interedes.agriculturappv3.asistencia_tecnica.models.rol.Rol
import com.interedes.agriculturappv3.asistencia_tecnica.models.unidad_medida.Unidad_Medida

class Listas {

    companion object {

        /*
        fun listaRoles(): ArrayList<Rol> {
            val lista_roles = ArrayList<Rol>()
            lista_roles.add(Rol(Nombre = "Productor", Imagen = R.drawable.ic_productor_big))
            lista_roles.add(Rol(Nombre = "Comprador", Imagen = R.drawable.ic_comprador_big))
            return lista_roles
        }*/

        /*
        fun listaRoles(): ArrayList<Rol> {
            val lista_roles = ArrayList<Rol>()
            lista_roles.add(Rol(Id = 1, Nombre = "Productor", Imagen = R.drawable.ic_productor_big))
            lista_roles.add(Rol(Id = 2, Nombre = "Comprador", Imagen = R.drawable.ic_comprador_big))
            return lista_roles
        } */

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

        fun listaUP(): ArrayList<UnidadProductiva> {
            val listaUP = ArrayList<UnidadProductiva>()
            listaUP.add(UnidadProductiva(1,
                    "Unidad Productiva 1",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null
            ))
            listaUP.add(UnidadProductiva(2,"Unidad Productiva 2",null,null,null, null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null))
            listaUP.add(UnidadProductiva(3,"Unidad Productiva 3",null,null,null, null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null))
            listaUP.add(UnidadProductiva(4,"Unidad Productiva 4",null,null,null, null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null))
            listaUP.add(UnidadProductiva(5,"Unidad Productiva 5",null,null,null, null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null))
            listaUP.add(UnidadProductiva(6,"Unidad Productiva 6",null,null,null, null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null))

            return listaUP
        }


        fun listaUnidadMedida(): ArrayList<Unidad_Medida> {
            val listaUnidadMedida = ArrayList<Unidad_Medida>()
            listaUnidadMedida.add(Unidad_Medida(1,
                    "Hectarea",
                    "Hect"

            ))
            listaUnidadMedida.add(Unidad_Medida(2,
                    "Metros",
                    "Mts"))


            return listaUnidadMedida
        }


        fun queryGeneral(criterio: String, valor: String): String {
            val queryFilter = "$criterio eq '$valor'"
            return queryFilter
        }
    }
}
