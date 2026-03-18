package com.example.clinica_univirtus.models

data class Agendamento(
    var uid: String = "",
    val data: String = "",
    val hora: String = "",
    val especialidade: String = "",
    val medico: String = "",
    val concluido: Boolean = false
)
