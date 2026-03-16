package com.example.clinica_univirtus

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.clinica_univirtus.databinding.ActivityMainBinding
import com.example.clinica_univirtus.fragments.AgendamentosFragment
import com.example.clinica_univirtus.fragments.AgendarFragment
import com.example.clinica_univirtus.fragments.ContatoFragment
import com.example.clinica_univirtus.fragments.InicioFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNav = binding.menuNavegacao
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        trocarFragmento(InicioFragment())

        binding.menuNavegacao.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_inicio -> trocarFragmento(InicioFragment())
                R.id.item_agendar -> trocarFragmento(AgendarFragment())
                R.id.item_agendamentos -> trocarFragmento(AgendamentosFragment())
                R.id.item_contato -> trocarFragmento(ContatoFragment())
                else -> {
                    trocarFragmento(InicioFragment())
                }
            }
            true
        }
    }

    private fun trocarFragmento(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}