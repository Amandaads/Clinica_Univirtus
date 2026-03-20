package com.example.clinica_univirtus.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.clinica_univirtus.databinding.FragmentAgendarBinding
import com.example.clinica_univirtus.models.Medico
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinica_univirtus.R
import com.example.clinica_univirtus.adapters.HorarioListAdapter
import com.example.clinica_univirtus.models.AgendamentoDto
import com.google.android.material.bottomnavigation.BottomNavigationView


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
    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val uidPaciente: String? = user?.uid
    var contadorDatas : Int = 0
    var contadorHorarios : Int = 0
    val listaMedicos = mutableListOf<Medico>()
    val listaDatas = mutableListOf<String>()
    lateinit var especialidadeSelecionada: String
    lateinit var idEspecialidadeSelecionada: String
    lateinit var medicoSelecionado: String
    lateinit var idMedicoSelecionado: String
    lateinit var dataSelecionada: String
    lateinit var horarioSelecionado: String
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


        configurarCliqueEspecialidade(binding.btnEspCardiologia, "cardiologia")
        configurarCliqueEspecialidade(binding.btnEspClinico, "clinico")
        configurarCliqueEspecialidade(binding.btnEspOrtopedia, "ortopedia")
        configurarCliqueEspecialidade(binding.btnEspNutricao, "nutricao")

        binding.autoCompleteMedicos.setOnItemClickListener { parent, _, position, _ ->
            val medico = listaMedicos[position]
            medicoSelecionado = medico.nome
            idMedicoSelecionado = medico.uid

            println("Selecionado via Material: UID: $medicoSelecionado")
            buscarDatas(idMedicoSelecionado)
        }

        val recycler = binding.recyclerHorarios
        recycler.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.autoCompleteDatas.setOnItemClickListener { parent, _, position, _ ->
            dataSelecionada = listaDatas[position]
            horarioSelecionado = ""

            println("Selecionado via Material: Data: $dataSelecionada")

            buscarHorarios(idMedicoSelecionado, dataSelecionada, recycler)
        }

        binding.btnMarcarAgendamento.setOnClickListener {
            binding.btnMarcarAgendamento.isEnabled = false
            confirmarAgendamento()
        }

    }

    private fun verificarAgendamentoExistente(idEspecialidade: String, callback: (Boolean) -> Unit) {
        val dbREF = database.getReference("pacientes")

        dbREF.child(uidPaciente.toString()).child("agendamentos")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var possuiAgendamento = false

                    for (agendamento in snapshot.children) {
                        val idEsp = agendamento.child("idEspecialidade").value.toString()
                        val concluido = agendamento.child("concluido").value as? Boolean ?: false

                        if (idEsp == idEspecialidade && !concluido) {
                            possuiAgendamento = true
                            break
                        }
                    }
                    callback(possuiAgendamento)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    private fun configurarCliqueEspecialidade(botao: com.google.android.material.button.MaterialButton, especialidade: String) {
        botao.setOnClickListener {
            atualizarBotoes(botao)
            verificarAgendamentoExistente(especialidade) { temAgendamento ->
                if (temAgendamento) {
                    binding.textAgendaIndisponivel.text = "Você já possui um agendamento \n para esta especialidade."
                    mudarVisibilidadeCamposAgendaIndisponivel(false)
                } else {
                    buscarMedicos(especialidade)
                    idEspecialidadeSelecionada = especialidade
                }
            }
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
                if (listaMedicos.isEmpty()) {
                    binding.textAgendaIndisponivel.setText("A especialidade escolhida \n não possui agenda.")
                    mudarVisibilidadeCamposAgendaIndisponivel(false)
                } else {
                    popularDropdownMedicos()
                    mudarVisibilidadeCamposMedicos(true)
                    mudarVisibilidadeCamposDatas(false)
                    mudarVisibilidadeCamposHorarios(false)
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
        contadorDatas = 0
        refAgenda.child(uidMedico).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaDatas.clear()

                for (dataSnapshot in snapshot.children) {
                    val possuiHorarios =
                        dataSnapshot.child("possuiHorarios").getValue(Boolean::class.java)

                    if (possuiHorarios == true) {
                        contadorDatas++
                        listaDatas.add(dataSnapshot.key.toString())
                    }
                }

                if (listaDatas.isEmpty()) {
                    mudarVisibilidadeCamposDatas(false)
                } else {
                    popularDropdownDatas(listaDatas)
                    mudarVisibilidadeCamposDatas(true)
                    mudarVisibilidadeCamposHorarios(false)
                    binding.btnMarcarAgendamento.isEnabled = false
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
        contadorHorarios = 0

        refAgenda.child(idMedico).child(data).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaHorarios.clear()

                for (horarioSnapshot in snapshot.children) {

                    if (horarioSnapshot.key != "possuiHorarios" && horarioSnapshot.value.toString() == "true") {
                        println("Horário: ${horarioSnapshot.key.toString()}")
                        contadorHorarios++
                        listaHorarios.add(horarioSnapshot.key.toString())
                    }
                }

                if (listaHorarios.isEmpty()) {
                    mudarVisibilidadeCamposHorarios(false)
                    binding.btnMarcarAgendamento.isEnabled = false
                } else {
                    val adapter = HorarioListAdapter(listaHorarios, onClick =  { horario ->
                        horarioSelecionado = horario
                        println("Horário selecionado: $horario")
                        binding.btnMarcarAgendamento.isEnabled = true
                    })
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    mudarVisibilidadeCamposHorarios(true)
                }

            }
            override fun onCancelled(error: DatabaseError) {
                println("Erro: ${error.message}")
            }
        })

    }

    private fun mudarVisibilidadeCamposAgendaIndisponivel(mostrar: Boolean){
        mudarVisibilidadeCamposMedicos(mostrar)
        mudarVisibilidadeCamposDatas(mostrar)
        mudarVisibilidadeCamposHorarios(mostrar)

        if(!mostrar) {
            binding.textAgendaIndisponivel.visibility = View.VISIBLE
            binding.btnMarcarAgendamento.visibility = View.GONE
        }
    }

    private fun mudarVisibilidadeCamposMedicos(mostrar: Boolean){
        val visibilidade = if (mostrar) View.VISIBLE else View.GONE
        binding.textTituloMedicos.visibility = visibilidade
        binding.layoutAutocompleteMedicos.visibility = visibilidade
        binding.textAgendaIndisponivel.visibility = View.GONE
    }

    private fun mudarVisibilidadeCamposDatas(mostrar: Boolean){
        val visibilidade = if (mostrar) View.VISIBLE else View.GONE
        binding.textTituloDatas.visibility = visibilidade
        binding.layoutAutocompleteDatas.visibility = visibilidade
    }

    private fun mudarVisibilidadeCamposHorarios(mostrar: Boolean){
        val visibilidade = if (mostrar) View.VISIBLE else View.GONE
        binding.textTituloHorarios.visibility = visibilidade
        binding.recyclerHorarios.visibility = visibilidade
        binding.btnMarcarAgendamento.visibility = visibilidade
    }


    private fun atualizarBotoes(botaoClicado: com.google.android.material.button.MaterialButton) {
        val textoBotao = botaoClicado.text.toString()
        especialidadeSelecionada = textoBotao

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

    private fun confirmarAgendamento(){

        if(horarioSelecionado.isEmpty()){
            Toast.makeText(requireContext(), "Selecione um horário", Toast.LENGTH_SHORT).show()
            binding.btnMarcarAgendamento.isEnabled = false
            return
        }

        val agendamento = AgendamentoDto(
            data = dataSelecionada,
            hora = horarioSelecionado,
            idEspecialidade = idEspecialidadeSelecionada,
            especialidade = especialidadeSelecionada,
            idMedico = idMedicoSelecionado,
            medico = medicoSelecionado,
            concluido = false
        )

        var dbREF = database.getReference("pacientes")

        val novoAgendamentoRef = dbREF
            .child(uidPaciente.toString())
            .child("agendamentos")
            .push()

        novoAgendamentoRef.setValue(agendamento)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Agendamento realizado com sucesso", Toast.LENGTH_SHORT).show()

                atualizarAgenda()

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao agendar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun atualizarAgenda() {

        Log.d("Agenda", "Contador de Datas $contadorDatas")
        Log.d("Agenda", "Contador de Horários $contadorHorarios")

        val refData = refAgenda
            .child(idMedicoSelecionado)
            .child(dataSelecionada)

        val refMedico = ref
            .child(idEspecialidadeSelecionada)
            .child(idMedicoSelecionado)

        refData.child(horarioSelecionado)
            .setValue(false)
            .addOnSuccessListener {

                if (contadorHorarios == 1) {
                    refData.child("possuiHorarios")
                        .setValue(false)
                        .addOnSuccessListener {

                            if (contadorDatas == 1) {
                                refMedico.child("possuiAgenda")
                                    .setValue(false)
                            }
                        }
                }

                val bottomNav = requireActivity()
                    .findViewById<BottomNavigationView>(R.id.menu_navegacao)

                bottomNav.selectedItemId = R.id.item_agendamentos
            }
            .addOnFailureListener {
                Log.e("Agenda", "Erro ao atualizar agenda", it)
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
         *         * @param param1 Parameter 1.
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