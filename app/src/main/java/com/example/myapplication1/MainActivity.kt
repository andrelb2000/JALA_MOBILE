package com.example.myapplication1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication1.ui.theme.MyApplication1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication1Theme {
                    val contadorGlobal = remember { mutableStateOf(0) }
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Greeting(
                            name = "Android Real"
                        )
                        ContarButton(contadorGlobal)
                        InputNumero(contadorGlobal)
                    }
            }
        }
    }
}
@Composable
fun ContarButton(contador: MutableState<Int>) {
    Button(onClick = {
        contador.value++
    }) {
        Text(text = "contar ${contador.value}")
    }
}
@Composable
fun InputNumero(contador: MutableState<Int>) {
    var texto by remember { mutableStateOf("") }
    TextField(
        value = texto,
        onValueChange = { novoTexto ->
            texto = novoTexto
            novoTexto.toIntOrNull()?.let { novoValor ->
                contador.value = novoValor
            }
        },
        label = { Text("Definir contador") },
        modifier = Modifier.padding(16.dp)
    )
}
@Composable
fun Greeting(name: String) {
    Text(
        text = "Hello $name!"
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplication1Theme {
        val contadorPreview = remember { mutableStateOf(0) }
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Greeting("AndroidPreview")
            ContarButton(contadorPreview)
            InputNumero(contadorPreview)
        }
    }
}


