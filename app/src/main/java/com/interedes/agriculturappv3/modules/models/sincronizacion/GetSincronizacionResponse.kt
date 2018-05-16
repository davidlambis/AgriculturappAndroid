package com.interedes.agriculturappv3.modules.models.sincronizacion

import com.google.gson.annotations.SerializedName
import com.interedes.agriculturappv3.modules.models.unidad_productiva.PostUnidadProductiva
import com.interedes.agriculturappv3.modules.models.unidad_productiva.Unidad_Productiva

 class GetSincronizacionResponse{
         @SerializedName("value")
         var value: MutableList<Unidad_Productiva>? = null
}