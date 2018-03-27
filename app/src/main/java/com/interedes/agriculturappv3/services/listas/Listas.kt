package com.interedes.agriculturappv3.services.listas

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.asistencia_tecnica.models.*
import com.interedes.agriculturappv3.asistencia_tecnica.models.insumos.Insumo
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.Enfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.FotoEnfermedad
import com.interedes.agriculturappv3.asistencia_tecnica.models.plagas.TipoEnfermedad
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
            lista_asistencia_tecnica_productor.add(ItemLista("Produccion", Imagen = R.drawable.ic_produccion_cultivo, Identificador = "produccion"))
            lista_asistencia_tecnica_productor.add(ItemLista("Plagas", Imagen = R.drawable.ic_plagas, Identificador = "plagas"))
            lista_asistencia_tecnica_productor.add(ItemLista("Control Plagas", Imagen = R.drawable.ic_plagas, Identificador = "control_plagas"))
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
            listaUP.add(UnidadProductiva(2, "Unidad Productiva 2", null, null, null, null,
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
            listaUP.add(UnidadProductiva(3, "Unidad Productiva 3", null, null, null, null,
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
            listaUP.add(UnidadProductiva(4, "Unidad Productiva 4", null, null, null, null,
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
            listaUP.add(UnidadProductiva(5, "Unidad Productiva 5", null, null, null, null,
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
            listaUP.add(UnidadProductiva(6, "Unidad Productiva 6", null, null, null, null,
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

            listaUnidadMedida.add(Unidad_Medida(2,
                    "Metros",
                    "Mts"))
            listaUnidadMedida.add(Unidad_Medida(2,
                    "Kilos",
                    "Kl"))

            listaUnidadMedida.add(Unidad_Medida(2,
                    "Cargas",
                    "Cargas"))


            return listaUnidadMedida
        }


        fun listaTipoProducto(): ArrayList<TipoProducto> {
            val lista_tipo_producto = ArrayList<TipoProducto>()
            lista_tipo_producto.add(TipoProducto(Id = 1, Nombre = "Aguacate"))
            lista_tipo_producto.add(TipoProducto(Id = 2, Nombre = "Fríjol"))
            lista_tipo_producto.add(TipoProducto(Id = 3, Nombre = "Plátano"))
            lista_tipo_producto.add(TipoProducto(Id = 4, Nombre = "Aguacate"))
            lista_tipo_producto.add(TipoProducto(Id = 5, Nombre = "Fríjol"))
            lista_tipo_producto.add(TipoProducto(Id = 6, Nombre = "Plátano"))
            return lista_tipo_producto
        }

        fun listaDetalleTipoProducto(): ArrayList<DetalleTipoProducto> {
            val lista_detalle_tipo_producto = ArrayList<DetalleTipoProducto>()
            lista_detalle_tipo_producto.add(DetalleTipoProducto(Id = 1, Descripcion = "Aguacate Hass", Nombre = "Aguacate hass", TipoProductoId = 1))
            lista_detalle_tipo_producto.add(DetalleTipoProducto(Id = 2, Descripcion = "Aguacate Hoss", Nombre = "Aguacate Hoss", TipoProductoId = 1))
            lista_detalle_tipo_producto.add(DetalleTipoProducto(Id = 3, Descripcion = "Fríjol Pinto", Nombre = "Fríjol pinto", TipoProductoId = 2))
            lista_detalle_tipo_producto.add(DetalleTipoProducto(Id = 4, Descripcion = "Plátano Verde", Nombre = "Plátano verde", TipoProductoId = 3))
            return lista_detalle_tipo_producto
        }

        fun listaTipoEnfermedad(): ArrayList<TipoEnfermedad> {
            val lista_tipo_enfermedad = ArrayList<TipoEnfermedad>()
            lista_tipo_enfermedad.add(TipoEnfermedad(Id = 1, Descripcion = "Se observan heridas redondas o irregulares de color café claro, de apariencia arrugada, gruesa, que pueden                unirse y afectar gran parte del fruto. Con el quiebre de las heridas, se favorece la llegada de otros organismos, afectando su valor comercial. Se advierten heridas en hojas             y ramas pequeñas; en casos severos lucen deformadas y con poco crecimiento. Así mismo, se pueden observar manchas gruesas de color oscuro y variadas formas que luego al                   unirse a las venas y tallos de las hojas y corteza de las ramas.", Nombre = "Roña", NombreCientifico = "Sphaceloma perseae Jenkins", TipoProductoId = 1, NombreTipoProducto = "Aguacate", Imagen = R.drawable.aguacate))
            lista_tipo_enfermedad.add(TipoEnfermedad(Id = 2, Descripcion = "Las principales fuentes de infección son rastrojos de plantas o residuos y plantas  infectadas. La enfermedad             es propaga por equipos contaminados y vestimenta, rocíos de agua y lluvia con viento. La enfermedad forma lesiones circulares que usualmente aparecen como manchas de color               café con un centro café claro o plateado. Las manchas inicialmente se encuentran localizadas al tejido entre las venas mayores, dándole una apariencia angular. Las lesiones              sobre los tallos se muestran alargadas y de color café oscuro. Las lesiones sobre las vainas son de hundidos irregulares a manchas negras circulares con centros de color café            rojizo y puede ser similar a los que causan antracnosis. Vainas infectadas pueden contener semillas pobremente desarrolladas, marchitas o decoloradas. (FAO)",
                    Nombre = "Mancha Angular", NombreCientifico = "Phaseoriopsis griseola", TipoProductoId = 2, NombreTipoProducto = "Fríjol", Imagen = R.drawable.frijol))
            lista_tipo_enfermedad.add(TipoEnfermedad(Id = 3, Descripcion = "Todos los órganos de la planta, desde las raíces hasta el escapo floral, pueden ser infectados y presentan              síntomas internos y externos. Los síntomas varían según la edad de la planta, medio de transmisión y órgano afectado. Se presentan marchitamientos y amarilleamiento de plantas,           las hojas se secan y se quiebran, pero sin desprenderse de la planta. Los hijos o rebrotes de plantas enfermas pueden quedar pequeños, retorcerse y ponerse negros. Se presenta          un secamiento de los bordes de las hojas, seguido de una franja de color amarillo intenso. Se presentan racimos y dedos deformes, alguna fruta se madura antes de tiempo, además            los dedos se rajan cuando el racimo está muy desarrollado. La bellota se seca, luego se seca el vástago hasta secarse todo el racimo. (ICA)",
                    Nombre = "Moko o Madurabiche", NombreCientifico = "Ralstonia solanacearum E. F.", TipoProductoId = 3, NombreTipoProducto = "Plátano", Imagen = R.drawable.platano))
            return lista_tipo_enfermedad
        }

        fun listaEnfermedad(): ArrayList<Enfermedad> {
            val lista_enfermedad = ArrayList<Enfermedad>()
            lista_enfermedad.add(Enfermedad(Id = 1, Codigo = "enf1ag", TipoEnfermedadId = 1))
            lista_enfermedad.add(Enfermedad(Id = 2, Codigo = "enf1fr", TipoEnfermedadId = 2))
            lista_enfermedad.add(Enfermedad(Id = 3, Codigo = "enf1Pl", TipoEnfermedadId = 3))
            return lista_enfermedad
        }

        fun listaFotosEnfermedad(): ArrayList<FotoEnfermedad> {
            val lista_fotos_enfermedad = ArrayList<FotoEnfermedad>()
            lista_fotos_enfermedad.add(FotoEnfermedad(Id = 1, Codigo = "fotenf1ag", FechaCreacion = "03/22/2018", Hora = "17:32", Ruta = "", Titulo = "Foto Roña Aguacate", EnfermedadesId = 1))
            lista_fotos_enfermedad.add(FotoEnfermedad(Id = 2, Codigo = "fotenf1fr", FechaCreacion = "03/22/2018", Hora = "17:36", Ruta = "", Titulo = "Foto mancha angular Fríjol", EnfermedadesId = 2))
            lista_fotos_enfermedad.add(FotoEnfermedad(Id = 3, Codigo = "fotenf1pl", FechaCreacion = "03/22/2018", Hora = "17:37", Ruta = "", Titulo = "Foto moko Plátano", EnfermedadesId = 3))
            return lista_fotos_enfermedad
        }

        fun listaInsumos(): ArrayList<Insumo> {
            val lista_insumos = ArrayList<Insumo>()
            lista_insumos.add(Insumo(Id = 1, Descripcion = "Polvo soluble. 10 a 20 cc/Bomba de 20 L. Hidróxido de Cobre 53,8 %", Nombre = "Kocide", EnfermedadId = 1, Imagen = R.drawable.kocide))
            return lista_insumos
        }

        fun queryGeneral(criterio: String, valor: String): String {
            val queryFilter = "$criterio eq '$valor'"
            return queryFilter
        }
    }
}
