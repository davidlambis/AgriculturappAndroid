package com.interedes.agriculturappv3.modules.comprador.productores.events

import com.interedes.agriculturappv3.modules.models.departments.Ciudad
import com.interedes.agriculturappv3.modules.models.departments.Departamento

data class EventDepartamentCities( var departamentos: MutableList<Departamento>? = null, var cities: MutableList<Ciudad>? = null) {
}