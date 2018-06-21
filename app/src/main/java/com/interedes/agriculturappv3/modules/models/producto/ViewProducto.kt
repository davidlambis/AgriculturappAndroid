package com.interedes.agriculturappv3.modules.models.producto

import com.google.gson.annotations.SerializedName
import java.util.*

data class ViewProducto(

        @SerializedName("id")
        var id: Long? = null,

        @SerializedName("nombre_producto")
        var nombre_producto: String? = null,

        @SerializedName("descripcion_producto")
        var descripcion_producto: String? = null,

        @SerializedName("fechalimite_disponibilidad")
        var fechalimite_disponibilidad: String? = null,

        @SerializedName("unidadmedida_id_producto")
        var unidadmedida_id_producto: Long? = 0,

        @SerializedName("nombre_unidadmedida_producto")
        var nombre_unidadmedida_producto: String? = null,

        @SerializedName("sigla_unidadmedida_producto")
        var sigla_unidadmedida_producto: String? = null,

        @SerializedName("imagen_producto")
        var imagen_producto: String? = null,

        @SerializedName("is_enabled_producto")
        var is_enabled_producto: Boolean? = false,

        @SerializedName("precio_producto")
        var precio_producto: Double? = 0.0,

        @SerializedName("precio_escpecial_producto")
        var precio_escpecial_producto: Double? = 0.0,

        @SerializedName("stock_producto")
        var stock_producto: Double? = 0.0,

        @SerializedName("precio_unidadmedida_producto")
        var precio_unidadmedida_producto: String? = null,

        @SerializedName("usuarioid_producto")
        var usuarioid_producto: String? = null,

        @SerializedName("calidad_id")
        var calidad_id: Long? = 0,

        @SerializedName("nombre_calidad")
        var nombre_calidad: String? = null,


        @SerializedName("descripcion_calidad")
        var descripcion_calidad: String? = null,


        @SerializedName("cultivoid")
        var cultivoid: Long? = 0,

        @SerializedName("descripcion_cultivo")
        var descripcion_cultivo: String? = null,

        @SerializedName("estimado_cosecha")
        var estimado_cosecha: Double? = 0.0,

        @SerializedName("fechainicio_cultivo")
        var fechainicio_cultivo: String? = null,

        @SerializedName("fechafin_cultivo")
        var fechafin_cultivo: String? = null,


        @SerializedName("nombre_cultivo")
        var nombre_cultivo: String? = null,


        @SerializedName("siembratotal_cultivo")
        var siembratotal_cultivo: Long? = 0,


        @SerializedName("unidadmedida_id_cultivo")
        var unidadmedida_id_cultivo: Long? = 0,

        @SerializedName("descripcion_unidadmedida_cultivo")
        var descripcion_unidadmedida_cultivo: String? = null,

        @SerializedName("detalle_tipo_productoid")
        var detalle_tipo_productoid: Long? = 0,

        @SerializedName("descripcion_detalle_tipoproducto")
        var descripcion_detalle_tipoproducto: String? = null,

        @SerializedName("nombre_detalle_tipoproducto")
        var nombre_detalle_tipoproducto: String? = null,

        @SerializedName("tipo_producto_id")
        var tipo_producto_id: Long? = 0,

        @SerializedName("nombre_tipoproducto")
        var nombre_tipoproducto: String? = null,

        @SerializedName("lote_id")
        var lote_id: Long? = 0,

        @SerializedName("area_lote")
        var area_lote: Double? = 0.0,

        @SerializedName("localizacion_lote")
        var localizacion_lote: String? = null,

        @SerializedName("localizacion_poligono_lote")
        var localizacion_poligono_lote: String? = null,

        @SerializedName("descripcion_lote")
        var descripcion_lote: String? = null,

        @SerializedName("nombre_lote")
        var nombre_lote: String? = null,

        @SerializedName("unidadmedida_id_lote")
        var unidadmedida_id_lote: Long? = 0,

        @SerializedName("descripcion_unidadmedida_lote")
        var descripcion_unidadmedida_lote: String? = null,

        @SerializedName("unidad_productiva_id")
        var unidad_productiva_id: Long? = 0,

        @SerializedName("area_up")
        var area_up: Double? = 0.0,

        @SerializedName("codigo_up")
        var codigo_up: String? = null,

        @SerializedName("descripcion_up")
        var descripcion_up: String? = null,

        @SerializedName("nombre_up")
        var nombre_up: String? = null,

        @SerializedName("unidadmedida_id_up")
        var unidadmedida_id_up: Long? = 0,

        @SerializedName("descripcion_unidadmedida_up")
        var descripcion_unidadmedida_up: String? = null,

        @SerializedName("ciudad_id")
        var ciudad_id: Long? = 0,

        @SerializedName("nombre_ciudad")
        var nombre_ciudad: String? = null,


        @SerializedName("departamento_id")
        var departamento_id: Long? = 0,

        @SerializedName("nombre_departamento")
        var nombre_departamento: String? = null,


        @SerializedName("usuario_id")
        var usuario_id: UUID? = null,

        @SerializedName("apellido_usuario")
        var apellido_usuario: String? = null,

        @SerializedName("email_usuario")
        var email_usuario: String? = null,

        @SerializedName("estado_usuario")
        var estado_usuario: String? = null,

        @SerializedName("fecharegistro_usuario")
        var fecharegistro_usuario: String? = null,

        @SerializedName("fotoperfil_usuario")
        var fotoperfil_usuario: String? = null,

        @SerializedName("identificacion_usuario")
        var identificacion_usuario: String? = null,

        @SerializedName("nombre_usuario")
        var nombre_usuario: String? = null,

        @SerializedName("nro_movil_usuario")
        var nro_movil_usuario: String? = null,

        @SerializedName("numero_cuenta_usuario")
        var numero_cuenta_usuario: String? = null,

        @SerializedName("phone_number_usuario")
        var phone_number_usuario: String? = null,

        @SerializedName("username_usuario")
        var username_usuario: String? = null,

        @SerializedName("detalle_metodopago_id")
        var detalle_metodopago_id: Long? = 0,

        @SerializedName("nombre_detallemetodo_pago")
        var nombre_detallemetodo_pago: String? = null,

        @SerializedName("metodopago_id")
        var metodopago_id: Long? = 0,

        @SerializedName("nombre_metodopago")
        var nombre_metodopago: String? = null
) {
}

