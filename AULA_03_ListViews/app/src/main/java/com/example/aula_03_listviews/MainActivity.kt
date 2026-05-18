package com.example.aula_03_listviews

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.ID1_editTextText)
        val button = findViewById<Button>(R.id.ID1_button)
        val listView = findViewById<ListView>(R.id.ID1_LIST_VIEW)

        // 1. Criar a lista de dados
        val listaNomes = mutableListOf<String>()

        // 2. Criar e configurar o Adapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaNomes)
        listView.adapter = adapter

        // 3. Lógica do botão
        button.setOnClickListener {
            val texto = editText.text.toString().trim()
            
            if (texto.isNotEmpty()) {
                listaNomes.add(texto)
                adapter.notifyDataSetChanged() // Atualiza a tela
                editText.text.clear()          // Limpa o campo
            } else {
                Toast.makeText(this, "Digite algo primeiro!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
