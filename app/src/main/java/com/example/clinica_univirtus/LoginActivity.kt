package com.example.clinica_univirtus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clinica_univirtus.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botaoEntrar = binding.btnEntrar
        val auth: FirebaseAuth = FirebaseAuth.getInstance()

        // Shared Preferences para persistir email/senha (fins didáticos)
        val sharedPreferences = this.getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val emailPrefs = sharedPreferences.getString("email", "")
        val senhaPrefs = sharedPreferences.getString("senha", "")

        if (!emailPrefs.isNullOrEmpty() && !senhaPrefs.isNullOrEmpty()) {
            binding.editEmail.setText(emailPrefs)
            binding.editSenhaLogin.setText(senhaPrefs)
            binding.checkBoxManterConectado.isChecked = true
        }

        botaoEntrar.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val senha = binding.editSenhaLogin.text.toString()

            auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener {
                    if (binding.checkBoxManterConectado.isChecked) {
                        sharedPreferences.edit {
                            putString("email", email)
                            putString("senha", senha)
                        }
                    } else {
                        sharedPreferences.edit {
                            remove("email")
                            remove("senha")
                        }
                    }
                    Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.putExtra("uid", auth.currentUser?.uid)
                    startActivity(intent)
                    binding.editEmail.text?.clear()
                    binding.editSenhaLogin.text?.clear()
                    binding.checkBoxManterConectado.isChecked = false
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
                }
        }

        val botaoCadastro = binding.txtCadastroLogin
        botaoCadastro.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        val botaoEsqueciSenha = binding.txtEsqueciSenha
        botaoEsqueciSenha.setOnClickListener {
            Toast.makeText(this, "Função ainda não implementada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DEBUG", "Login Resume")

        val sharedPreferences = this.getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val emailPrefs = sharedPreferences.getString("email", "")
        val senhaPrefs = sharedPreferences.getString("senha", "")

        if (!emailPrefs.isNullOrEmpty() && !senhaPrefs.isNullOrEmpty()) {
            binding.editEmail.setText(emailPrefs)
            binding.editSenhaLogin.setText(senhaPrefs)
            binding.checkBoxManterConectado.isChecked = true
        }
    }
}