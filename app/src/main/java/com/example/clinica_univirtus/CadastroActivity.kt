package com.example.clinica_univirtus

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clinica_univirtus.databinding.ActivityCadastroBinding
import com.example.clinica_univirtus.models.Paciente
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class CadastroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCadastroBinding
    private val database = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val auth = FirebaseAuth.getInstance()

        // MASCARA PARA TELEFONE
        binding.editTextPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 2) {
                    binding.editTextPhone.append(" ")
                }
                if (s.toString().length == 8) {
                    binding.editTextPhone.append("-")
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // MASCARA PARA DATA DE NASCIMENTO
        val editDataNascimento = binding.editDataNascimento
        editDataNascimento.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }
                var str = s.toString().replace("[^\\d]".toRegex(), "")
                var formatted = ""
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > i) {
                        formatted += m
                        continue
                    }

                    try {
                        formatted += str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }
                isUpdating = true
                editDataNascimento.setText(formatted)
                editDataNascimento.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // VALIDAÇÃO DE EMAIL
        binding.editEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (email.isEmpty()) {
                    binding.editEmailConstraint.error = null
                    return
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.editEmailConstraint.error = "Email inválido"
                } else {
                    binding.editEmailConstraint.error = null
                }
            }
        })

        // MASCARA PARA CEP
        binding.editCep.addTextChangedListener(object : TextWatcher {

            private var isUpdating = false
            private val mask = "#####-###"
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }
                var str = s.toString().replace("[^\\d]".toRegex(), "")
                var formatted = ""
                var i = 0
                for (m in mask.toCharArray()) {

                    if (m != '#' && str.length > i) {
                        formatted += m
                        continue
                    }

                    try {
                        formatted += str[i]
                    } catch (e: Exception) {
                        break
                    }

                    i++
                }
                isUpdating = true
                binding.editCep.setText(formatted)
                binding.editCep.setSelection(formatted.length)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // MASCARA PARA CPF
        binding.editCpf.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "###.###.###-##"
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }
                var str = s.toString().replace("[^\\d]".toRegex(), "")
                var formatted = ""
                var i = 0
                for (m in mask.toCharArray()) {

                    if (m != '#' && str.length > i) {
                        formatted += m
                        continue
                    }

                    try {
                        formatted += str[i]
                    } catch (e: Exception) {
                        break
                    }

                    i++
                }
                isUpdating = true
                binding.editCpf.setText(formatted)
                binding.editCpf.setSelection(formatted.length)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // BOTAO PARA VOLTAR O LOGIN
        binding.txtFacaLogin.setOnClickListener {
            finish()
        }

        // CADASTRAR
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

            if (nome.isEmpty()) {
                cadastroOk = false
                binding.editNome.error = "Digite seu nome"
            }
            if (sobrenome.isEmpty()) {
                cadastroOk = false
                binding.editSobrenome.error = "Digite seu sobrenome"
            }
            if (email.isEmpty()) {
                cadastroOk = false
                binding.editEmail.error = "Digite seu email"
            }
            if (senha.isEmpty()) {
                cadastroOk = false
                binding.editSenha.error = "Digite sua senha"
            }
            if (cpf.isEmpty()) {
                cadastroOk = false
                binding.editCpf.error = "Digite seu cpf"
            }


            if (cadastroOk) {

                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnSuccessListener { result ->

                        val uid = result.user!!.uid

                        val paciente = Paciente(
                            nome,
                            sobrenome,
                            dataNascimento,
                            telefone,
                            email,
                            cpf,
                            rua,
                            numero,
                            cidade,
                            bairro,
                            uf,
                            cep
                        )

                        salvarPerfil(uid, paciente)
                        finish()

                    }
                    .addOnFailureListener { exception ->
                        println("Erro ao registrar: ${exception.message}")
                    }

            }

        }

    }

    private fun salvarPerfil(uid: String, paciente: Paciente) {

        var dbREF = database.getReference("pacientes")
        dbREF.child(uid).child("infos").setValue(paciente).addOnSuccessListener {
            Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("DEBUG", "Cadastro Destroyed")
    }

}


