package com.example.clinica_univirtus.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.clinica_univirtus.databinding.FragmentAgendarBinding
import com.example.clinica_univirtus.models.Medico
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinica_univirtus.adapters.HorarioListAdapter


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgendarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgendarFragment : Fragment() {

    private var _binding: FragmentAgendarBinding? = null
    private val binding get() = _binding!!
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("medicos")
    val refAgenda = database.getReference("agendas")
    val listaMedicos = mutableListOf<Medico>()
    val listaDatas = mutableListOf<String>()
    lateinit var idMedicoSelecionado: String
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAgendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEspCardiologia.setOnClickListener {
           buscarMedicos("cardiologia")
            atualizarEstiloBotoes(it as com.google.android.material.button.MaterialButton)
        }
        binding.btnEspClinico.setOnClickListener {
            buscarMedicos("clinico")
            atualizarEstiloBotoes(it as com.google.android.material.button.MaterialButton)
        }
        binding.btnEspOrtopedia.setOnClickListener {
            buscarMedicos("ortopedia")
            atualizarEstiloBotoes(it as com.google.android.material.button.MaterialButton)
        }
        binding.btnEspNutricao.setOnClickListener {
            buscarMedicos("nutricao")
            atualizarEstiloBotoes(it as com.google.android.material.button.MaterialButton)
        }

        binding.autoCompleteMedicos.setOnItemClickListener { parent, _, position, _ ->
            val medicoSelecionado = listaMedicos[position]
            idMedicoSelecionado = medicoSelecionado.uid

            println("Selecionado via Material: UID: $idMedicoSelecionado")
            buscarDatas(idMedicoSelecionado)
        }

        val recycler = binding.recyclerHorarios
        recycler.layoutManager = GridLayoutManager(requireContext(), 3)


        binding.autoCompleteDatas.setOnItemClickListener { parent, _, position, _ ->
            val dataSelecionada = listaDatas[position]
            println("Selecionado via Material: Data: $dataSelecionada")

            buscarHorarios(idMedicoSelecionado, dataSelecionada, recycler)
        }



    }

    private fun buscarMedicos(especialidade: String) {
        ref.child(especialidade).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaMedicos.clear()

                for (medicoSnapshot in snapshot.children) {
                    val possuiAgenda =
                        medicoSnapshot.child("possuiAgenda").getValue(Boolean::class.java)

                    if (possuiAgenda == true) {
                        val uid = medicoSnapshot.key ?: continue
                        val nome = medicoSnapshot.child("nome").getValue(String::class.java)
                        listaMedicos.add(Medico(uid, nome.toString()))
                    }
                }
//
//                for (medico in listaMedicos) {
//                    println("UID: ${medico.uid}, Nome: ${medico.nome}")
//                }
                if (listaMedicos.isEmpty()) {
                    binding.textAgendaIndisponivel.visibility = View.VISIBLE
                    binding.layoutAutocompleteMedicos.visibility = View.GONE
                    binding.textTituloMedicos.visibility = View.GONE
                } else {
                    popularDropdownMedicos()
                    binding.textAgendaIndisponivel.visibility = View.GONE
                    binding.layoutAutocompleteMedicos.visibility = View.VISIBLE
                    binding.textTituloMedicos.visibility = View.VISIBLE
                }

            }
            override fun onCancelled(error: DatabaseError) {
                println("Erro: ${error.message}")
            }
        })
    }

    private fun popularDropdownMedicos() {
        val nomesMedicos = listaMedicos.map { it.nome }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            nomesMedicos
        )

        binding.autoCompleteMedicos.setAdapter(adapter)

        binding.autoCompleteMedicos.setText("", false)
        binding.autoCompleteMedicos.clearFocus()
    }

    private fun buscarDatas(uidMedico: String){

        refAgenda.child(uidMedico).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaDatas.clear()

                for (dataSnapshot in snapshot.children) {
                    val possuiHorarios =
                        dataSnapshot.child("possuiHorarios").getValue(Boolean::class.java)

                    if (possuiHorarios == true) {
                        listaDatas.add(dataSnapshot.key.toString())
                    }
                }

                if (listaDatas.isEmpty()) {
                    binding.layoutAutocompleteDatas.visibility = View.GONE
                    binding.textTituloDatas.visibility = View.GONE
                } else {
                    popularDropdownDatas(listaDatas)
                    binding.layoutAutocompleteDatas.visibility = View.VISIBLE
                    binding.textTituloDatas.visibility = View.VISIBLE
                }

            }
            override fun onCancelled(error: DatabaseError) {
                println("Erro: ${error.message}")
            }
        })
    }

    private fun popularDropdownDatas(listaDatas: List<String>) {

        val formatoEntrada = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val formatoSaida = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

        val datasFormatadas = listaDatas.map { dataString ->
            try {
                val data = formatoEntrada.parse(dataString)
                formatoSaida.format(data!!)
            } catch (e: Exception) {
                dataString
            }
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            datasFormatadas
        )
        binding.autoCompleteDatas.setAdapter(adapter)

        binding.autoCompleteDatas.setText("", false)
        binding.autoCompleteDatas.clearFocus()

    }

    private fun buscarHorarios(idMedico: String, data: String, recyclerView: RecyclerView){
        val listaHorarios = mutableListOf<String>()

        refAgenda.child(idMedico).child(data).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaHorarios.clear()

                for (horarioSnapshot in snapshot.children) {

                    if (horarioSnapshot.key != "possuiHorarios" && horarioSnapshot.value.toString() == "true") {
                        println("Horário: ${horarioSnapshot.key.toString()}")
                        listaHorarios.add(horarioSnapshot.key.toString())
                    }
                }

                if (listaHorarios.isEmpty()) {
                    binding.recyclerHorarios.visibility = View.GONE
                    binding.textTituloHorarios.visibility = View.GONE
                } else {
                    val adapter = HorarioListAdapter(listaHorarios)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    binding.recyclerHorarios.visibility = View.VISIBLE
                    binding.textTituloHorarios.visibility = View.VISIBLE
                }

            }
            override fun onCancelled(error: DatabaseError) {
                println("Erro: ${error.message}")
            }
        })

    }

    private fun mudarVisibilidadeCampos(mostrar: Boolean){
        if(mostrar){
            binding.layoutAutocompleteDatas.visibility = View.VISIBLE
            binding.textTituloDatas.visibility = View.VISIBLE
        }else{
            binding.layoutAutocompleteDatas.visibility = View.GONE
            binding.textTituloDatas.visibility = View.GONE
        }

    }

    private fun atualizarEstiloBotoes(botaoClicado: com.google.android.material.button.MaterialButton) {
        val listaBotoes = listOf(
            binding.btnEspCardiologia,
            binding.btnEspClinico,
            binding.btnEspOrtopedia,
            binding.btnEspNutricao
        )

        listaBotoes.forEach { botao ->
            if (botao == botaoClicado) {
                botao.setBackgroundColor("#D6ECFF".toColorInt())
                botao.elevation = 8f
            } else {
                botao.setBackgroundColor("#FFFFFF".toColorInt())
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AgendarFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgendarFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}