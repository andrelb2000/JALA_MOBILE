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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * MODELO DE DADOS PARA RETROFIT
 */
data class AgifyResponse(
    val name: String,
    val age: Int?,
    val count: Int
)

/**
 * INTERFACE DO RETROFIT PARA AGIFY.IO
 */
interface AgifyService {
    @GET("/")
    suspend fun getAge(@Query("name") name: String): AgifyResponse
}

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
        val etNumberInput = view.findViewById<EditText>(R.id.etNumberInput)
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
        /**
         * EXPLICAÇÃO DIDÁTICA (REATIVIDADE COM FLOW):
         * O DataStore expõe um Flow (fluxo de dados). 
         * Usamos 'collect' para escutar mudanças em tempo real. Como o 'collect' suspende 
         * a execução até que novos dados cheguem, ele deve rodar dentro de uma Coroutine.
         */
        viewLifecycleOwner.lifecycleScope.launch {
            dataStoreManager.listNames.collect { names ->
                // Sempre que o DataStore mudar, esta linha é executada na Main Thread (UI)
                listNamesAdapter.updateData(names.toList())
            }
        }
        /********************************/

        editText.setText(initialText)

        btnDevolver.setOnClickListener {
            val textToReturn = editText.text.toString()
            val listName = etListName.text.toString()

            /**
             * EXPLICAÇÃO DIDÁTICA (ESCRITA ASSÍNCRONA):
             * Gravar no DataStore é uma operação de I/O que pode demorar.
             * Usamos 'launch' para criar uma coroutine e não travar a interface do usuário (UI).
             */
            lifecycleScope.launch {
                dataStoreManager.addListName(listName)
                listener?.onDevolverClicked(textToReturn, listName)
            }
        }

        btnEnviarRest.setOnClickListener {
            val nameToPost = editText.text.toString()
            
            // OPÇÃO 1: OkHttp Puro
           // consultarIdadeAgify(nameToPost, etNumberInput)

            // OPÇÃO 2: Retrofit (Descomente para usar)
            enviarDadosRest3(nameToPost, etNumberInput)
        }

        return view
    }

    /**
     * EXPLICAÇÃO DIDÁTICA - RETROFIT E COROUTINES (enviarDadosRest3):
     * 1. O Retrofit automatiza a criação das chamadas HTTP e o parsing do JSON (usando Gson).
     * 2. 'suspend fun': O Retrofit suporta nativamente Coroutines. Quando marcamos o método 
     *    na interface como 'suspend', o Retrofit faz a chamada em uma thread de background 
     *    e retorna o resultado diretamente para a Main Thread sem bloquear a UI.
     * 3. Menos código manual: Não precisamos lidar com JSONObject ou strings manuais.
     */
    private fun enviarDadosRest3(nome: String, etNumber: EditText) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.agify.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(AgifyService::class.java)

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Toast.makeText(requireContext(), "Consultando (Retrofit)...", Toast.LENGTH_SHORT).show()
                
                // Chamada direta e suspensa. O Retrofit gerencia a thread de IO internamente
                // se o método na interface for 'suspend'.
                val response = service.getAge(nome)
                
                etNumber.setText(response.age?.toString() ?: "N/A")
                Toast.makeText(requireContext(), "Idade (Retrofit): ${response.age}", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Erro Retrofit: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * EXPLICAÇÃO DIDÁTICA - COROUTINES E CHAMADA DE REDE REAL:
     * 1. lifecycleScope.launch: Cria uma coroutine vinculada ao ciclo de vida do Fragment.
     *    VANTAGEM: Se o usuário sair da tela, a chamada de rede é cancelada automaticamente (evita memory leaks).
     * 
     * 2. withContext(Dispatchers.IO): O Android PROÍBE chamadas de rede na "Main Thread".
     *    Esta função "suspende" a coroutine na thread principal e a "move" para uma thread de I/O.
     * 
     * 3. Suspensão: Enquanto esperamos a resposta da rede, a Main Thread fica livre para 
     *    atender o usuário (o app continua fluido).
     */
    private fun consultarIdadeAgify(nome: String, etNumber: EditText) {
        // Iniciamos a Coroutine na Thread Principal para poder manipular Views (Toasts/EditText)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                Toast.makeText(requireContext(), "Consultando Agify.io...", Toast.LENGTH_SHORT).show()

                // 'withContext' faz o "salto" para a thread de background (IO)
                val resultAge = withContext(Dispatchers.IO) {
                    // Aqui dentro podemos fazer operações pesadas ou bloqueantes (como OkHttp .execute())
                    chamadaAgify(nome)
                }

                // Ao sair do withContext, voltamos AUTOMATICAMENTE para a Main Thread
                if (resultAge != null) {
                    // Agora podemos mexer na UI com segurança
                    etNumber.setText(resultAge.toString())
                    Toast.makeText(requireContext(), "Idade estimada: $resultAge", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Não foi possível obter a idade", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // É CRUCIAL tratar erros de rede (falta de internet, servidor offline, etc)
                Toast.makeText(requireContext(), "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Função que realiza a requisição HTTP usando OkHttpClient.
     * Deve ser executada sempre fora da Main Thread.
     */
    private fun chamadaAgify(nome: String): Int? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.agify.io/?name=$nome")
            .build()

        // .execute() é síncrono e bloqueia a thread atual até o servidor responder
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null
            
            val responseData = response.body?.string() ?: return null
            val json = JSONObject(responseData)
            
            // Verificamos se a API retornou um valor válido para a idade
            return if (json.has("age") && !json.isNull("age")) {
                json.getInt("age")
            } else {
                null
            }
        }
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
