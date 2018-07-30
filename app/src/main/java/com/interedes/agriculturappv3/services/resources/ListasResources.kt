package com.interedes.agriculturappv3.services.resources

import com.interedes.agriculturappv3.R
import com.interedes.agriculturappv3.modules.models.*
import com.interedes.agriculturappv3.modules.models.compras.Compras
import com.interedes.agriculturappv3.modules.models.compras.DetalleCompra
import com.interedes.agriculturappv3.modules.models.detalletipoproducto.DetalleTipoProducto
import com.interedes.agriculturappv3.modules.models.insumos.Insumo
import com.interedes.agriculturappv3.modules.models.ofertas.Oferta
import com.interedes.agriculturappv3.modules.models.plagas.Enfermedad
import com.interedes.agriculturappv3.modules.models.plagas.FotoEnfermedad
import com.interedes.agriculturappv3.modules.models.plagas.TipoEnfermedad
import com.interedes.agriculturappv3.modules.models.producto.CalidadProducto
import com.interedes.agriculturappv3.modules.models.producto.RangePrice
import com.interedes.agriculturappv3.modules.models.tipoproducto.TipoProducto
import com.interedes.agriculturappv3.modules.models.tratamiento.Tratamiento
import com.interedes.agriculturappv3.modules.models.unidad_medida.Unidad_Medida
import com.interedes.agriculturappv3.modules.models.usuario.Usuario
import com.interedes.agriculturappv3.modules.models.ventas.CategoriaPuk
import com.interedes.agriculturappv3.modules.models.ventas.Estado_Transaccion
import com.interedes.agriculturappv3.modules.models.ventas.Puk
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class ListasResources {

    companion object {

        /*
        fun listaRoles(): ArrayList<Rol> {
            val lista_roles = ArrayList<Rol>()
            lista_roles.add(Rol(nombre = "Productor", Imagen = R.drawable.ic_productor_big))
            lista_roles.add(Rol(nombre = "Comprador", Imagen = R.drawable.ic_comprador_big))
            return lista_roles
        }*/

        /*
        fun listaRoles(): ArrayList<Rol> {
            val lista_roles = ArrayList<Rol>()
            lista_roles.add(Rol(Id = 1, nombre = "Productor", Imagen = R.drawable.ic_productor_big))
            lista_roles.add(Rol(Id = 2, nombre = "Comprador", Imagen = R.drawable.ic_comprador_big))
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

        fun listaMenuComprador(): ArrayList<ItemLista> {
            val lista_menu_comprador = ArrayList<ItemLista>()
            //lista_menu_comprador.add(ItemLista("Asistencia Técnica", Imagen = R.drawable.ic_asistencia_tecnica_color_500, Identificador = "asistencia_tecnica"))
            lista_menu_comprador.add(ItemLista("Comercial", Imagen = R.drawable.ic_comercial_color_500, Identificador = "comercial"))
            //lista_menu_comprador.add(ItemLista("Contabilidad", Imagen = R.drawable.ic_contabilidad_color_500, Identificador = "contabilidad"))
            lista_menu_comprador.add(ItemLista("Salir", Imagen = R.drawable.ic_salir_500, Identificador = "salir"))
            return lista_menu_comprador
        }

        fun listaAsistenciaTecnicaProductor(): ArrayList<ItemLista> {
            val lista_asistencia_tecnica_productor = ArrayList<ItemLista>()
            lista_asistencia_tecnica_productor.add(ItemLista("Mis Unidades Productivas", Imagen = R.drawable.ic_unidad_productiva, Identificador = "mis_unidades_productivas"))
            lista_asistencia_tecnica_productor.add(ItemLista("Mis Lotes", Imagen = R.drawable.ic_lote, Identificador = "mis_lotes"))
            lista_asistencia_tecnica_productor.add(ItemLista("Mis Cultivos", Imagen = R.drawable.ic_cultivos, Identificador = "mis_cultivos"))
            lista_asistencia_tecnica_productor.add(ItemLista("Producción", Imagen = R.drawable.ic_produccion, Identificador = "produccion"))
            lista_asistencia_tecnica_productor.add(ItemLista("Plagas", Imagen = R.drawable.ic_plagas, Identificador = "plagas"))
            lista_asistencia_tecnica_productor.add(ItemLista("Control Plagas", Imagen = R.drawable.ic_plagas_erradicada, Identificador = "control_plagas"))
            return lista_asistencia_tecnica_productor
        }


        fun listaModuloComercialProductor(): ArrayList<ItemLista> {
            val lista_comercial_productor = ArrayList<ItemLista>()
            lista_comercial_productor.add(ItemLista("Mis Productos", Imagen = R.drawable.ic_productos_blue, Identificador = "mis_productos"))
            lista_comercial_productor.add(ItemLista("Ofertas", Imagen = R.drawable.ic_ofertas, Identificador = "ofertas"))
            //lista_comercial_productor.add(ItemLista("Ventas Realizadas", Imagen = R.drawable.ic_ventas, Identificador = "ventas_realizadas"))
           // lista_comercial_productor.add(ItemLista("Clientes", Imagen = R.drawable.ic_clientes_azul, Identificador = "clientes"))
           // lista_comercial_productor.add(ItemLista("Preguntas", Imagen = R.drawable.ic_preguntas_frecuentas_azul, Identificador = "preguntas"))
            return lista_comercial_productor
        }

        fun listaModuloComercialComprador(): ArrayList<ItemLista> {
            val lista_comercial_comprador = ArrayList<ItemLista>()
            lista_comercial_comprador.add(ItemLista("Productos", Imagen = R.drawable.ic_productos_blue, Identificador = "productos"))
            return lista_comercial_comprador
        }

        fun listaModuloContableProductor(): ArrayList<ItemLista> {
            val lista_comercial_productor = ArrayList<ItemLista>()
            lista_comercial_productor.add(ItemLista("Ventas", Imagen = R.drawable.ic_ventas_contables, Identificador = "ventas"))
            lista_comercial_productor.add(ItemLista("Gastos", Imagen = R.drawable.ic_gasto_contable2, Identificador = "gastos"))
            lista_comercial_productor.add(ItemLista("Reportes", Imagen = R.drawable.ic_reporte_contable, Identificador = "reportes"))
            return lista_comercial_productor
        }


        fun listaRangePrice(): ArrayList<RangePrice> {
            val listRange = ArrayList<RangePrice>()
            listRange.add(RangePrice(1000.0))
            listRange.add(RangePrice(10000.0))
            listRange.add(RangePrice(100000.0))
            listRange.add(RangePrice(1000000.0))
            listRange.add(RangePrice(10000000.0))
            listRange.add(RangePrice(100000000.0))
            return listRange
        }

        /*fun listaUP(): ArrayList<Unidad_Productiva> {
            val listaUP = ArrayList<Unidad_Productiva>()
            listaUP.add(Unidad_Productiva(1,
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
            listaUP.add(Unidad_Productiva(2, "Unidad Productiva 2", null, null, null, null,
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
            listaUP.add(Unidad_Productiva(3, "Unidad Productiva 3", null, null, null, null,
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
            listaUP.add(Unidad_Productiva(4, "Unidad Productiva 4", null, null, null, null,
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
            listaUP.add(Unidad_Productiva(5, "Unidad Productiva 5", null, null, null, null,
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
            listaUP.add(Unidad_Productiva(6, "Unidad Productiva 6", null, null, null, null,
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
        }*/


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
            lista_tipo_enfermedad.add(TipoEnfermedad(Id = 1, Descripcion = "Se observan heridas redondas o irregulares de color café claro, de apariencia arrugada, gruesa, que pueden unirse y afectar gran parte del fruto. Con el quiebre de las heridas, se favorece la llegada de otros organismos, afectando su valor comercial. Se advierten heridas en hojas y ramas pequeñas; en casos severos lucen deformadas y con poco crecimiento. Así mismo, se pueden observar manchas gruesas de color oscuro y variadas formas que luego al unirse a las venas y tallos de las hojas y corteza de las ramas.", Nombre = "Roña", NombreCientifico = "Sphaceloma perseae Jenkins", TipoProductoId = 1, NombreTipoProducto = "Aguacate", Imagen = R.drawable.aguacate))
            lista_tipo_enfermedad.add(TipoEnfermedad(Id = 2, Descripcion = "Las principales fuentes de infección son rastrojos de plantas o residuos y plantas  infectadas. La enfermedad   es propaga por equipos contaminados y vestimenta, rocíos de agua y lluvia con viento. La enfermedad forma lesiones circulares que usualmente aparecen como manchas de color café con un centro café claro o plateado. Las manchas inicialmente se encuentran localizadas al tejido entre las venas mayores, dándole una apariencia angular. Las lesiones sobre los tallos se muestran alargadas y de color café oscuro. Las lesiones sobre las vainas son de hundidos irregulares a manchas negras circulares con centros de color café rojizo y puede ser similar a los que causan antracnosis. Vainas infectadas pueden contener semillas pobremente desarrolladas, marchitas o decoloradas.(FAO)",
                    Nombre = "Mancha Angular", NombreCientifico = "Phaseoriopsis griseola", TipoProductoId = 2, NombreTipoProducto = "Fríjol", Imagen = R.drawable.frijol))
            lista_tipo_enfermedad.add(TipoEnfermedad(Id = 3, Descripcion = "Todos los órganos de la planta, desde las raíces hasta el escapo floral, pueden ser infectados y presentan     síntomas internos y externos. Los síntomas varían según la edad de la planta, medio de transmisión y órgano afectado. Se presentan marchitamientos y amarilleamiento de plantas, las hojas se secan y se quiebran, pero sin desprenderse de la planta. Los hijos o rebrotes de plantas enfermas pueden quedar pequeños, retorcerse y ponerse negros. Se presenta un secamiento de los bordes de las hojas, seguido de una franja de color amarillo intenso. Se presentan racimos y dedos deformes, alguna fruta se madura antes de tiempo, además los dedos se rajan cuando el racimo está muy desarrollado. La bellota se seca, luego se seca el vástago hasta secarse todo el racimo. (ICA)",
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
            lista_fotos_enfermedad.add(FotoEnfermedad(Id = 1, Descripcion = "fotenf1ag", FechaCreacion = "03/22/2018", Hora = "17:32", Ruta = "", Titulo = "Foto Roña Aguacate", EnfermedadesId = 1))
            lista_fotos_enfermedad.add(FotoEnfermedad(Id = 2, Descripcion = "fotenf1fr", FechaCreacion = "03/22/2018", Hora = "17:36", Ruta = "", Titulo = "Foto mancha angular Fríjol", EnfermedadesId = 2))
            lista_fotos_enfermedad.add(FotoEnfermedad(Id = 3, Descripcion = "fotenf1pl", FechaCreacion = "03/22/2018", Hora = "17:37", Ruta = "", Titulo = "Foto moko Plátano", EnfermedadesId = 3))
            return lista_fotos_enfermedad
        }



        fun listaInsumos(): ArrayList<Insumo> {
            val lista_insumos = ArrayList<Insumo>()
            lista_insumos.add(Insumo(Id = 1,
                    Descripcion = "Insumo para Roña(Aguacate)",
                    Nombre = "Kocide",
                    EnfermedadId = 1,
                    Foto = R.drawable.kocide))
            return lista_insumos
        }

        fun listaTratamientos(): ArrayList<Tratamiento> {
            val lista_tratamientos = ArrayList<Tratamiento>()
            lista_tratamientos.add(Tratamiento(Id = 1, Desc_Aplicacion = "10 a 20 cc/Bomba de 20 L", Desc_Formulacion = "Polvo soluble", IngredienteActivo = "Hidróxido de Cobre 53,8 %", InsumoId = 1, Modo_Accion = "", Nombre_Comercial = "KOCIDE", proveedor = ""))
            return lista_tratamientos
        }

        fun listaCalidadProducto(): ArrayList<CalidadProducto> {
            val lista_calidades_producto = ArrayList<CalidadProducto>()
            lista_calidades_producto.add(CalidadProducto(Id = 1, Descripcion = "Calidad Primera", Nombre = "Primera"))
            lista_calidades_producto.add(CalidadProducto(Id = 2, Descripcion = "Calidad Segunda", Nombre = "Segunda"))
            lista_calidades_producto.add(CalidadProducto(Id = 3, Descripcion = "Calidad Tercera", Nombre = "Tercera"))
            return lista_calidades_producto
        }

        fun listCategoriaPuk(): ArrayList<CategoriaPuk> {
            val categoriaPuk = ArrayList<CategoriaPuk>()
            categoriaPuk.add(CategoriaPuk(Id = 1, Nombre = "Gasto", Sigla = "I"))
            categoriaPuk.add(CategoriaPuk(Id = 2, Nombre = "Ingreso", Sigla = "G"))
            return categoriaPuk
        }

        fun listPuk(): ArrayList<Puk> {
            val categoriaPuk = ArrayList<Puk>()
            categoriaPuk.add(Puk(Id = 1, CategoriaId = 1, Codigo = "", Descripcion = "Analisis de suelo"))
            categoriaPuk.add(Puk(Id = 2, CategoriaId = 1, Codigo = "", Descripcion = "Material Vegetal"))
            categoriaPuk.add(Puk(Id = 3, CategoriaId = 1, Codigo = "", Descripcion = "Materiales"))
            categoriaPuk.add(Puk(Id = 4, CategoriaId = 1, Codigo = "", Descripcion = "Herramientas"))
            categoriaPuk.add(Puk(Id = 5, CategoriaId = 1, Codigo = "", Descripcion = "Mano de obra preparaciond de terreno"))
            categoriaPuk.add(Puk(Id = 6, CategoriaId = 1, Codigo = "", Descripcion = "Mano de obra cosecha y poscosecha"))
            categoriaPuk.add(Puk(Id = 7, CategoriaId = 1, Codigo = "", Descripcion = "Agroinsumos"))
            categoriaPuk.add(Puk(Id = 8, CategoriaId = 1, Codigo = "", Descripcion = "Transporte"))

            categoriaPuk.add(Puk(Id = 9, CategoriaId = 2, Codigo = "", Descripcion = "Cosecha"))
            categoriaPuk.add(Puk(Id = 10, CategoriaId = 2, Codigo = "", Descripcion = "Cultivo de Cafe"))
            return categoriaPuk
        }

        fun listEstadoTransaccion(): ArrayList<Estado_Transaccion> {
            val estadoTransaccion = ArrayList<Estado_Transaccion>()
            estadoTransaccion.add(Estado_Transaccion(Id = 1, Nombre = "Por Cobrar"))
            estadoTransaccion.add(Estado_Transaccion(Id = 2, Nombre = "Por Pagar"))
            estadoTransaccion.add(Estado_Transaccion(Id = 3, Nombre = "Pagado"))
            return estadoTransaccion
        }


        var listCategoriasPuk = listCategoriaPuk()
        var listPuk = listPuk()

        fun listStaticOfertas(): ArrayList<Oferta> {
            val list_static_ofertas = ArrayList<Oferta>()
            //list_static_ofertas.add(Oferta(Id = 1, CreatedOn = Calendar.getInstance().time, EstadoOferta = 1, EstadoOfertaId = 1, UsuarioId = 1, ProductoId = 1, NombreUsuario = "Anacleto", Valor_Oferta = 50.15))
            return list_static_ofertas
        }

        fun listVentasProductor(): ArrayList<Compras> {
            val list_static_ventas = ArrayList<Compras>()
            list_static_ventas.add(Compras(Id = 1, ProductoId = 1, NombreProducto = "Producto1", NombreUsuario = "Serafín", CodigoCupon = "", CreatedOn = Calendar.getInstance().time, MetodoPago = "Efectivo", TotalCompra = 50000.0, UsuarioId = 2, UsuarioVendedorId = 1))
            return list_static_ventas
        }

        fun listaDetalleVentasProductor(): ArrayList<DetalleCompra> {
            val list_static_detalle_ventas = ArrayList<DetalleCompra>()
            list_static_detalle_ventas.add(DetalleCompra(Id = 1, ComprasId = 1, ProductoId = 1))
            return list_static_detalle_ventas
        }

        fun listUsuarios(): ArrayList<Usuario> {
            val list_static_usuarios = ArrayList<Usuario>()
            list_static_usuarios.add(Usuario(Id = UUID.randomUUID(), Apellidos = "Meza", Email = "anacleto@mail.com", Identificacion = "108908756", Nombre = "Cleto", PhoneNumber = "3145678512"))
            return list_static_usuarios
        }

        fun listUsuariosLogin(): ArrayList<Usuario> {
            val list_usuarios_login = ArrayList<Usuario>()
            list_usuarios_login.add(Usuario(Id = UUID.randomUUID(), Apellidos = "Gonzalez", Email = "productor", Identificacion = "1054654660", Nombre = "Pedro", Contrasena = "productor", DetalleMetodoPagoNombre = "Efectivo", RolNombre = "Productor"))
            list_usuarios_login.add(Usuario(Id = UUID.randomUUID(), Apellidos = "Gutierrez", Email = "comprador", Identificacion = "65165278", Nombre = "Carlos", Contrasena = "comprador", RolNombre = "Comprador"))
            return list_usuarios_login
        }

        fun queryGeneral(criterio: String, valor: String): String {
            val queryFilter = "$criterio eq '$valor'"
            return queryFilter
        }



        fun queryGeneralWithContains(model:String,criterio: String, valor: String): String {
            val queryFilter = "contains($model/$criterio,  '$valor')"
            return queryFilter
        }

        fun queryOrderByDesc(criterio: String): String {
            val queryFilter = "$criterio desc"
            return queryFilter
        }



        fun queryGeneralLong(criterio: String, valor: Long): String {
            val queryFilter = "$criterio eq $valor"
            return queryFilter
        }


        fun queryFilterProductsRangeAndCriterioDepartamentoAll(criterio: String, valor: Long, criterio2:String,mayor:BigDecimal,criterio3:String, menor:BigDecimal): String {
            val queryFilter = "$criterio eq $valor and $criterio2 gt $mayor and $criterio3 lt $menor"
            ///http://18.233.87.16/odata/Agp2/ViewProductos?&$filter=tipo_producto_id eq 1 and precio_producto gt 0 and precio_producto lt 10000000&$top=20&$skip=0&$count=true
            return queryFilter
        }

        fun queryFilterProductsRangeAndCriterioCiudad(criterio: String, valor: Long, criterio2:String,valor2:Long, criterio3:String,mayor:BigDecimal,criterio4:String, menor:BigDecimal ): String {
            val queryFilter = "$criterio eq $valor and $criterio2 eq $valor2 and $criterio3 gt $mayor and $criterio4 lt $menor"
            ///http://18.233.87.16/odata/Agp2/ViewProductos?&$filter=tipo_producto_id eq 1 and precio_producto gt 0 and precio_producto lt 10000000&$top=20&$skip=0&$count=true
            return queryFilter
        }


        fun queryFilterProductsRangeAndCriterioDepartamentoAllContains(criterio: String, valor: Long,criterio2:String, valor2:String, criterio3:String,mayor:BigDecimal,criterio4:String, menor:BigDecimal): String {
            val queryFilter = "$criterio eq $valor and $criterio3 gt $mayor and $criterio4 lt $menor and contains($criterio2,'$valor2')"
            ///http://18.233.87.16/odata/Agp2/ViewProductos?&$filter=tipo_producto_id eq 4 and precio_producto gt 0 and precio_producto lt 10000000 and contains(nombre_producto,'prue')&$top=20&$skip=0&$count=true
            return queryFilter
        }

        fun queryFilterProductsRangeAndCriterioCiudadContains(criterio: String, valor: Long, criterio2:String,valor2:Long,criterio3:String, valor3:String, criterio4:String,mayor:BigDecimal,criterio5:String, menor:BigDecimal): String {
            val queryFilter = "$criterio eq $valor and $criterio2 eq $valor2 and contains($criterio3,'$valor3') and $criterio4 gt $mayor and $criterio5 lt $menor"
            ///http://18.233.87.16/odata/Agp2/ViewProductos?&$filter=tipo_producto_id eq 4 and precio_producto gt 0 and precio_producto lt 10000000 and contains(nombre_producto,'prue')&$top=20&$skip=0&$count=true
            return queryFilter
        }


        fun queryGeneralMultipleLongAndString(criterio1: String, valor1: Long?,criterio2: String, valor2: String): String {
            val queryFilter = "$criterio1 eq $valor1 and $criterio2 eq '$valor2'"
            return queryFilter
        }
    }
}
