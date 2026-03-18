package com.example.clinica_univirtus.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import com.example.clinica_univirtus.databinding.FragmentInicioBinding
import com.google.firebase.database.FirebaseDatabase


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InicioFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InicioFragment : Fragment() {

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!

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
        // Inflate the layout for this fragment
        _binding = FragmentInicioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("pacientes")
        val uid = requireActivity().intent.getStringExtra("uid")


        ref.child(uid!!).get()
            .addOnSuccessListener { snapshot ->

                val nome = snapshot.child("nome").value
                val sobrenome = snapshot.child("sobrenome").value

                binding.txtNomePaciente.text = "$nome $sobrenome"
                binding.txtPaciente.text = "Paciente"

            }
            .addOnFailureListener {
                println("Erro ao buscar usuário")
            }

        binding.buttonSair.setOnClickListener {
            // Apagar o SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("loginPrefs", MODE_PRIVATE)
            sharedPreferences.edit {
                remove("email")
                remove("senha")
            }
            requireActivity().finish()
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
         * @return A new instance of fragment InicioFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InicioFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}