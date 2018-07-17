package com.interedes.agriculturappv3.modules.comprador.productores.events

import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.Departamento

data class EventDepartamentCities( var departamentos: List<Departamento>? = null, var cities: List<Ciudad>? = null) {
}