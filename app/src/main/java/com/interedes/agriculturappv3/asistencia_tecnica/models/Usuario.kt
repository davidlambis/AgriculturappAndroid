package com.interedes.agriculturappv3.asistencia_tecnica.models

data class Usuario(val rol: String?, val nombres: String, val apellidos: String, val cedula: String, val correo: String, val contrasena: String, val confirmar_contrasena: String, val celular: String, val metodo_pago: String, val banco: String, val numero_cuenta: String) {
}