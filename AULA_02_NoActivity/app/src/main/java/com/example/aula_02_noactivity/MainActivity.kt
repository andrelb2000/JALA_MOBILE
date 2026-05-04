package com.example.aula_02_noactivity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val editTextNome = findViewById<EditText>(R.id.ID1_Nome_editTextText)
        val editTextIdade = findViewById<EditText>(R.id.ID1_IDADEeditTextNumber)
        val buttonChecaIdade = findViewById<Button>(R.id.ID1_CHECA_IDADEbutton)
        buttonChecaIdade.setOnClickListener {
            val idade = editTextIdade.text.toString().toIntOrNull()
            if (idade != null) {
                if (idade >= 18) {
                    editTextNome.setText("Olá, você é maior de idade!")
                } else {
                    Toast.makeText(this, "Menor de idade", Toast.LENGTH_SHORT).show()
                    val Intent = Intent(this, MainActivity2::class.java)
                    Intent.putExtra("idade", idade)
                    startActivity(Intent)
                }
            }

        }




    }
}