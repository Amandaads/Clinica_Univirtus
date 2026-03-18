package com.example.clinica_univirtus.models

data class Paciente(
    val nome: String = "",
    val sobrenome: String = "",
    val dataNascimento: String = "",
    val telefone: String = "",
    val email: String = "",
    val cpf: String = "",
    val rua: String = "",
    val numero: String = "",
    val cidade: String = "",
    val bairro: String = "",
    val uf: String = "",
    val cep: String = ""
)
