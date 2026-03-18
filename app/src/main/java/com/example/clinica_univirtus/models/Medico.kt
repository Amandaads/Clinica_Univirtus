package com.example.clinica_univirtus.models

data class Medico(
    val uid: String,
    val nome: String
) {
    override fun toString(): String {
        return nome
    }
}