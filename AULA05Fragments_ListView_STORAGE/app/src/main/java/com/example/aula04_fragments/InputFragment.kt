package com.example.aula04_fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
/****** RECYCLERVIEW CHANGES *****/
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
/********************************/
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputFragment : Fragment() {

    private var listener: OnFragmentInteractionListener? = null
    private var initialText: String? = null
    private lateinit var dataStoreManager: DataStoreManager
    /****** RECYCLERVIEW CHANGES *****/
    private lateinit var listNamesAdapter: ListNamesAdapter
    /********************************/

    interface OnFragmentInteractionListener {
        fun onDevolverClicked(text: String, listName: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context deve implementar OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            initialText = it.getString(ARG_TEXT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_input, container, false)
        val etListName = view.findViewById<EditText>(R.id.etListName)
        val editText = view.findViewById<EditText>(R.id.etFragmentInput)
        val btnDevolver = view.findViewById<Button>(R.id.btnDevolver)
        val btnEnviarRest = view.findViewById<Button>(R.id.btnEnviarRest)

        /****** RECYCLERVIEW CHANGES *****/
        val rvListNames = view.findViewById<RecyclerView>(R.id.rvListNames)
        listNamesAdapter = ListNamesAdapter(emptyList())
        rvListNames.adapter = listNamesAdapter
        rvListNames.layoutManager = LinearLayoutManager(requireContext())
        /********************************/

        dataStoreManager = DataStoreManager(requireContext())

        /****** RECYCLERVIEW CHANGES *****/
        // Observa as mudanças no DataStore para atualizar o RecyclerView
        viewLifecycleOwner.lifecycleScope.launch {
            dataStoreManager.listNames.collect { names ->
                listNamesAdapter.updateData(names.toList())
            }
        }
        /********************************/

        editText.setText(initialText)

        btnDevolver.setOnClickListener {
            val textToReturn = editText.text.toString()
            val listName = etListName.text.toString()

            // SQLite & DataStore CHANGES
            lifecycleScope.launch {
                dataStoreManager.addListName(listName)
                listener?.onDevolverClicked(textToReturn, listName)
            }
        }

        btnEnviarRest.setOnClickListener {
            val textToPost = editText.text.toString()
            enviarDadosRest(textToPost)
        }

        return view
    }

    /**
     * Exemplo didático de arquitetura usando Coroutines para chamada REST (Simulada).
     */
    private fun enviarDadosRest(texto: String) {
        // lifecycleScope garante que a coroutine morre se o fragmento for destruído
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Simulação de início de processo
                Toast.makeText(requireContext(), "Iniciando envio REST...", Toast.LENGTH_SHORT).show()

                // withContext(Dispatchers.IO) move a execução para uma thread de rede/disco
                val resultado = withContext(Dispatchers.IO) {
                    simularChamadaRede(texto)
                }

                // De volta à Main Thread, exibimos o resultado
                Toast.makeText(requireContext(), "Sucesso: $resultado", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Método simulando a suspensão de uma chamada de rede (Retrofit faria isso internamente)
     */
    private suspend fun simularChamadaRede(texto: String): String {
        delay(2000) // Simula 2 segundos de latência de rede
        return "HTTP 201: '$texto' enviado com sucesso!"
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        private const val ARG_TEXT = "arg_text"

        @JvmStatic
        fun newInstance(text: String) =
            InputFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TEXT, text)
                }
            }
    }
}