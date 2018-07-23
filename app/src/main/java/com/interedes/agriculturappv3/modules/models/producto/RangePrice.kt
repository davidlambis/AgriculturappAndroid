package com.interedes.agriculturappv3.modules.models.producto

class RangePrice(
                   var Valor_Producto: Double = 0.0) {

    override fun toString(): String {
        return   String.format("$ %,.0f",Valor_Producto)
    }
}