package com.example.aula04_fragments

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity(), InputFragment.OnFragmentInteractionListener {

    private lateinit var tvResult: TextView
    private lateinit var etInput: EditText
    private lateinit var lvResults: ListView
    private val items = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    /****** SQLite CHANGES *****/
    private lateinit var dbHelper: DatabaseHelper
    /****************************/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        /****** SQLite CHANGES *****/
        dbHelper = DatabaseHelper(this)
        /****************************/
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvResult = findViewById(R.id.tvActivityResult)
        etInput = findViewById(R.id.etActivityInput)
        lvResults = findViewById(R.id.lvResults)
        
        val btnLoad = findViewById<Button>(R.id.btnLoadFragment)

        /****** SQLite CHANGES *****/
        // Carrega itens salvos do banco de dados
        items.addAll(dbHelper.getAllWords())
        /****************************/

        // Configuração da ListView Legacy
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        lvResults.adapter = adapter

        btnLoad.setOnClickListener {
            val textToSend = etInput.text.toString()
            val fragment = InputFragment.newInstance(textToSend)
            
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit()
        }
    }

    override fun onDevolverClicked(text: String) {
        tvResult.text = "Último recebido: $text"
        etInput.setText(text)

        /****** SQLite CHANGES *****/
        // Salva no banco de dados
        dbHelper.addWord(text)
        /****************************/

        // Adiciona à lista e atualiza o ListView
        items.add(text)
        adapter.notifyDataSetChanged()
        
        // Remove o fragmento após devolver
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(fragment)
                .commit()
        }
    }
}