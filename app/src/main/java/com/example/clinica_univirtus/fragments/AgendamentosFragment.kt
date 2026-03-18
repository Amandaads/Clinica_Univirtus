package com.example.clinica_univirtus.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clinica_univirtus.R
import com.example.clinica_univirtus.adapters.AgendamentoListAdapter
import com.example.clinica_univirtus.databinding.FragmentAgendamentosBinding
import com.example.clinica_univirtus.models.Agendamento
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgendamentosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgendamentosFragment : Fragment() {
    private var _binding: FragmentAgendamentosBinding? = null
    private val binding get() = _binding!!

    private var agendamentosRef: com.google.firebase.database.DatabaseReference? = null
    private var agendamentosListener: ValueEventListener? = null
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // verifica se usuário está logado
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), "Usuário não logado", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAgendamentosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = binding.recyclerAgendamentos
        recycler.layoutManager = LinearLayoutManager(requireContext())
        val lista = mutableListOf<Agendamento>()
        val adapter = AgendamentoListAdapter(
            lista,
            onCancelar = { agendamento ->
                val dialog = AlertDialog.Builder(requireContext())
                    .setTitle("Cancelar agendamento")
                    .setMessage("Tem certeza que deseja cancelar este agendamento?")
                    .setPositiveButton("SIM, CANCELAR") { _, _ ->

                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        val database = FirebaseDatabase.getInstance().reference

                        // 1. Caminho do agendamento do paciente (para deletar)
                        val refAgendamentoPaciente =
                            database.child("pacientes/$uid/agendamentos/${agendamento.uid}")

                        val atualizacoes = hashMapOf<String, Any?>(
                            // Deleta o agendamento do paciente (setar null no Firebase deleta o nó)
                            "pacientes/$uid/agendamentos/${agendamento.uid}" to null,

                            // Libera o horário na agenda do médico
                            "agendas/${agendamento.idMedico}/${agendamento.data}/${agendamento.hora}" to true,

                            // Garante que a data e o médico apareçam como disponíveis
                            "agendas/${agendamento.idMedico}/${agendamento.data}/possuiHorarios" to true,
                            "medicos/${agendamento.idEspecialidade}/${agendamento.idMedico}/possuiAgenda" to true
                        )

                        // 3. Executa tudo em uma única operação de rede
                        database.updateChildren(atualizacoes)
                            .addOnSuccessListener {
                                if (_binding != null) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Cancelamento realizado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Erro ao cancelar",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    .setNegativeButton("Voltar", null)
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            }
        )
        recycler.adapter = adapter

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            agendamentosRef =
                FirebaseDatabase.getInstance().getReference("pacientes/$uid/agendamentos")

            agendamentosListener = object : ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    _binding?.let { binding ->
                        lista.clear()
                        for (agendamentoSnap in snapshot.children) {
                            if (agendamentoSnap.child("concluido").value == true) continue
                            val agendamento = agendamentoSnap.getValue(Agendamento::class.java)
                            agendamento?.let {
                                it.uid = agendamentoSnap.key.toString()
                                lista.add(it)
                            }
                        }

                        if (lista.isEmpty()) {
                            binding.textSemAgendamentos.visibility = View.VISIBLE
                            binding.recyclerAgendamentos.visibility = View.GONE
                        } else {
                            binding.textSemAgendamentos.visibility = View.GONE
                            binding.recyclerAgendamentos.visibility = View.VISIBLE
                            lista.sortWith(compareBy({ it.data }, { it.hora }))
                        }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Erro Firebase: ${error.message}")
                }
            }
            agendamentosRef?.addValueEventListener(agendamentosListener!!)
        }
    }

    override fun onDestroyView() {
        agendamentosListener?.let {
            agendamentosRef?.removeEventListener(it)
        }
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
         * @return A new instance of fragment AgendamentosFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AgendamentosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}