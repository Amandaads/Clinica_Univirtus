package com.example.clinica_univirtus.models

data class AgendamentoDto (
    val data: String = "",
    val hora: String = "",
    val especialidade: String = "",
    val medico: String = "",
    val concluido: Boolean = false
)