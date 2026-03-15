package com.example.clinica_univirtus

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clinica_univirtus.databinding.ActivityCadastroBinding
import com.example.clinica_univirtus.models.Perfil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCadastroBinding.inflate(layoutInflater)

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val auth = FirebaseAuth.getInstance()


        binding.btnCadastro.setOnClickListener {
            val nome = binding.editNome.text.toString()
            val sobrenome = binding.editSobrenome.text.toString()
            val dataNascimento = binding.editDataNascimento.text.toString()
            val telefone = binding.editTextPhone.text.toString()
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenha.text.toString()
            val cpf = binding.editCpf.text.toString()
            val rua = binding.editRua.text.toString()
            val numero = binding.editNumero.text.toString()
            val cidade = binding.editCidade.text.toString()
            val bairro = binding.editBairro.text.toString()
            val uf = binding.editUf.text.toString()
            val cep = binding.editCep.text.toString()
            var cadastroOk = true

            if(nome.isEmpty()){
                cadastroOk = false
                binding.editNome.error = "Digite seu nome"
            }
            if(sobrenome.isEmpty()){
                cadastroOk = false
                binding.editSobrenome.error = "Digite seu sobrenome"
            }
            if(email.isEmpty()){
                cadastroOk = false
                binding.editEmail.error = "Digite seu email"
            }
            if(senha.isEmpty()){
                cadastroOk = false
                binding.editSenha.error = "Digite sua senha"
            }
            if(cpf.isEmpty()){
                cadastroOk = false
                binding.editCpf.error = "Digite seu cpf"
            }


            if(cadastroOk){

                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnSuccessListener { result ->

                        val uid = result.user!!.uid

                        val perfil = Perfil(nome, sobrenome, dataNascimento, telefone, email, cpf, rua, numero, cidade, bairro, uf, cep)

                        salvarPerfil(uid, perfil)

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()

                    }
                    .addOnFailureListener { exception ->
                        println("Erro ao registrar: ${exception.message}")
                    }

            }

        }

    }

    private fun salvarPerfil(uid: String, perfil: Perfil) {

        var dbREF = database.getReference("perfis")
        dbREF.child(uid).setValue(perfil).addOnSuccessListener {
            Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show()
        }

    }

}


