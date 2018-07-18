package com.interedes.agriculturappv3.modules.comprador.productores.resources

import java.math.BigDecimal

data class RequestFilter(var checkConection:Boolean,
                         var tipoProductoId:Long,
                         var ciudadId:Long,
                         var priceMin:BigDecimal,
                         var priceMax:BigDecimal,
                         var isFirst:Boolean,
                         var top:Int,
                         var skip:Int

                         ) {
}