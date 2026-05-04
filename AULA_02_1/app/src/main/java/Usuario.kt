class Usuario(val nome: String, val idade: Int) {
    constructor(nome: String): this(nome, idade=18)
    init {
        println("Pessoa $nome criada")
    }
}