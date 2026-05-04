package com.example.aula_02_noactivity

class Usuario(val nome: String, val idade: Int) {
    constructor(nome: String):this(nome,idade = 18)
    init {
        println("Objeto criado $nome com sucesso")
    }


}