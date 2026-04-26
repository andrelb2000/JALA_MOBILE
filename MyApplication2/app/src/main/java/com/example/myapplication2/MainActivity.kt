package com.example.myapplication2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import android.widget.TextView

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
    }
    fun contar (view: View) {
        val editTextView = findViewById<TextView>(R.id.ID1_editTextText)
        val textView2 = findViewById<TextView>(R.id.ID1_textView2)

        val countStringInput = editTextView.text.toString()
        var countInput = countStringInput.toInt()

        val countStringAtual = textView2.text.toString()
        var countAtual = countInput + countStringAtual.toInt()

        textView2.text = countAtual.toString()
    }


}

