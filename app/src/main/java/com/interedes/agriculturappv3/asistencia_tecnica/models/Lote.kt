package com.interedes.agriculturappv3.asistencia_tecnica.models

/**
 * Created by EnuarMunoz on 7/03/18.
 */


data class Lote (var Nombre: String?,
                 var Descripcion: String?,
                 val Area: Double?,
                 val Coordenadas:String,
                 val Unidad_Medida_Id:Long,
                 val Unidad_Productiva_Id:Long) {
}