package com.interedes.agriculturappv3.asistencia_tecnica.activities.registration.register_productor_comprador

interface RegisterProductorCompradorView {
    fun loadInfo()
    fun limpiarCambios()
    fun navigateToParentActivity()
    fun registerProductor()
    fun disableInputs()
    fun enableInputs()
}