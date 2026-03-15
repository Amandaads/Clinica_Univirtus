package com.example.clinica_univirtus

import android.R.attr.name
import android.R.id.input
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clinica_univirtus.databinding.ActivityHomeBinding
import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("perfis")
        val uid = intent.getStringExtra("uid")

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
            val i = Intent(this, LoginActivity::class.java)
            i.putExtra("sair", true)
            startActivity(i)
            finish()
        }




    }
}